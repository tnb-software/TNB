package software.tnb.telegram.resource.local;

import software.tnb.common.deployment.Deployable;
import software.tnb.telegram.service.TelegramBotApi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.auto.service.AutoService;

@AutoService(TelegramBotApi.class)
public class LocalTelegramBotAPI extends TelegramBotApi implements Deployable {

    private static final Logger LOG = LoggerFactory.getLogger(LocalTelegramBotAPI.class);

    private TelegramBotAPIContainer container;

    @Override
    public String externalHostname() {
        return container.getHost();
    }

    @Override
    protected String getWorkingDir() {
        return "/home/telegram-bot-api/telegram/working";
    }

    @Override
    protected String getUploadDir() {
        return "/home/telegram-bot-api/telegram/temp-files";
    }

    @Override
    public String getLogs() {
        return container.getLogs();
    }

    @Override
    public void deploy() {
        LOG.debug("Starting Telegram Bot API container");
        container = new TelegramBotAPIContainer(image(), getEnv(), startupParams(), getPort());
        container.start();
    }

    @Override
    public void undeploy() {
        container.stop();
    }

    @Override
    public void openResources() {

    }

    @Override
    public void closeResources() {

    }
}
