package software.tnb.google.cloud.functions.service;

import software.tnb.common.account.AccountFactory;
import software.tnb.common.service.Service;
import software.tnb.common.service.ServiceFactory;
import software.tnb.google.cloud.functions.account.GoogleFunctionsAccount;
import software.tnb.google.cloud.functions.validation.GoogleFunctionsValidation;
import software.tnb.google.storage.service.GoogleStorage;

import org.junit.jupiter.api.extension.ExtensionContext;

import org.apache.commons.io.IOUtils;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.auto.service.AutoService;
import com.google.cloud.functions.v1.CloudFunctionsServiceClient;
import com.google.cloud.functions.v1.CloudFunctionsServiceSettings;

import io.fabric8.kubernetes.client.utils.Base64;

@AutoService(GoogleFunctions.class)
public class GoogleFunctions implements Service {
    private final GoogleStorage storage = ServiceFactory.create(GoogleStorage.class);

    private GoogleFunctionsAccount account;
    private GoogleFunctionsValidation validation;

    public GoogleFunctionsAccount account() {
        if (account == null) {
            account = AccountFactory.create(GoogleFunctionsAccount.class);
        }
        return account;
    }

    protected CloudFunctionsServiceClient client() {
        try {
            String decodedJson = new String(Base64.decode(account().serviceAccountKey()));
            return CloudFunctionsServiceClient.create(CloudFunctionsServiceSettings.newBuilder()
                .setCredentialsProvider(FixedCredentialsProvider.create(ServiceAccountCredentials
                    .fromStream(IOUtils.toInputStream(decodedJson, "UTF-8")))).build());
        } catch (Exception e) {
            throw new RuntimeException("Unable to create new client", e);
        }
    }

    public GoogleFunctionsValidation validation() {
        return validation;
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
