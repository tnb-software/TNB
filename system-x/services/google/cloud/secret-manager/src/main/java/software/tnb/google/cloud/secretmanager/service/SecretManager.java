package software.tnb.google.cloud.secretmanager.service;

import software.tnb.common.service.Service;
import software.tnb.google.cloud.common.account.GoogleCloudAccount;
import software.tnb.google.cloud.secretmanager.validation.SecretManagerValidation;

import org.junit.jupiter.api.extension.ExtensionContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auto.service.AutoService;
import com.google.cloud.secretmanager.v1.SecretManagerServiceClient;
import com.google.cloud.secretmanager.v1.SecretManagerServiceSettings;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Base64;

@AutoService(SecretManager.class)
public class SecretManager extends Service<GoogleCloudAccount, SecretManagerServiceClient, SecretManagerValidation> {

    private static final Logger LOG = LoggerFactory.getLogger(SecretManager.class);

    @Override
    public void beforeAll(ExtensionContext extensionContext) {
        LOG.debug("Creating Google Secret Manager validation");
        validation = new SecretManagerValidation(client(), account().projectId());
    }

    @Override
    public void afterAll(ExtensionContext extensionContext) {
        if (client != null) {
            client.close();
        }
    }

    protected SecretManagerServiceClient client() {
        if (client == null) {
            try (InputStream serviceAccountKey = new ByteArrayInputStream(Base64.getDecoder().decode(account().serviceAccountKey()))) {
                FixedCredentialsProvider provider = FixedCredentialsProvider.create(GoogleCredentials.fromStream(serviceAccountKey));
                SecretManagerServiceSettings settings = SecretManagerServiceSettings.newBuilder().setCredentialsProvider(provider).build();
                LOG.debug("Creating Google Secret Manager client");
                client = SecretManagerServiceClient.create(settings);
            } catch (Exception e) {
                throw new RuntimeException("Unable to create client: ", e);
            }
        }
        return client;
    }
    
}
