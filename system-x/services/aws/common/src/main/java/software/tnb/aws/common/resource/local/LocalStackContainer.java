package software.tnb.aws.common.resource.local;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

public class LocalStackContainer extends GenericContainer<LocalStackContainer> {
    private final int port;

    public LocalStackContainer(String image, int port) {
        super(image);
        this.port = port;
        withExposedPorts(port);
        waitingFor(Wait.forLogMessage(".*Ready\\..*", 1));
    }

    public int getPort() {
        return getMappedPort(port);
    }
}
