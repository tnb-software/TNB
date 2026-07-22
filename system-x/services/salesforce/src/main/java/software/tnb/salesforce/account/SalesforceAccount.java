package software.tnb.salesforce.account;

import software.tnb.common.account.Account;
import software.tnb.common.account.WithId;

public class SalesforceAccount implements Account, WithId {
    private String topicName;
    private String loginUrl;
    private String clientId;
    private String clientSecret;
    private String userName;
    private String password;
    private String secureSocketProtocol;

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
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String clientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String userName() {
        return userName;
    }

    public void setUsername(String username) {
        this.userName = username;
    }

    public String password() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String secureSocketProtocol() {
        return secureSocketProtocol;
    }

    public void setSecureSocketProtocol(String secureSocketProtocol) {
        this.secureSocketProtocol = secureSocketProtocol;
    }
}
