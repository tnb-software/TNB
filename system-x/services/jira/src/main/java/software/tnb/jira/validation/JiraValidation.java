package software.tnb.jira.validation;

import software.tnb.common.validation.Validation;
import software.tnb.jira.validation.generated.ApiClient;
import software.tnb.jira.validation.generated.ApiException;
import software.tnb.jira.validation.generated.api.IssueCommentsApi;
import software.tnb.jira.validation.generated.api.IssueSearchApi;
import software.tnb.jira.validation.generated.api.IssueTypesApi;
import software.tnb.jira.validation.generated.api.IssuesApi;
import software.tnb.jira.validation.generated.api.ProjectsApi;
import software.tnb.jira.validation.generated.model.Comment;
import software.tnb.jira.validation.generated.model.CreatedIssue;
import software.tnb.jira.validation.generated.model.IssueBean;
import software.tnb.jira.validation.generated.model.IssueTransition;
import software.tnb.jira.validation.generated.model.IssueUpdateDetails;
import software.tnb.jira.validation.model.Issue;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.internal.LinkedTreeMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class JiraValidation implements Validation {
    private static final Logger LOG = LoggerFactory.getLogger(JiraValidation.class);

    private final ApiClient client;

    private final IssuesApi issuesApi;
    private final IssueCommentsApi issueCommentsApi;
    private final IssueSearchApi issueSearchApi;

    public JiraValidation(ApiClient client) {
        this.client = client;
        this.issuesApi = new IssuesApi(client);
        this.issueCommentsApi = new IssueCommentsApi(client);
        this.issueSearchApi = new IssueSearchApi(client);
    }

    /**
     * Create issue in given project.
     *
     * @param projectKey key of project where issue will be created
     * @param issueSummary name of issue to be created
     * @return id of created issue
     */
    public String createIssue(String projectKey, String issueSummary) {
        try {
            String projectId = Objects.requireNonNull(new ProjectsApi(client).getProject(projectKey, null, null).getId());
            String issueTypeId = new IssueTypesApi(client).getIssueTypesForProject(Long.valueOf(projectId), null).stream()
                .filter(it -> "Bug".equals(it.getName())).findFirst().get().getId();

            Map<String, Object> fields = Map.of(
                "summary", issueSummary,
                "issuetype", Map.of(
                    "id", issueTypeId
                ),
                "project", Map.of(
                    "id", projectId
                )
            );
            CreatedIssue response = issuesApi.createIssue(new IssueUpdateDetails().fields(fields), null);
            return response.getKey();
        } catch (ApiException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteIssue(String issueKey) {
        LOG.debug("Deleting issue {}", issueKey);
        try {
            issuesApi.deleteIssue(issueKey, null);
        } catch (ApiException e) {
            throw new RuntimeException(e);
        }
    }

    public List<String> getComments(String issueKey) {
        LOG.debug("Getting comments of {}", issueKey);
        try {
            return issueCommentsApi.getComments(issueKey, null, null, null, null).getComments().stream()
                .map(Comment::getBody)
                .map(this::getTextFromADF)
                .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Failed to get comments for " + issueKey, e);
        }
    }

    public Issue getIssue(String issueKey) {
        try {
            return convertIssueBeanToIssue(issuesApi.getIssue(issueKey, List.of("*all"), null, null, null, null, null));
        } catch (ApiException e) {
            throw new RuntimeException(e);
        }
    }

    public void addComment(String issueKey, String content) {
        LOG.debug("Adding comment on issue {}", issueKey);
        try {
            Map<String, Object> body = new JSONObject()
                .put("type", "doc")
                .put("version", 1)
                .put("content", new JSONArray().put(
                    new JSONObject().put("type", "paragraph").put("content",
                        new JSONArray().put(
                            new JSONObject().put("type", "text").put("text", content)
                        )
                    )
                )
                ).toMap();
            issueCommentsApi.addComment(issueKey, new Comment().body(body), null);
        } catch (Exception e) {
            throw new RuntimeException("Failed to add comment on issue " + issueKey, e);
        }
    }

    public List<Issue> getIssues(String jql) {
        try {
            return issueSearchApi.searchAndReconsileIssuesUsingJql(jql, null, 200, List.of("*all"), null, null, null, null, null)
                .getIssues().stream()
                .map(this::convertIssueBeanToIssue)
                .collect(Collectors.toList());
        } catch (ApiException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Issue> getIssues(String project, String customJQL) {
        assert StringUtils.isNotEmpty(project);
        assert StringUtils.isNotEmpty(customJQL);
        return getIssues(String.format("project = \"%s\" AND %s", project, customJQL));
    }

    public void setTransition(String issueKey, int transitionId) {
        LOG.debug("Transit issue {} - transition id: {}", issueKey, transitionId);
        try {
            issuesApi.doTransition(issueKey, new IssueUpdateDetails().transition(new IssueTransition().id(transitionId + "")));
        } catch (Exception e) {
            throw new RuntimeException("Failed to transit issue " + issueKey, e);
        }
    }

    private Issue convertIssueBeanToIssue(IssueBean sourceIssue) {
        Issue result = new Issue();
        result.setKey(sourceIssue.getKey());

        Map<String, Object> issueFields = sourceIssue.getFields();
        if (issueFields != null) {
            result.setSummary(issueFields.get("summary") != null ? issueFields.get("summary").toString() : null);
            result.setDescription(issueFields.get("description") != null ? getTextFromADF(issueFields.get("description")) : null);
            result.setProjectKey(((LinkedTreeMap<?, ?>) issueFields.get("project")).get("key").toString());
            result.setType(((LinkedTreeMap<?, ?>) issueFields.get("issuetype")).get("name").toString());
            result.setPriority(((LinkedTreeMap<?, ?>) issueFields.get("priority")).get("name").toString());
            result.setStatus(((LinkedTreeMap<?, ?>) issueFields.get("status")).get("name").toString());

            List<?> attachments = (List<?>) issueFields.get("attachment");
            if (attachments != null) {
                result.setAttachmentsIds(((List<?>) issueFields.get("attachment")).stream()
                    .map(it -> (String) ((LinkedTreeMap<?, ?>) it).get("id"))
                    .collect(Collectors.toList()));
            }
        }
        return result;
    }

    //@formatter:off
    /**
     * Method which gets all text from ADF (Atlassian Document Format) object.
     * @see <a href="https://developer.atlassian.com/cloud/jira/platform/apis/document/structure/">Structure</a>
     * The object is represented by tree data structure (combination of ArrayList and LinkedTreeMap). E.g.:
     * <p>
     *         N           LinkedTreeMap          ADF format root, type==doc
     *        / \
     *       /   \
     *      /     \
     *     N       N       ArrayList of LinkedTreeMap  type== paragraph || heading || codeBlock ...
     *    / \     / \
     *   L   L   L   N     ArrayList of LinkedTreeMap  type == paragraph || heading || codeBlock ... for NODES, text || status || emoji ... for LEAFS
     *                \
     *                L    ArrayList of LinkedTreeMap  type == text || status || emoji ... for LEAFS
     * <p>
     * Method is called recursively on each node till it finds the leaf where is stored the text (type==text) or (type==status || type==emoji)
     *
     * @param node the whole ADF (Atlassian Document Format) object
     * @return All text from the document as a string formatted according to the lines (does not apply to the table where each cell is on new line)
     */
    //@formatter:on
    private String getTextFromADF(Object node) {
        if (node instanceof ArrayList<?> castedNode) {
            // list of all child nodes, collect String from all of them and then, concat it into one string
            List<String> collect = castedNode.stream().map(this::getTextFromADF).toList();
            return collect.stream().map(Object::toString).collect(Collectors.joining(""));
        } else if (node instanceof LinkedTreeMap<?, ?> castedNode) {
            // node, decide if it is the parent node(contains arraylist of child nodes) or leaf node where is the text

            String nodeType = (String) castedNode.get("type");
            return switch (nodeType) {
                // root node, recursively call a method and remove trailing /n from recursion
                case "doc" -> getTextFromADF(castedNode.get("content")).trim();

                // we are in the parent node (not in the leaf), continue with recursion
                case "blockquote", "bulletList", "mediaGroup", "mediaSingle", "orderedList", "listItem", "tableCell", "tableHeader", "tableRow",
                     "panel", "table" -> getTextFromADF(castedNode.get("content"));

                // these parent nodes represent line in the raw text, add \n to have result text formatted according to the lines (does not apply
                // to the table)
                case "codeBlock", "heading", "paragraph" -> getTextFromADF(castedNode.get("content")) + "\n";

                // in the text node (leaf), return text
                case "text" -> (String) castedNode.get("text");

                // inlineCard node (leaf) has url(text) saved in attributes map
                case "inlineCard" -> (String) ((LinkedTreeMap<?, ?>) castedNode.get("attrs")).get("url");

                // status and emoji node (leaf) has text saved in attributes map
                case "status", "emoji", "mention" -> (String) ((LinkedTreeMap<?, ?>) castedNode.get("attrs")).get("text");

                // no text in this type of leaf
                case "media", "rule" -> "";
                default -> throw new IllegalStateException("getTextFromADF doesn't know this type of ADF node '" + nodeType
                    + "'! Please update the switch above according to this type of node.");
            };
        } else {
            throw new IllegalStateException("Not supported instance of node in ADF! Instance: " + node.getClass().getTypeName());
        }
    }
}
