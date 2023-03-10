package software.tnb.jaeger.resource.local;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.util.Map;

public class JaegerContainer extends GenericContainer<JaegerContainer> {

    public JaegerContainer(String image, Map<String, String> env) {
        super(image);
        withEnv(env);
        withNetworkMode("host");
        waitingFor(Wait.forLogMessage(".*Channel Connectivity change to IDLE\",\"system\":\"grpc\".*", 1));
    }
}
