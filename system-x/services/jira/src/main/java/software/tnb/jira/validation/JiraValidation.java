package software.tnb.jira.validation;

import software.tnb.common.validation.Validation;
import software.tnb.jira.validation.model.Issue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.Attachment;
import com.atlassian.jira.rest.client.api.domain.Comment;
import com.atlassian.jira.rest.client.api.domain.IssueType;
import com.atlassian.jira.rest.client.api.domain.input.IssueInputBuilder;
import com.atlassian.jira.rest.client.api.domain.input.TransitionInput;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

public class JiraValidation implements Validation {
    private static final Logger LOG = LoggerFactory.getLogger(JiraValidation.class);

    private final JiraRestClient client;

    public JiraValidation(JiraRestClient client) {
        this.client = client;
    }

    /**
     * Create issue in given project.
     *
     * @param projectKey key of project where issue will be created
     * @param issueSummary name of issue to be created
     * @return id of created issue
     */
    public String createIssue(String projectKey, String issueSummary) {
        final Optional<IssueType> bugTypeId = StreamSupport.stream(client.getMetadataClient().getIssueTypes().claim().spliterator(), false)
            .filter(t -> "bug".equalsIgnoreCase(t.getName())).findAny();

        if (bugTypeId.isEmpty()) {
            throw new RuntimeException("Unable to find bug type id");
        }

        final IssueInputBuilder builder = new IssueInputBuilder()
            .setProjectKey(projectKey)
            .setIssueTypeId(bugTypeId.get().getId())
            .setSummary(issueSummary);
        final String key = client.getIssueClient().createIssue(builder.build()).claim().getKey();
        LOG.debug("Created a new issue with key {}", key);
        return key;
    }

    public void deleteIssue(String issueKey) {
        LOG.debug("Deleting issue {}", issueKey);
        client.getIssueClient().deleteIssue(issueKey, true).claim();
    }

    public List<String> getComments(String issueKey) {
        LOG.debug("Getting comments of {}", issueKey);
        return StreamSupport.stream(client.getIssueClient().getIssue(issueKey).claim().getComments().spliterator(), false)
            .map(Comment::getBody).toList();
    }

    public Issue getIssue(String issueKey) {
        LOG.debug("Getting issue {}", issueKey);
        return convertToIssue(client.getIssueClient().getIssue(issueKey).claim());
    }

    public void addComment(String issueKey, String content) {
        LOG.debug("Adding comment {} to issue {}", content, issueKey);
        final URI commentsUri = client.getIssueClient().getIssue(issueKey).claim().getCommentsUri();
        client.getIssueClient().addComment(commentsUri, Comment.valueOf(content)).claim();
    }

    public List<Issue> getIssues(String jql) {
        return StreamSupport.stream(client.getSearchClient().searchJql(jql).claim().getIssues().spliterator(), false)
            .map(this::convertToIssue).toList();
    }

    public List<Issue> getIssues(String project, String customJQL) {
        return getIssues(String.format("project = \"%s\" AND %s", project, customJQL));
    }

    public void setTransition(String issueKey, int transitionId) {
        LOG.debug("Transition issue {} - transition id: {}", issueKey, transitionId);
        final com.atlassian.jira.rest.client.api.domain.Issue issue = client.getIssueClient().getIssue(issueKey).claim();
        client.getIssueClient().transition(issue, new TransitionInput(transitionId));
    }

    private Issue convertToIssue(com.atlassian.jira.rest.client.api.domain.Issue issue) {
        Issue result = new Issue();
        result.setKey(issue.getKey());
        result.setSummary(issue.getSummary());
        result.setDescription(issue.getDescription());
        result.setProjectKey(issue.getProject().getKey());
        result.setType(issue.getIssueType().getName());
        result.setPriority(issue.getPriority() == null ? "" : issue.getPriority().getName());
        result.setStatus(issue.getStatus().getName());
        final Iterable<Attachment> attachments = issue.getAttachments();
        if (attachments != null) {
            result.setAttachmentsIds(StreamSupport.stream(attachments.spliterator(), false).map(Attachment::getFilename).toList());
        } else {
            result.setAttachmentsIds(List.of());
        }
        return result;
    }
}
