package software.tnb.telegram.service;

import software.tnb.common.account.AccountFactory;
import software.tnb.common.deployment.WithExternalHostname;
import software.tnb.common.service.Service;
import software.tnb.telegram.account.TelegramAccount;
import software.tnb.telegram.resource.TelegramBotAPIContainer;

import org.junit.jupiter.api.extension.ExtensionContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.auto.service.AutoService;

import java.util.Map;

@AutoService(TelegramBotApi.class)
public class TelegramBotApi implements Service, WithExternalHostname {
    private TelegramAccount account;
    private TelegramBotAPIContainer container;
    private static final Logger LOG = LoggerFactory.getLogger(TelegramBotApi.class);

    @Override
    public void beforeAll(ExtensionContext context) {
        LOG.debug("Creating new Telegram Bot API validation");
        account(); //initialize account
        container = new TelegramBotAPIContainer(getEnv()); //init container
        container.start();
    }

    public TelegramAccount account() {
        if (account == null) {
            LOG.debug("Creating new Telegram Bot API account");
            account = AccountFactory.create(TelegramAccount.class);
        }
        return account;
    }

    @Override
    public void afterAll(ExtensionContext context) {
        container.stop();
    }

    public Map<String, String> getEnv() {
        return Map.of("TELEGRAM_API_ID", account.getAppId(),
            "TELEGRAM_API_HASH", account.getAppHash());
    }

    @Override
    public String externalHostname() {
        return "localhost";
    }

    public int getPort() {
        return container.getPort();
    }

    public String getLogs() {
        return container.getLogs();
    }
}
