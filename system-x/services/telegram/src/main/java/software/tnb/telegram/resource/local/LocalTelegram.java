package software.tnb.telegram.resource.local;

import software.tnb.common.deployment.Deployable;
import software.tnb.telegram.service.Telegram;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.auto.service.AutoService;

import java.io.IOException;

@AutoService(Telegram.class)
public class LocalTelegram extends Telegram implements Deployable {

    private static final Logger LOG = LoggerFactory.getLogger(LocalTelegram.class);
    private TelegramClientContainer container;

    @Override
    public String execInContainer(String... commands) {
        try {
            return container.execInContainer(commands).getStdout();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deploy() {
        LOG.debug("Starting Telegram client container");
        container = new TelegramClientContainer(image(), getEnv());
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
