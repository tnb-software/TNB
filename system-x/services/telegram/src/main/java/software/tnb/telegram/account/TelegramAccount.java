package software.tnb.telegram.account;

import software.tnb.common.account.Account;
import software.tnb.common.account.WithId;

public class TelegramAccount implements Account, WithId {
    private String app_hash;
    private String appId;
    private String dcId;
    private String dcIp;
    private String sessionString;
    private String token;
    private String username;
    private String sender_id;

    public String getAppHash() {
        return app_hash;
    }

    public void setApp_hash(String app_hash) {
        this.app_hash = app_hash;
    }

    public String getAppId() {
        return appId;
    }

    public String getDcId() {
        return dcId;
    }

    public String getDcIp() {
        return dcIp;
    }

    public String getSessionString() {
        return sessionString;
    }

    public String getToken() {
        return token;
    }

    public String getUsername() {
        return username;
    }

    public String getSenderId() {
        return sender_id;
    }

    public void setSender_id(String sender_id) {
        this.sender_id = sender_id;
    }

    @Override
    public String credentialsId() {
        return "telegram";
    }
}
