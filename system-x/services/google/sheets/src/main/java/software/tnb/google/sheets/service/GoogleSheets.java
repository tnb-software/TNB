package software.tnb.google.sheets.service;

import software.tnb.google.sheets.validation.SheetsValidation;
import software.tnb.common.account.Accounts;
import software.tnb.common.service.Service;
import software.tnb.google.sheets.account.GoogleAccount;

import org.junit.jupiter.api.extension.ExtensionContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.auto.service.AutoService;

import java.io.IOException;
import java.security.GeneralSecurityException;

@AutoService(GoogleSheets.class)
public class GoogleSheets implements Service {
    private static final Logger LOG = LoggerFactory.getLogger(GoogleSheets.class);
    private static final String APPLICATION_NAME = "TNB test app";
    private static final JacksonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    private final NetHttpTransport httpTransport;

    private GoogleAccount account;
    private SheetsValidation validation;

    public GoogleSheets() {
        try {
            this.httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        } catch (IOException | GeneralSecurityException e) {
            throw new RuntimeException("Can't create http transport", e);
        }
    }

    public GoogleAccount account() {
        if (account == null) {
            account = Accounts.get(GoogleAccount.class);
        }
        return account;
    }

    protected Sheets client() {
        LOG.debug("Creating new Google Sheet client");
        return new Sheets.Builder(httpTransport, JSON_FACTORY, authorize()).setApplicationName(APPLICATION_NAME).build();
    }

    private Credential authorize() {
        // authorize
        Credential credential = new GoogleCredential.Builder()
            .setJsonFactory(JSON_FACTORY)
            .setTransport(httpTransport)
            .setClientSecrets(account().getApiClientId(), account().getApiClientSecret())
            .build();

        credential.setRefreshToken(account().getApiRefreshToken());

        return credential;
    }

    public SheetsValidation validation() {
        return validation;
    }

    @Override
    public void afterAll(ExtensionContext extensionContext) throws Exception {
    }

    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {
        LOG.debug("Creating new Google Sheet validation");
        validation = new SheetsValidation(client(), account());
    }
}
