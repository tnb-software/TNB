package software.tnb.mllp.resource.local;

import software.tnb.mllp.service.MLLPServer;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.util.Map;

public class MLLPServerContainer extends GenericContainer<MLLPServerContainer> {

    public MLLPServerContainer(String image, int port, Map<String, String> env) {
        super(image);
        withEnv(env);
        withExposedPorts(MLLPServer.LISTENING_PORT);
        waitingFor(Wait.forLogMessage(".*Accepting connections on port.*", 1));
    }
}
