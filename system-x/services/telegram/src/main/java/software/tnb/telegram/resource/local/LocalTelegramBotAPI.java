package software.tnb.telegram.resource.local;

import software.tnb.common.deployment.ContainerDeployable;
import software.tnb.telegram.service.TelegramBotApi;

import com.google.auto.service.AutoService;

@AutoService(TelegramBotApi.class)
public class LocalTelegramBotAPI extends TelegramBotApi implements ContainerDeployable<TelegramBotAPIContainer> {
    private final TelegramBotAPIContainer container = new TelegramBotAPIContainer(image(), getEnv(), startupParams(), getPort());

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
    public TelegramBotAPIContainer container() {
        return container;
    }

    @Override
    public String getLogs() {
        return ContainerDeployable.super.getLogs();
    }

    @Override
    public void openResources() {
    }

    @Override
    public void closeResources() {
    }
}
