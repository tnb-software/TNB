package software.tnb.jira.account;

import software.tnb.common.account.Account;
import software.tnb.common.account.WithId;

public class JiraAccount implements Account, WithId {
    private String url;
    private String username;
    private String password;

    @Override
    public String credentialsId() {
        return "jira";
    }

    public String url() {
        return url;
    }

    public String username() {
        return username;
    }

    public String password() {
        return password;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
