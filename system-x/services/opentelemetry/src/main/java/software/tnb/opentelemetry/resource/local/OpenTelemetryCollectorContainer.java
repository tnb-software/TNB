package software.tnb.opentelemetry.resource.local;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.images.builder.Transferable;

import java.time.Duration;

public class OpenTelemetryCollectorContainer extends GenericContainer<OpenTelemetryCollectorContainer> {

    public OpenTelemetryCollectorContainer(String image, String collectorConfiguration) {
        super(image);
        withCopyToContainer(Transferable.of(collectorConfiguration), "/conf/collector.yaml");
        setCommandParts(new String[] {"--config=/conf/collector.yaml"});
        withNetworkMode("host");
        waitingFor(Wait.forLogMessage(".*Everything is ready. Begin running and processing data.*", 1)
            .withStartupTimeout(Duration.ofSeconds(20)));
    }
}
