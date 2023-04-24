package software.tnb.webhook.service;

import software.tnb.common.client.NoClient;
import software.tnb.common.service.Service;
import software.tnb.common.utils.JUnitUtils;
import software.tnb.webhook.account.WebhookAccount;
import software.tnb.webhook.validation.WebhookValidation;

import org.junit.jupiter.api.extension.ExtensionContext;

import com.google.auto.service.AutoService;

@AutoService(Webhook.class)
public class Webhook extends Service<WebhookAccount, NoClient, WebhookValidation> {
    // make validation static on purpose to share the created webhook endpoint in multiple test classes (in addition to afterAll method)
    private static WebhookValidation validation;

    public WebhookValidation validation() {
        if (validation == null) {
            validation = new WebhookValidation();
        }
        return validation;
    }

    @Override
    public void afterAll(ExtensionContext extensionContext) throws Exception {
        if (JUnitUtils.isExtensionStillNeeded(extensionContext, this.getClass())) {
            validation.clearEndpoint();
        } else {
            validation.deleteEndpoint();
            validation = null;
        }
    }

    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {
    }
}
