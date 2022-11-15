package software.tnb.telegram.resource.local;

import org.testcontainers.containers.GenericContainer;

import java.util.Map;

public class TelegramClientContainer extends GenericContainer<TelegramClientContainer> {

    public TelegramClientContainer(String image, Map<String, String> env) {
        super(image);
        withEnv(env);
    }

}
