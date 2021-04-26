package org.jboss.fuse.tnb.google.account;

import org.jboss.fuse.tnb.common.account.Account;

public class GoogleAccount implements Account {

    private String api_client_id;
    private String api_client_secret;
    private String api_refresh_token;

    @Override
    public String credentialsId() {
        return "google";
    }

    public String getApiClientId() {
        return api_client_id;
    }

    public void setApi_client_id(String apiClientId) {
        this.api_client_id = apiClientId;
    }

    public String getApiClientSecret() {
        return api_client_secret;
    }

    public void setApi_client_secret(String apiClientSecret) {
        this.api_client_secret = apiClientSecret;
    }

    public String getApiRefreshToken() {
        return api_refresh_token;
    }

    public void setApi_refresh_token(String api_refresh_token) {
        this.api_refresh_token = api_refresh_token;
    }
}
