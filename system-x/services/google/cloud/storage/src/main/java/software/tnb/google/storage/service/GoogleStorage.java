package software.tnb.google.storage.service;

import software.tnb.common.account.AccountFactory;
import software.tnb.common.service.Service;
import software.tnb.google.cloud.common.account.GoogleCloudAccount;
import software.tnb.google.storage.validation.GoogleStorageValidation;

import org.junit.jupiter.api.extension.ExtensionContext;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.auto.service.AutoService;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

import io.fabric8.kubernetes.client.utils.Base64;

@AutoService(GoogleStorage.class)
public class GoogleStorage implements Service {
    private static final Logger LOG = LoggerFactory.getLogger(GoogleStorage.class);

    private GoogleCloudAccount account;
    private Storage client;
    private GoogleStorageValidation validation;

    public GoogleCloudAccount account() {
        if (account == null) {
            account = AccountFactory.create(GoogleCloudAccount.class);
        }
        return account;
    }

    protected Storage client() {
        if (client == null) {
            LOG.debug("Creating new Google Storage client");
            try {
                String decodedJson = new String(Base64.decode(account().serviceAccountKey()));
                client = StorageOptions.newBuilder().setCredentials(
                    ServiceAccountCredentials.fromStream(IOUtils.toInputStream(decodedJson, "UTF-8"))).build().getService();
            } catch (Exception e) {
                throw new RuntimeException("Unable to create new Google Storage client", e);
            }
        }
        return client;
    }

    public GoogleStorageValidation validation() {
        return validation;
    }

    @Override
    public void afterAll(ExtensionContext extensionContext) throws Exception {

    }

    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {
        LOG.debug("Creating new Google Storage validation");
        validation = new GoogleStorageValidation(client());
    }
}
