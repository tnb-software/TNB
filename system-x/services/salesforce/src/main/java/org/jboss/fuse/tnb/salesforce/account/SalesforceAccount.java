package org.jboss.fuse.tnb.salesforce.account;

import org.jboss.fuse.tnb.common.account.Account;
import org.jboss.fuse.tnb.common.account.WithId;

public class SalesforceAccount implements Account, WithId {
    private String topicName;
    private String loginUrl;
    private String client_id;
    private String client_secret;
    private String userName;
    private String password;

    @Override
    public String credentialsId() {
        return "salesforce";
    }

    public String topicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public String loginUrl() {
        return loginUrl;
    }

    public void setLoginUrl(String loginUrl) {
        this.loginUrl = loginUrl;
    }

    public String clientId() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public String clientSecret() {
        return client_secret;
    }

    public void setClient_secret(String client_secret) {
        this.client_secret = client_secret;
    }

    public String userName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String password() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
