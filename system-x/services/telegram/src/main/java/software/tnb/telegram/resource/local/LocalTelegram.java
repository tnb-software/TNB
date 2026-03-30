package software.tnb.telegram.resource.local;

import software.tnb.common.deployment.ContainerDeployable;
import software.tnb.telegram.service.Telegram;

import com.google.auto.service.AutoService;

@AutoService(Telegram.class)
public class LocalTelegram extends Telegram implements ContainerDeployable<TelegramClientContainer> {
    private final TelegramClientContainer container = new TelegramClientContainer(image(), getEnv());

    @Override
    protected String getHttpEndpoint() {
        return container.getHttpEndpoint();
    }

    @Override
    public void openResources() {
    }

    @Override
    public void closeResources() {
    }

    @Override
    public TelegramClientContainer container() {
        return container;
    }
}
