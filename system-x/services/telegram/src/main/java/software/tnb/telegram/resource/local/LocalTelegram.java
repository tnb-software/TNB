package software.tnb.telegram.resource.local;

import software.tnb.common.deployment.ContainerDeployable;
import software.tnb.telegram.service.Telegram;

import com.google.auto.service.AutoService;

@AutoService(Telegram.class)
public class LocalTelegram extends Telegram implements ContainerDeployable<TelegramClientContainer> {
    private final TelegramClientContainer container = new TelegramClientContainer(image(), getEnv());

    @Override
    public String containerServiceVersion() {
        try {
            String v = container().execInContainer("sh", "-c",
                "python3 -c 'import telethon; print(telethon.__version__)' 2>/dev/null")
                .getStdout().trim();
            return v.isEmpty() ? null : v;
        } catch (Exception e) {
            LOG.debug("Failed to detect Telethon version from container", e);
            return null;
        }
    }

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
