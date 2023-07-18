package software.tnb.apicurio.registry.resource.local;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

public class ApicurioRegistryContainer extends GenericContainer<ApicurioRegistryContainer> {
    public ApicurioRegistryContainer(String image) {
        super(image);
        withExposedPorts(8080);
        waitingFor(Wait.forLogMessage(".*Listening on.*", 1));
    }

    public int getPort() {
        return getMappedPort(8080);
    }
}
