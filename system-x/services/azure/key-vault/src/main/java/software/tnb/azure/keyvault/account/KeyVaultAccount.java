package software.tnb.azure.keyvault.account;

import software.tnb.common.account.Account;
import software.tnb.common.account.WithId;

public class KeyVaultAccount implements Account, WithId {
    private String vaultName;
    private String tenantId;
    private String clientId;
    private String clientSecret;

    public void setVault_name(String vault_name) {
        this.vaultName = vault_name;
    }

    public void setTenant_Id(String tenant_Id) {
        this.tenantId = tenant_Id;
    }

    public void setClient_Id(String client_Id) {
        this.clientId = client_Id;
    }

    public void setClient_secret(String client_secret) {
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
        return "tnb-azure-key-vault";
    }
}
