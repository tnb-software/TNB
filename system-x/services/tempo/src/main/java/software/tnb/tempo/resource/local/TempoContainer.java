package software.tnb.tempo.resource.local;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.util.List;

public class TempoContainer extends GenericContainer<TempoContainer> {
    public TempoContainer(String image, List<Integer> ports) {
        super(image);
        this.withExposedPorts(ports.toArray(new Integer[0]));
        this.waitingFor(Wait.forLogMessage(".*The OpenTelemetry collector and the Grafana LGTM stack are up and running.*\\s", 1));
    }
}
