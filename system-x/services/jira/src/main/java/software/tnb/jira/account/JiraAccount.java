package software.tnb.jira.account;

import software.tnb.common.account.Account;
import software.tnb.common.account.WithId;

public class JiraAccount implements Account, WithId {
    private String jira_url;
    private String username;
    private String password;
    private String private_key;
    private String access_token;
    private String consumer_key;
    private String verification_code;

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

    public String getPrivateKey() {
        return private_key;
    }

    public void setPrivate_key(final String private_key) {
        this.private_key = private_key;
    }

    public String getAccessToken() {
        return access_token;
    }

    public void setAccess_token(final String access_token) {
        this.access_token = access_token;
    }

    public String getConsumerKey() {
        return consumer_key;
    }

    public void setConsumer_key(final String consumer_key) {
        this.consumer_key = consumer_key;
    }

    public String getVerificationCode() {
        return verification_code;
    }

    public void setVerification_code(final String verification_code) {
        this.verification_code = verification_code;
    }
}
