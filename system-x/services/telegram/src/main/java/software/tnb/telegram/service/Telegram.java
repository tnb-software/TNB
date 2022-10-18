package software.tnb.telegram.service;

import software.tnb.common.account.AccountFactory;
import software.tnb.common.service.Service;
import software.tnb.telegram.account.TelegramAccount;
import software.tnb.telegram.resource.TelegramContainer;
import software.tnb.telegram.validation.TelegramValidation;

import org.junit.jupiter.api.extension.ExtensionContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.auto.service.AutoService;

import java.util.HashMap;
import java.util.Map;

@AutoService(Telegram.class)
public class Telegram implements Service {
    private TelegramAccount account;
    private TelegramValidation validation;
    private TelegramContainer container;
    private static final Logger LOG = LoggerFactory.getLogger(Telegram.class);

    @Override
    public void beforeAll(ExtensionContext context) {
        LOG.debug("Creating new Telegram validation");
        account(); //initialize account
        container = new TelegramContainer(getEnv()); //init container
        validation = new TelegramValidation(container); //create validation with container
        container.start();
    }

    public TelegramAccount account() {
        if (account == null) {
            LOG.debug("Creating new Telegram account");
            account = AccountFactory.create(TelegramAccount.class);
        }
        return account;
    }

    @Override
    public void afterAll(ExtensionContext context) {
        container.stop();
    }

    public TelegramValidation validation() {
        return validation;
    }

    public Map<String, String> getEnv() {
        Map<String, String> env = new HashMap<>();
        env.put("TELEGRAM_DC_ID", account.getDcId());
        env.put("TELEGRAM_DC_IP", account.getDcIp());
        env.put("TELEGRAM_API_ID", account.getAppId());
        env.put("TELEGRAM_API_HASH", account.getAppHash());
        env.put("TELEGRAM_SESSION", account.getSessionString());
        env.put("TELEGRAM_USERNAME", account.getUsername());
        return env;
    }
}
