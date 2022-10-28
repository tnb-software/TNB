package software.tnb.telegram.resource;

import org.testcontainers.containers.GenericContainer;

import java.util.Map;

public class TelegramBotAPIContainer extends GenericContainer<TelegramBotAPIContainer> {

    private static final String IMAGE = System.getProperty("telegram-bot-api.image", "quay.io/fuse_qe/telegram-bot-api:latest");
    private static final String PORT = System.getProperty("telegram-bot-api.port", "8081");
    private static final String VERBOSITY = System.getProperty("telegram-bot-api.verbosity", "9");

    public TelegramBotAPIContainer(Map<String, String> env) {
        super(IMAGE);
        withNetworkMode("host");
        withEnv(env);
        setCommandParts(new String[] {"--local", "--http-port", PORT
            , "--dir", "/home/telegram-bot-api/telegram/working"
            , "--temp-dir", "/home/telegram-bot-api/telegram/temp-files"
            , "--verbosity", VERBOSITY});
    }

    public int getPort() {
        return Integer.valueOf(PORT);
    }
}
