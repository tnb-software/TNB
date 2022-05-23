package org.jboss.fuse.tnb.jira.validation;

import org.jboss.fuse.tnb.jira.account.JiraAccount;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.rest.client.api.GetCreateIssueMetadataOptionsBuilder;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.BasicIssue;
import com.atlassian.jira.rest.client.api.domain.Comment;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.input.IssueInputBuilder;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

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
        long issueTypeId = client.getIssueClient().getCreateIssueMetadata(
            new GetCreateIssueMetadataOptionsBuilder().withIssueTypeNames("Bug").withProjectKeys(projectKey).withExpandedIssueTypesFields().build())
            .claim().iterator().next().getIssueTypes().iterator().next().getId(); // (long) 10005
        BasicIssue issue = null;
        boolean unsuccessful = true;
        for (int i = 0; i < 2 && unsuccessful; i++) {
            try {
                issue =
                    client.getIssueClient().createIssue(new IssueInputBuilder(projectKey, issueTypeId).setSummary(issueSummary).build()).claim();
                unsuccessful = false;
            } catch (Exception e) {
                LOG.debug(i + ". attempt to create issue failed");
            }
        }
        if (unsuccessful) {
            throw new RuntimeException("Failed to create issue");
        }
        LOG.debug("Created issue: " + issue.getKey());
        return issue.getKey();
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

    public void addComment(String issueKey, String comment) {
        LOG.debug("Adding comment on issue " + issueKey);
        try {
            client.getIssueClient().addComment(client.getIssueClient().getIssue(issueKey).claim().getCommentsUri()
                , Comment.valueOf(comment));
        } catch (Exception e) {
            throw new RuntimeException("Failed to add comment on issue " + issueKey, e);
        }
    }

    public Iterable<Issue> getIssues(String project) {
        assert StringUtils.isNotEmpty(project);
        return client.getSearchClient().searchJql(String.format("project = \"%s\"", project))
            .claim().getIssues();
    }

    public Iterable<Issue> getIssues(String project, String customJQL) {
        assert StringUtils.isNotEmpty(project);
        assert StringUtils.isNotEmpty(customJQL);
        return client.getSearchClient().searchJql(String.format("project = \"%s\" AND %s", project, customJQL))
            .claim().getIssues();
    }
}
