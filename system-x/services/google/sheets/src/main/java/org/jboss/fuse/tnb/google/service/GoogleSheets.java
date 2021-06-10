package org.jboss.fuse.tnb.google.service;

import org.jboss.fuse.tnb.common.account.Accounts;
import org.jboss.fuse.tnb.common.service.Service;
import org.jboss.fuse.tnb.google.account.GoogleAccount;
import org.jboss.fuse.tnb.google.validation.SheetsValidation;

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

    private NetHttpTransport httpTransport;

    private GoogleAccount account;
    private Sheets service;
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
        if (service == null) {
            LOG.debug("Creating new google-sheet client");
            Credential credential = authorize();
            return new Sheets.Builder(httpTransport, JSON_FACTORY, credential).setApplicationName(APPLICATION_NAME)
                .build();
        }
        return service;
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
        if (validation == null) {
            LOG.debug("Creating new google-sheet validation");
            validation = new SheetsValidation(client(), account());
        }
        return validation;
    }

    @Override
    public void afterAll(ExtensionContext extensionContext) throws Exception {

    }

    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {
    }
}
