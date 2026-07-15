package software.tnb.microsoft.account;

import software.tnb.common.account.Account;
import software.tnb.common.account.WithId;

public class MicrosoftAccount implements Account, WithId {
    private String username;
    private String clientId;
    private String clientSecret;
    private String tenantId;

    @Override
    public String credentialsId() {
        return "microsoft_exchange";
    }

    public String username() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public String tenantId() {
        return tenantId;
    }

    public void setTenantId(String tenant_id) {
        this.tenantId = tenant_id;
    }
}
