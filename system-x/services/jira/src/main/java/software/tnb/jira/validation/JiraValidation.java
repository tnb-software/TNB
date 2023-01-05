package software.tnb.jira.validation;

import software.tnb.jira.account.JiraAccount;
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

public class JiraValidation {
    private static final Logger LOG = LoggerFactory.getLogger(JiraValidation.class);

    private final ApiClient client;
    private final JiraAccount account;

    private final IssuesApi issuesApi;
    private final IssueCommentsApi issueCommentsApi;
    private final IssueSearchApi issueSearchApi;

    public JiraValidation(ApiClient client, JiraAccount account) {
        this.client = client;
        this.account = account;
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

            JSONObject requestBody = new JSONObject()
                .put("fields", new JSONObject()
                    .put("summary", issueSummary)
                    .put("issuetype", new JSONObject()
                        .put("id", issueTypeId))
                    .put("project", new JSONObject()
                        .put("id", projectId))
                );
            CreatedIssue response = issuesApi.createIssue(requestBody.toMap(), null);
            return response.getKey();
        } catch (ApiException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteIssue(String issueKey) {
        LOG.debug("Deleting issue " + issueKey);
        try {
            issuesApi.deleteIssue(issueKey, null);
        } catch (ApiException e) {
            throw new RuntimeException(e);
        }
    }

    public List<String> getComments(String issueKey) {
        LOG.debug("Getting comments of " + issueKey);
        try {
            return issueCommentsApi.getComments(issueKey, null, null, null, null).getComments().stream()
                .map(Comment::getBody)
                .map(this::getTextFromADF)
                .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Failed to get comments for " + issueKey, e);
        }
    }

    public boolean findInComments(String issueKey, String message) {
        return getComments(issueKey).contains(message);
    }

    public Issue getIssue(String issueKey) {
        try {
            return convertIssueBeanToIssue(issuesApi.getIssue(issueKey, null, null, null, null, null));
        } catch (ApiException e) {
            throw new RuntimeException(e);
        }
    }

    public void addComment(String issueKey, String comment) {
        LOG.debug("Adding comment on issue " + issueKey);
        try {
            JSONObject requestBody = new JSONObject()
                .put("body", new JSONObject()
                    .put("type", "doc")
                    .put("version", 1)
                    .put("content", new JSONArray().put(
                            new JSONObject().put("type", "paragraph").put("content",
                                new JSONArray().put(
                                    new JSONObject().put("type", "text").put("text", comment)
                                )
                            )
                        )
                    ));
            issueCommentsApi.addComment(issueKey, requestBody.toMap(), null);
        } catch (Exception e) {
            throw new RuntimeException("Failed to add comment on issue " + issueKey, e);
        }
    }

    public Iterable<Issue> getIssues(String project) {
        assert StringUtils.isNotEmpty(project);
        try {
            return issueSearchApi.searchForIssuesUsingJql(String.format("project = \"%s\"", "FUQT"), null, 200, null, null, null, null, null)
                .getIssues().stream()
                .map(this::convertIssueBeanToIssue)
                .collect(Collectors.toList());
        } catch (ApiException e) {
            throw new RuntimeException(e);
        }
    }

    public Iterable<Issue> getIssues(String project, String customJQL) {
        assert StringUtils.isNotEmpty(project);
        assert StringUtils.isNotEmpty(customJQL);
        try {
            return issueSearchApi.searchForIssuesUsingJql(String.format("project = \"%s\" AND %s", project, customJQL), null, 200, null, null, null,
                    null, null)
                .getIssues().stream()
                .map(this::convertIssueBeanToIssue)
                .collect(Collectors.toList());
        } catch (ApiException e) {
            throw new RuntimeException(e);
        }
    }

    public void setTransition(String issueKey, int transitionId) {
        LOG.debug("Transit issue " + issueKey + " - transition id: " + transitionId);
        try {
            JSONObject requestBody = new JSONObject().put("transition", new JSONObject().put("id", transitionId));
            issuesApi.doTransition(issueKey, requestBody.toMap());
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
     * https://developer.atlassian.com/cloud/jira/platform/apis/document/structure/
     * The object is represented by tree data structure (combination of ArrayList and LinkedTreeMap). E.g.:
     *
     *         N           LinkedTreeMap          ADF format root, type==doc
     *        / \
     *       /   \
     *      /     \
     *     N       N       ArrayList of LinkedTreeMap  type== paragraph || heading || codeBlock ...
     *    / \     / \
     *   L   L   L   N     ArrayList of LinkedTreeMap  type == paragraph || heading || codeBlock ... for NODES, text || status || emoji ... for LEAFS
     *                \
     *                L    ArrayList of LinkedTreeMap  type == text || status || emoji ... for LEAFS
     *
     * Method is called recursively on each node till it finds the leaf where is stored the text (type==text) or (type==status || type==emoji)
     *
     * @param node the whole ADF (Atlassian Document Format) object
     * @return All text from the document as a string formatted according to the lines (does not apply to the table where each cell is on new line)
     */
    //@formatter:on
    private String getTextFromADF(Object node) {
        if (node instanceof ArrayList) {
            // list of all child nodes, collect String from all of them and then, concat it into one string
            ArrayList<?> castedNode = (ArrayList<?>) node;
            List<String> collect = castedNode.stream()
                .map(this::getTextFromADF).collect(Collectors.toList());
            return collect.stream()
                .map(Object::toString)
                .collect(Collectors.joining(""));
        } else if (node instanceof LinkedTreeMap) {
            // node, decide if it is the parent node(contains arraylist of child nodes) or leaf node where is the text
            LinkedTreeMap<?, ?> castedNode = (LinkedTreeMap<?, ?>) node;

            String nodeType = (String) castedNode.get("type");
            switch (nodeType) {
                // root node, recursively call a method and remove trailing /n from recursion
                case "doc":
                    return getTextFromADF(castedNode.get("content")).trim();

                // we are in the parent node (not in the leaf), continue with recursion
                case "blockquote":
                case "bulletList":
                case "mediaGroup":
                case "mediaSingle":
                case "orderedList":
                case "listItem":
                case "tableCell":
                case "tableHeader":
                case "tableRow":
                case "panel":
                case "table":
                    return getTextFromADF(castedNode.get("content"));

                // these parent nodes represent line in the raw text, add \n to have result text formatted according to the lines (does not apply
                // to the table)
                case "codeBlock":
                case "heading":
                case "paragraph":
                    return getTextFromADF(castedNode.get("content")) + "\n";

                // in the text node (leaf), return text
                case "text":
                    return (String) castedNode.get("text");

                // inlineCard node (leaf) has url(text) saved in attributes map
                case "inlineCard":
                    return (String) ((LinkedTreeMap<?, ?>) castedNode.get("attrs")).get("url");

                // status and emoji node (leaf) has text saved in attributes map
                case "status":
                case "emoji":
                case "mention":
                    return (String) ((LinkedTreeMap<?, ?>) castedNode.get("attrs")).get("text");

                // no text in this type of leaf
                case "media":
                case "rule":
                    return "";

                default:
                    throw new IllegalStateException("getTextFromADF doesn't know this type of ADF node '" + nodeType
                        + "'! Please update the switch above according to this type of node.");
            }
        } else {
            throw new IllegalStateException("Not supported instance of node in ADF! Instance: " + node.getClass().getTypeName());
        }
    }
}
