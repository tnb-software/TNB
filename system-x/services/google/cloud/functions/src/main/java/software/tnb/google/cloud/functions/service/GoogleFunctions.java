package software.tnb.google.cloud.functions.service;

import software.tnb.common.service.Service;
import software.tnb.common.service.ServiceFactory;
import software.tnb.google.cloud.functions.account.GoogleFunctionsAccount;
import software.tnb.google.cloud.functions.validation.GoogleFunctionsValidation;
import software.tnb.google.storage.service.GoogleStorage;

import org.junit.jupiter.api.extension.ExtensionContext;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.auto.service.AutoService;
import com.google.cloud.functions.v1.CloudFunctionsServiceClient;
import com.google.cloud.functions.v1.CloudFunctionsServiceSettings;

@AutoService(GoogleFunctions.class)
public class GoogleFunctions extends Service<GoogleFunctionsAccount, CloudFunctionsServiceClient, GoogleFunctionsValidation> {
    private final GoogleStorage storage = ServiceFactory.create(GoogleStorage.class);

    protected CloudFunctionsServiceClient client() {
        if (client == null) {
            try {
                return CloudFunctionsServiceClient.create(CloudFunctionsServiceSettings.newBuilder()
                    .setCredentialsProvider(FixedCredentialsProvider.create(ServiceAccountCredentials
                        .fromStream(account().serviceAccountKeyStream()))).build());
            } catch (Exception e) {
                throw new RuntimeException("Unable to create new client", e);
            }
        }
        return client;
    }

    @Override
    public void afterAll(ExtensionContext extensionContext) throws Exception {
        storage.afterAll(extensionContext);
    }

    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {
        storage.beforeAll(extensionContext);
        validation = new GoogleFunctionsValidation(account(), client(), storage);
    }

}
