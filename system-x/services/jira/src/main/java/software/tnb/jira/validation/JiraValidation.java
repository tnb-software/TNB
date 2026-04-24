package software.tnb.jira.validation;

import software.tnb.common.utils.HTTPUtils;
import software.tnb.common.validation.Validation;
import software.tnb.jira.account.JiraAccount;
import software.tnb.jira.validation.model.Issue;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.Credentials;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class JiraValidation implements Validation {
    private static final Logger LOG = LoggerFactory.getLogger(JiraValidation.class);
    private static final MediaType JSON = MediaType.get("application/json");
    private static final String API_PATH = "/rest/api/3";
    private static final int CLOSE_ISSUE_TRANSITION_ID = 2;

    private final JiraAccount account;
    private final HTTPUtils httpUtils;
    private final Map<String, String> headers;

    public JiraValidation(JiraAccount account) {
        this.account = account;
        this.httpUtils = HTTPUtils.getInstance();
        this.headers = Map.of(
            "Authorization", Credentials.basic(account.getUsername(), account.getPassword()),
            "Content-Type", "application/json",
            "Accept", "application/json"
        );
    }

    private String apiUrl(String path) {
        return account.getJiraUrl() + API_PATH + path;
    }

    /**
     * Create issue in given project.
     *
     * @param projectKey key of project where issue will be created
     * @param issueSummary name of issue to be created
     * @return key of created issue
     */
    public String createIssue(String projectKey, String issueSummary) {
        JSONObject body = new JSONObject()
            .put("fields", new JSONObject()
                .put("project", new JSONObject().put("key", projectKey))
                .put("summary", issueSummary)
                .put("issuetype", new JSONObject().put("name", "Bug")));

        HTTPUtils.Response response = httpUtils.post(
            apiUrl("/issue"),
            RequestBody.create(body.toString(), JSON),
            headers
        );

        if (!response.isSuccessful()) {
            throw new RuntimeException("Unable to create issue: " + response.getBody());
        }

        String key = new JSONObject(response.getBody()).getString("key");
        LOG.debug("Created a new issue with key {}", key);
        return key;
    }

    public boolean deleteIssue(String issueKey) {
        LOG.debug("Deleting issue {}", issueKey);
        HTTPUtils.Response response = httpUtils.delete(apiUrl("/issue/" + issueKey + "?deleteSubtasks=true"), headers);
        if (!response.isSuccessful()) {
            LOG.warn("Failed to delete issue {}: {}", issueKey, response.getBody());
            return false;
        }
        return true;
    }

    public void closeIssue(String issueKey) {
        LOG.debug("Closing issue {}", issueKey);
        try {
            setTransition(issueKey, CLOSE_ISSUE_TRANSITION_ID);
        } catch (RuntimeException e) {
            LOG.warn("Failed to close issue {}: {}", issueKey, e.getMessage());
        }
    }

    public List<String> getComments(String issueKey) {
        LOG.debug("Getting comments of {}", issueKey);
        HTTPUtils.Response response = httpUtils.get(apiUrl("/issue/" + issueKey + "/comment"), headers);

        if (!response.isSuccessful()) {
            throw new RuntimeException("Unable to get comments for issue " + issueKey + ": " + response.getBody());
        }

        JSONArray comments = new JSONObject(response.getBody()).getJSONArray("comments");
        List<String> result = new ArrayList<>();
        for (int i = 0; i < comments.length(); i++) {
            JSONObject commentBody = comments.getJSONObject(i).optJSONObject("body");
            if (commentBody != null) {
                result.add(extractTextFromAdf(commentBody));
            } else {
                result.add(comments.getJSONObject(i).optString("body", ""));
            }
        }
        return result;
    }

    public Issue getIssue(String issueKey) {
        LOG.debug("Getting issue {}", issueKey);
        HTTPUtils.Response response = httpUtils.get(apiUrl("/issue/" + issueKey), headers);

        if (!response.isSuccessful()) {
            throw new RuntimeException("Unable to get issue " + issueKey + ": " + response.getBody());
        }

        return convertToIssue(new JSONObject(response.getBody()));
    }

    public void addComment(String issueKey, String content) {
        LOG.debug("Adding comment {} to issue {}", content, issueKey);
        JSONObject body = new JSONObject()
            .put("body", new JSONObject()
                .put("type", "doc")
                .put("version", 1)
                .put("content", new JSONArray()
                    .put(new JSONObject()
                        .put("type", "paragraph")
                        .put("content", new JSONArray()
                            .put(new JSONObject()
                                .put("type", "text")
                                .put("text", content))))));

        HTTPUtils.Response response = httpUtils.post(
            apiUrl("/issue/" + issueKey + "/comment"),
            RequestBody.create(body.toString(), JSON),
            headers
        );

        if (!response.isSuccessful()) {
            throw new RuntimeException("Unable to add comment to issue " + issueKey + ": " + response.getBody());
        }
    }

    public List<Issue> getIssues(String jql) {
        String encodedJql = java.net.URLEncoder.encode(jql, java.nio.charset.StandardCharsets.UTF_8);
        HTTPUtils.Response response = httpUtils.get(
            apiUrl("/search/jql?jql=" + encodedJql + "&fields=key,summary,description,issuetype,priority,status,project,attachment"),
            headers
        );

        if (!response.isSuccessful()) {
            throw new RuntimeException("Unable to search issues: " + response.getBody());
        }

        JSONArray issues = new JSONObject(response.getBody()).getJSONArray("issues");
        List<Issue> result = new ArrayList<>();
        for (int i = 0; i < issues.length(); i++) {
            result.add(convertToIssue(issues.getJSONObject(i)));
        }
        return result;
    }

    public List<Issue> getIssues(String project, String customJQL) {
        return getIssues(String.format("project = \"%s\" AND %s", project, customJQL));
    }

    public Map<String, String> getTransitions(String issueKey) {
        HTTPUtils.Response response = httpUtils.get(apiUrl("/issue/" + issueKey + "/transitions"), headers);

        if (!response.isSuccessful()) {
            throw new RuntimeException("Unable to get transitions for issue " + issueKey + ": " + response.getBody());
        }

        JSONArray transitions = new JSONObject(response.getBody()).getJSONArray("transitions");
        Map<String, String> result = new java.util.LinkedHashMap<>();
        for (int i = 0; i < transitions.length(); i++) {
            JSONObject t = transitions.getJSONObject(i);
            result.put(t.getString("id"), t.getString("name"));
        }
        return result;
    }

    public void setTransition(String issueKey, int transitionId) {
        LOG.debug("Transition issue {} - transition id: {}", issueKey, transitionId);
        JSONObject body = new JSONObject()
            .put("transition", new JSONObject().put("id", String.valueOf(transitionId)));

        HTTPUtils.Response response = httpUtils.post(
            apiUrl("/issue/" + issueKey + "/transitions"),
            RequestBody.create(body.toString(), JSON),
            headers
        );

        if (!response.isSuccessful()) {
            throw new RuntimeException("Unable to transition issue " + issueKey + ": " + response.getBody());
        }
    }

    private Issue convertToIssue(JSONObject json) {
        Issue result = new Issue();
        result.setKey(json.getString("key"));

        JSONObject fields = json.getJSONObject("fields");
        result.setSummary(fields.optString("summary", ""));
        result.setDescription(fields.isNull("description") ? "" : extractTextFromAdf(fields.optJSONObject("description")));
        result.setType(fields.optJSONObject("issuetype") != null ? fields.getJSONObject("issuetype").optString("name", "") : "");
        result.setPriority(fields.optJSONObject("priority") != null ? fields.getJSONObject("priority").optString("name", "") : "");
        result.setStatus(fields.optJSONObject("status") != null ? fields.getJSONObject("status").optString("name", "") : "");
        result.setProjectKey(fields.optJSONObject("project") != null ? fields.getJSONObject("project").optString("key", "") : "");

        JSONArray attachments = fields.optJSONArray("attachment");
        if (attachments != null) {
            List<String> attachmentNames = new ArrayList<>();
            for (int i = 0; i < attachments.length(); i++) {
                attachmentNames.add(attachments.getJSONObject(i).optString("filename", ""));
            }
            result.setAttachmentsIds(attachmentNames);
        } else {
            result.setAttachmentsIds(List.of());
        }

        return result;
    }

    private String extractTextFromAdf(JSONObject adf) {
        if (adf == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        JSONArray content = adf.optJSONArray("content");
        if (content != null) {
            for (int i = 0; i < content.length(); i++) {
                JSONObject node = content.getJSONObject(i);
                if ("text".equals(node.optString("type"))) {
                    sb.append(node.optString("text", ""));
                } else {
                    sb.append(extractTextFromAdf(node));
                }
            }
        }
        if ("text".equals(adf.optString("type"))) {
            sb.append(adf.optString("text", ""));
        }
        return sb.toString();
    }
}
