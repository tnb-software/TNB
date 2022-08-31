package software.tnb.google.storage.service;

import software.tnb.common.account.Accounts;
import software.tnb.common.service.Service;
import software.tnb.google.storage.account.GoogleStorageAccount;
import software.tnb.google.storage.validation.GoogleStorageValidation;

import org.junit.jupiter.api.extension.ExtensionContext;

import org.apache.commons.io.IOUtils;

import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.auto.service.AutoService;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

import io.fabric8.kubernetes.client.utils.Base64;

@AutoService(GoogleStorage.class)
public class GoogleStorage implements Service {
    private GoogleStorageAccount account;
    private Storage client;
    private GoogleStorageValidation validation;

    public GoogleStorageAccount account() {
        if (account == null) {
            account = Accounts.get(GoogleStorageAccount.class);
        }
        return account;
    }

    protected Storage client() {
        if (client == null) {
            try {
                String decodedJson = new String(Base64.decode(account().serviceAccount()));
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
        validation = new GoogleStorageValidation(client());
    }
}
