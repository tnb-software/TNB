package software.tnb.google.mail.service;

import software.tnb.google.api.common.service.GoogleAPIService;
import software.tnb.google.mail.validation.GoogleMailValidation;

import org.junit.jupiter.api.extension.ExtensionContext;

import com.google.api.services.gmail.Gmail;
import com.google.auto.service.AutoService;

@AutoService(GoogleMail.class)
public class GoogleMail extends GoogleAPIService<GoogleMailValidation> {
    private static final String APPLICATION_NAME = "tnb-system-x-google-mail";

    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {
        LOG.debug("Creating new Google Mail validation");
        validation = new GoogleMailValidation(client());
    }

    protected Gmail client() {
        LOG.debug("Creating new Google Mail client");
        return new Gmail.Builder(httpTransport, JSON_FACTORY, createCredentials()).setApplicationName(APPLICATION_NAME).build();
    }
}
