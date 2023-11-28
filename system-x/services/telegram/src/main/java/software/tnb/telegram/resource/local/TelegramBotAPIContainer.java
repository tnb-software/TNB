package software.tnb.telegram.resource.local;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.util.Map;

public class TelegramBotAPIContainer extends GenericContainer<TelegramBotAPIContainer> {

    public TelegramBotAPIContainer(String image, Map<String, String> env, String[] startupParameters, int port) {
        super(image);
        withNetworkMode("host");
        withEnv(env);
        setCommandParts(startupParameters);
        waitingFor(Wait.forLogMessage(".*Create.*listener \\[address:0.0.0.0\\]\\[port:" + port + "\\].*", 1));
    }

}
