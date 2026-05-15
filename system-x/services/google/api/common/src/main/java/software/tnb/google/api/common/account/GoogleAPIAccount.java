package software.tnb.google.api.common.account;

import software.tnb.common.account.Account;
import software.tnb.common.account.WithId;

public class GoogleAPIAccount implements Account, WithId {
    private String clientId;
    private String clientSecret;
    private String refreshToken;

    @Override
    public String credentialsId() {
        return "google_api";
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

    public String refreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
