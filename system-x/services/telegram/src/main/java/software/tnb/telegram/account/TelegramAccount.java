package software.tnb.telegram.account;

import software.tnb.common.account.Account;
import software.tnb.common.account.WithId;

public class TelegramAccount implements Account, WithId {
    private String appHash;
    private String appId;
    private String sessionString;
    private String token;
    private String username;
    private String senderId;

    public String appHash() {
        return appHash;
    }

    public String appId() {
        return appId;
    }

    public String sessionString() {
        return sessionString;
    }

    public String token() {
        return token;
    }

    public String username() {
        return username;
    }

    public String senderId() {
        return senderId;
    }

    public void setAppHash(String appHash) {
        this.appHash = appHash;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public void setSessionString(String sessionString) {
        this.sessionString = sessionString;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    @Override
    public String credentialsId() {
        return "telegram";
    }
}
