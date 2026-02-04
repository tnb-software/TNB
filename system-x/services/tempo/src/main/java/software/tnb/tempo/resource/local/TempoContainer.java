package software.tnb.tempo.resource.local;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.util.List;
import java.util.Map;

public class TempoContainer extends GenericContainer<TempoContainer> {
    public TempoContainer(String image, List<Integer> ports, Map<Integer, Integer> fixedPorts) {
        super(image);
        this.withExposedPorts(ports.toArray(new Integer[0]));
        fixedPorts.forEach(this::addFixedExposedPort);
        this.waitingFor(Wait.forLogMessage(".*The OpenTelemetry collector and the Grafana LGTM stack are up and running.*\\s", 1));
    }
}
