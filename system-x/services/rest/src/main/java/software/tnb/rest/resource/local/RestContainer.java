package software.tnb.rest.resource.local;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

public class RestContainer extends GenericContainer<RestContainer> {

    public RestContainer(String image, int port) {
        super(image);
        this.withExposedPorts(port);
        this.waitingFor(Wait.forLogMessage(".*loading inflector config.*", 1));
    }
}
