package org.jboss.fuse.tnb.jira.validation;

import org.jboss.fuse.tnb.common.utils.HTTPUtils;
import org.jboss.fuse.tnb.jira.account.JiraAccount;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.Comment;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import okhttp3.Credentials;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class JiraValidation {
    private static final Logger LOG = LoggerFactory.getLogger(JiraValidation.class);

    private final JiraRestClient client;
    private final JiraAccount account;

    public JiraValidation(JiraRestClient client, JiraAccount account) {
        this.client = client;
        this.account = account;
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
            Map<String, Object> fields = new HashMap<>();
            fields.put("project", Map.of("key", projectKey));
            fields.put("summary", issueSummary);
            fields.put("issuetype", Map.of("id", "1"));
            Map<String, Object> inputMap = Map.of("fields", fields);
            String input = new ObjectMapper().writeValueAsString(inputMap);
            RequestBody body = RequestBody.create(MediaType.parse("application/json"), input);

            //added multiple attempts due to FUSEQE-12463
            String issueJson = null;
            boolean unsuccessful = true;
            for (int i = 0; i < 2 && unsuccessful; i++) {
                try {
                    issueJson = HTTPUtils.get().post(account.getJiraUrl() + "/rest/api/2/issue", body,
                        Map.of("Authorization", Credentials.basic(account.getUsername(), account.getPassword()))).getBody();
                    unsuccessful = false;
                } catch (Exception e) {
                    LOG.debug(i + ". attempt to create issue failed");
                }
            }

            LOG.debug("Created issue: " + issueJson);
            return new ObjectMapper().readValue(issueJson, Map.class).get("key").toString();
        } catch (IOException e) {
            throw new RuntimeException("Failed to create issue", e);
        }
    }

    public void deleteIssue(String issueKey) {
        LOG.debug("Deleting issue " + issueKey);
        client.getIssueClient()
            .deleteIssue(issueKey, true)
            .claim();
    }

    public List<Comment> getComments(String issueKey) {
        LOG.debug("Getting comments of " + issueKey);
        try {
            Iterable<Comment> comments = client.getIssueClient().getIssue(issueKey).get().getComments();
            return StreamSupport.stream(comments.spliterator(), false)
                .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Failed to get comments for " + issueKey, e);
        }
    }

    public boolean findInComments(String issueKey, String message) {
        try {
            List<Comment> comments = getComments(issueKey);
            Optional<Comment> commentWithMessage = comments.stream().filter(comment -> comment.getBody().contains(message)).findAny();
            return !commentWithMessage.isEmpty();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get comments for " + issueKey, e);
        }
    }

    public Issue getIssue(String issueKey) {
        return client.getIssueClient()
            .getIssue(issueKey)
            .claim();
    }
}
