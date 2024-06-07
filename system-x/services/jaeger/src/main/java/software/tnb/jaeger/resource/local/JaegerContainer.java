package software.tnb.jaeger.resource.local;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.time.Duration;
import java.util.Map;

public class JaegerContainer extends GenericContainer<JaegerContainer> {

    public JaegerContainer(String image, Map<String, String> env) {
        super(image);
        withEnv(env);
        withNetworkMode("host");
        waitingFor(Wait.forLogMessage(".*ListenSocket created.*", 1).withStartupTimeout(Duration.ofMinutes(2)));
    }
}
