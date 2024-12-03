package software.tnb.azure.keyvault.validation;

import software.tnb.common.validation.Validation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.azure.core.util.polling.SyncPoller;
import com.azure.security.keyvault.secrets.SecretClient;
import com.azure.security.keyvault.secrets.models.DeletedSecret;
import com.azure.security.keyvault.secrets.models.KeyVaultSecret;

public class KeyVaultValidation implements Validation {
    private static final Logger LOG = LoggerFactory.getLogger(KeyVaultValidation.class);

    private final SecretClient client;

    public KeyVaultValidation(SecretClient client) {
        this.client = client;
    }

    public KeyVaultSecret getSecret(String secret) {
        LOG.debug("Getting secret " + secret);
        KeyVaultSecret value = client.getSecret(secret);
        LOG.debug("Value read from secret {}: {}", secret, value.getValue());
        return value;
    }

    public void setSecret(String name, String value) {
        LOG.debug("Setting secret: {} to value: {}", name, value);
        client.setSecret(name, value);
    }

    public void createSecret(String name, String value) {
        LOG.debug(String.format("Creating secret: %s with value: %s", name, value));
        KeyVaultSecret newSecret = new KeyVaultSecret(name, value);
        client.setSecret(newSecret);
    }

    public void deleteSecret(String name) {
        LOG.debug("Deleting secret " + name);
        SyncPoller<DeletedSecret, Void> deletionPoller = client.beginDeleteSecret(name);
        deletionPoller.waitForCompletion();
        client.purgeDeletedSecret(name);
    }
}
