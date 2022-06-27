package software.tnb.google.sheets.service;

import software.tnb.common.account.Accounts;
import software.tnb.common.service.Service;
import software.tnb.google.sheets.account.GoogleAccount;
import software.tnb.google.sheets.validation.SheetsValidation;

import org.junit.jupiter.api.extension.ExtensionContext;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.GenericJson;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.UserCredentials;
import com.google.auto.service.AutoService;

import java.io.IOException;
import java.security.GeneralSecurityException;

@AutoService(GoogleSheets.class)
public class GoogleSheets implements Service {
    private static final Logger LOG = LoggerFactory.getLogger(GoogleSheets.class);
    private static final String APPLICATION_NAME = "TNB test app";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

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
        return new Sheets.Builder(httpTransport, JSON_FACTORY, createCredentials()).setApplicationName(APPLICATION_NAME).build();
    }

    private HttpCredentialsAdapter createCredentials() {
        GenericJson json = new GenericJson();
        json.put("type", "authorized_user");
        json.put("refresh_token", account().refreshToken());
        json.put("client_id", account().clientId());
        json.put("client_secret", account().clientSecret());
        json.setFactory(JSON_FACTORY);

        try {
            return new HttpCredentialsAdapter(UserCredentials.fromStream(IOUtils.toInputStream(json.toPrettyString(), "UTF-8")));
        } catch (IOException e) {
            throw new RuntimeException("Unable to create credentials", e);
        }
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
