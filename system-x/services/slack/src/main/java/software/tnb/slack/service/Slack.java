package software.tnb.slack.service;

import software.tnb.slack.validation.SlackValidation;
import software.tnb.common.account.Accounts;
import software.tnb.common.service.Service;
import software.tnb.slack.account.SlackAccount;

import org.junit.jupiter.api.extension.ExtensionContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.auto.service.AutoService;

@AutoService(Slack.class)
public class Slack implements Service {
    private static final Logger LOG = LoggerFactory.getLogger(Slack.class);

    private SlackAccount account;
    private com.slack.api.Slack client;
    private SlackValidation validation;

    public SlackAccount account() {
        if (account == null) {
            LOG.debug("Creating new Slack account");
            account = Accounts.get(SlackAccount.class);
        }
        return account;
    }

    protected com.slack.api.Slack client() {
        LOG.debug("Creating new Slack client");
        client = com.slack.api.Slack.getInstance();
        return client;
    }

    public SlackValidation validation() {
        return validation;
    }

    @Override
    public void afterAll(ExtensionContext extensionContext) throws Exception {
        if (client != null) {
            LOG.debug("Closing Slack client");
            client.close();
        }
    }

    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {
        LOG.debug("Creating new Slack validation");
        validation = new SlackValidation(client(), account());
    }
}
