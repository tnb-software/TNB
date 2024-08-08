package software.tnb.rest.resource.local;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.util.List;

public class RestContainer extends GenericContainer<RestContainer> {

    public RestContainer(String image, List<Integer> ports) {
        super(image);
        this.withExposedPorts(ports.toArray(new Integer[0]));
        this.waitingFor(Wait.forLogMessage(".*loading inflector config.*", 1));
    }
}
