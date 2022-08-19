package software.tnb.mllp.resource.local;

import software.tnb.mllp.service.MllpServer;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.util.Map;

public class MllpServerContainer extends GenericContainer<MllpServerContainer> {

    public MllpServerContainer(String image, int port, Map<String, String> env) {
        super(image);
        withEnv(env);
        withExposedPorts(MllpServer.LISTENING_PORT);
        waitingFor(Wait.forLogMessage(".*Accepting connections on port.*", 1));
    }
}
