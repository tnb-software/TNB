package software.tnb.prometheus.metrics.resource.local;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.util.Map;

public class PrometheusContainer extends GenericContainer<PrometheusContainer> {

    private static final int PORT = 9090;

    public PrometheusContainer(String image, Map<String, String> env) {
        super(image);
        withEnv(env);
        withNetworkMode("host");
        waitingFor(Wait.forLogMessage(".*Server is ready to receive web requests.*", 1));
    }

    public int getPort() {
        return PORT;
    }
}
