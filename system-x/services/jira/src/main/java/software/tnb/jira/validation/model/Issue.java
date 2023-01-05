package software.tnb.jira.validation.model;

import java.util.List;
import java.util.Objects;

public class Issue {
    private String key;
    private String type;
    private String priority;
    private String status;
    private String summary;
    private String description;
    private String projectKey;
    private List<String> attachmentsIds;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProjectKey() {
        return projectKey;
    }

    public void setProjectKey(String projectKey) {
        this.projectKey = projectKey;
    }

    public List<String> getAttachmentsIds() {
        return attachmentsIds;
    }

    public void setAttachmentsIds(List<String> attachmentsIds) {
        this.attachmentsIds = attachmentsIds;
    }

    @Override
    public String toString() {
        return "Issue{"
            + "key='" + key + '\''
            + ", type='" + type + '\''
            + ", priority='" + priority + '\''
            + ", status='" + status + '\''
            + ", summary='" + summary + '\''
            + ", description='" + description + '\''
            + ", projectKey='" + projectKey + '\''
            + ", attachmentsIds=" + attachmentsIds
            + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Issue issue = (Issue) o;
        return Objects.equals(key, issue.key) && Objects.equals(projectKey, issue.projectKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, projectKey);
    }
}
