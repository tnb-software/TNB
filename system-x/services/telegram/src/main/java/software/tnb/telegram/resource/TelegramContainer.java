package software.tnb.telegram.resource;

import org.testcontainers.containers.GenericContainer;

import java.util.Map;

public class TelegramContainer extends GenericContainer<TelegramContainer> {
    private static final String IMAGE = System.getProperty("telegram.image", "quay.io/fuse_qe/telegram-client:latest");

    public TelegramContainer(Map<String, String> env) {
        super(IMAGE);
        withEnv(env);
    }
}
