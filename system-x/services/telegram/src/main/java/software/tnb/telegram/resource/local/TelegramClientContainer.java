package software.tnb.telegram.resource.local;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.util.Map;

public class TelegramClientContainer extends GenericContainer<TelegramClientContainer> {

    private static final int HTTP_PORT = 8080;

    public TelegramClientContainer(String image, Map<String, String> env) {
        super(image);
        withEnv(env);
        withExposedPorts(HTTP_PORT);
        waitingFor(Wait.forHttp("/health").forPort(HTTP_PORT));
    }

    public String getHttpEndpoint() {
        return "http://" + getHost() + ":" + getMappedPort(HTTP_PORT);
    }
}
