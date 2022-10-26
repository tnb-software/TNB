package software.tnb.google.sheets.service;

import software.tnb.google.api.common.service.GoogleAPIService;
import software.tnb.google.sheets.validation.GoogleSheetsValidation;

import org.junit.jupiter.api.extension.ExtensionContext;

import com.google.api.services.sheets.v4.Sheets;
import com.google.auto.service.AutoService;

@AutoService(GoogleSheets.class)
public class GoogleSheets extends GoogleAPIService<GoogleSheetsValidation> {
    private static final String APPLICATION_NAME = "tnb-system-x-google-sheets";

    protected Sheets client() {
        LOG.debug("Creating new Google Sheets client");
        return new Sheets.Builder(httpTransport, JSON_FACTORY, createCredentials()).setApplicationName(APPLICATION_NAME).build();
    }

    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {
        LOG.debug("Creating new Google Sheets validation");
        validation = new GoogleSheetsValidation(client());
    }
}
