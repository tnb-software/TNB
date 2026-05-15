package software.tnb.azure.keyvault.account;

import software.tnb.common.account.Account;
import software.tnb.common.account.WithId;

public class AzureKeyVaultAccount implements Account, WithId {
    private String vaultName;
    private String tenantId;
    private String clientId;
    private String clientSecret;

    public void setVaultName(String vault_name) {
        this.vaultName = vault_name;
    }

    public void setTenantId(String tenant_Id) {
        this.tenantId = tenant_Id;
    }

    public void setClientId(String client_Id) {
        this.clientId = client_Id;
    }

    public void setClientSecret(String client_secret) {
        this.clientSecret = client_secret;
    }

    public String tenantId() {
        return tenantId;
    }

    public String clientId() {
        return clientId;
    }

    public String clientSecret() {
        return clientSecret;
    }

    public String vaultName() {
        return vaultName;
    }

    @Override
    public String credentialsId() {
        return "azure_key_vault";
    }
}
