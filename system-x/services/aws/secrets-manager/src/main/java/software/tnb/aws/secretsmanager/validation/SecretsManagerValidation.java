package software.tnb.aws.secretsmanager.validation;

import software.tnb.common.validation.Validation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.CreateSecretResponse;

public class SecretsManagerValidation implements Validation {
    private static final Logger LOG = LoggerFactory.getLogger(SecretsManagerValidation.class);

    private final SecretsManagerClient client;

    public SecretsManagerValidation(SecretsManagerClient client) {
        this.client = client;
    }

    public String createSecret(String name, String value) {
        LOG.debug("Creating Secret {}", name);
        CreateSecretResponse resp = client.createSecret(b -> b.name(name).secretString(value));
        return resp.arn();
    }

    public void updateSecret(String name, String newValue) {
        LOG.debug("Updating Secret {}", name);
        client.updateSecret(b -> b.secretId(name).secretString(newValue));
    }

    public void putSecretValue(String name, String newValue) {
        LOG.debug("Putting Secret value {}", name);
        client.putSecretValue(b -> b.secretId(name).secretString(newValue));
    }

    public void deleteSecret(String arn) {
        LOG.debug("Deleting Secret {}", arn);
        client.deleteSecret(b -> b.secretId(arn));
    }

    public void forceDeleteSecret(String arn) {
        LOG.debug("Force deleting Secret {}", arn);
        client.deleteSecret(b -> b.secretId(arn).forceDeleteWithoutRecovery(true));
    }

    public String readSecret(String arn) {
        LOG.debug("Reading Secret {}", arn);
        return client.getSecretValue(b -> b.secretId(arn)).secretString();
    }
}
