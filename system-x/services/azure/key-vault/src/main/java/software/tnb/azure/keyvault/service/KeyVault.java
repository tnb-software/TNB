package software.tnb.azure.keyvault.service;

import software.tnb.azure.keyvault.account.KeyVaultAccount;
import software.tnb.azure.keyvault.validation.KeyVaultValidation;
import software.tnb.common.service.Service;

import org.junit.jupiter.api.extension.ExtensionContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.azure.identity.ClientSecretCredential;
import com.azure.identity.ClientSecretCredentialBuilder;
import com.azure.security.keyvault.secrets.SecretClient;
import com.azure.security.keyvault.secrets.SecretClientBuilder;

public class KeyVault extends Service<KeyVaultAccount, SecretClient, KeyVaultValidation> {
    private static final Logger LOG = LoggerFactory.getLogger(KeyVault.class);

    @Override
    public void afterAll(ExtensionContext context) throws Exception {
    }

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        validation = new KeyVaultValidation(client());
    }

    @Override
    public SecretClient client() {
        SecretClient keyVaultClient;
        LOG.debug("Creating new Azure Key Vault client");
        String vaultName = account().vaultName();
        ClientSecretCredential credentials = new ClientSecretCredentialBuilder()
            .tenantId(account().tenantId())
            .clientId(account().clientId())
            .clientSecret(account().clientSecret())
            .build();
        keyVaultClient = new SecretClientBuilder()
            .vaultUrl(String.format("https://%s.vault.azure.net", vaultName))
            .credential(credentials)
            .buildClient();
        return keyVaultClient;
    }
}
