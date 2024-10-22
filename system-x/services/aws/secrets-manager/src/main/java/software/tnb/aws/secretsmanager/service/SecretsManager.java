package software.tnb.aws.secretsmanager.service;

import software.tnb.aws.common.account.AWSAccount;
import software.tnb.aws.common.service.AWSService;
import software.tnb.aws.secretsmanager.validation.SecretsManagerValidation;

import org.junit.jupiter.api.extension.ExtensionContext;

import com.google.auto.service.AutoService;

import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;

@AutoService(SecretsManager.class)
public class SecretsManager extends AWSService<AWSAccount, SecretsManagerClient, SecretsManagerValidation> {
    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {
        super.beforeAll(extensionContext);
        LOG.debug("Creating new Secrets Manager validation");
        validation = new SecretsManagerValidation(client());
    }
}
