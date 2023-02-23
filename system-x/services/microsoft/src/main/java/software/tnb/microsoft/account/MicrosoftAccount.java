package software.tnb.microsoft.account;

import software.tnb.common.account.Account;
import software.tnb.common.account.WithId;

public class MicrosoftAccount implements Account, WithId {
    private String username;
    private String client_id;
    private String client_secret;
    private String tenant_id;

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

    public String tenantId() {
        return tenant_id;
    }

    public void setTenant_id(String tenant_id) {
        this.tenant_id = tenant_id;
    }
}
