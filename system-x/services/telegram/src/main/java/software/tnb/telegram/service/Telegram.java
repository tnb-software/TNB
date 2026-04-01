package software.tnb.telegram.service;

import software.tnb.common.client.NoClient;
import software.tnb.common.deployment.WithDockerImage;
import software.tnb.common.service.Service;
import software.tnb.telegram.account.TelegramAccount;
import software.tnb.telegram.validation.TelegramValidation;

import java.util.HashMap;
import java.util.Map;

public abstract class Telegram extends Service<TelegramAccount, NoClient, TelegramValidation> implements WithDockerImage {
    @Override
    public String defaultImage() {
        return "quay.io/fuse_qe/telegram-client:latest";
    }

    protected abstract String getHttpEndpoint();

    public TelegramValidation validation() {
        if (validation == null) {
            validation = new TelegramValidation(getHttpEndpoint());
        }
        return validation;
    }

    public Map<String, String> getEnv() {
        Map<String, String> env = new HashMap<>();
        env.put("TELEGRAM_API_ID", account().getAppId());
        env.put("TELEGRAM_API_HASH", account().getAppHash());
        env.put("TELEGRAM_SESSION", account().getSessionString());
        env.put("TELEGRAM_USERNAME", account().getUsername());
        return env;
    }
}
