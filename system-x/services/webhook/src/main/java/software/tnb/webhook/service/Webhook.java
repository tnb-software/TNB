package software.tnb.webhook.service;

import software.tnb.common.account.AccountFactory;
import software.tnb.common.service.Service;
import software.tnb.webhook.account.WebhookAccount;
import software.tnb.webhook.validation.WebhookValidation;

import org.junit.jupiter.api.extension.ExtensionContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.auto.service.AutoService;

@AutoService(Webhook.class)
public class Webhook implements Service {
    private static final Logger LOG = LoggerFactory.getLogger(Webhook.class);

    private WebhookAccount account;
    private WebhookValidation validation;

    public WebhookAccount account() {
        if (account == null) {
            LOG.debug("Creating new Webhook account");
            account = AccountFactory.create(WebhookAccount.class);
        }
        return account;
    }

    public WebhookValidation validation() {
        return validation;
    }

    @Override
    public void afterAll(ExtensionContext extensionContext) throws Exception {
    }

    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {
        LOG.debug("Creating new Webhook validation");
        validation = new WebhookValidation();
    }
}
