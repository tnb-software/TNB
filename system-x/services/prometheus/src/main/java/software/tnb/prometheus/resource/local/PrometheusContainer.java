package software.tnb.prometheus.resource.local;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

public class PrometheusContainer extends GenericContainer<PrometheusContainer> {

    private static final int PORT = 9090;

    public PrometheusContainer(String image) {
        super(image);
        withNetworkMode("host");
        waitingFor(Wait.forLogMessage(".*Server is ready to receive web requests.*", 1));
    }

    public int getPort() {
        return PORT;
    }
}
