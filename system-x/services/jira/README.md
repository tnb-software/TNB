# TNB :: System-X :: Services :: Jira

Jira integration service for the TNB test framework. Provides CRUD operations on Jira issues via the [Jira REST API v3](https://developer.atlassian.com/cloud/jira/platform/rest/v3/intro/).

## API Reference

The OpenAPI specification for the Jira REST API is available at:
https://developer.atlassian.com/cloud/jira/platform/swagger-v3.v3.json

## Available Operations

| Method | Description |
|--------|-------------|
| `createIssue(projectKey, issueSummary)` | Create a Bug issue, returns issue key |
| `deleteIssue(issueKey)` | Delete an issue and its subtasks |
| `getIssue(issueKey)` | Get issue details |
| `getIssues(jql)` | Search issues using JQL |
| `getIssues(project, customJQL)` | Search issues within a project |
| `getComments(issueKey)` | Get all comments on an issue |
| `addComment(issueKey, content)` | Add a comment to an issue |
| `setTransition(issueKey, transitionId)` | Transition an issue to a new status |
