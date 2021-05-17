package org.jboss.fuse.tnb.jira.account;

import org.jboss.fuse.tnb.common.account.Account;
import org.jboss.fuse.tnb.common.account.WithId;

public class JiraAccount implements Account, WithId {
    private String jira_url;
    private String username;
    private String password;

    @Override
    public String credentialsId() {
        return "jira";
    }

    public String getJiraUrl() {
        return jira_url;
    }

    public void setJira_url(String jira_url) {
        this.jira_url = jira_url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
