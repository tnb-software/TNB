package software.tnb.opentelemetry.resource.local;

import software.tnb.common.deployment.Deployable;
import software.tnb.common.deployment.WithDockerImage;
import software.tnb.opentelemetry.service.OpenTelemetryCollector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.auto.service.AutoService;

import java.util.Optional;

@AutoService(OpenTelemetryCollector.class)
public class LocalOpenTelemetryCollector extends OpenTelemetryCollector implements Deployable, WithDockerImage {
    private static final Logger LOG = LoggerFactory.getLogger(LocalOpenTelemetryCollector.class);
    private OpenTelemetryCollectorContainer container;

    @Override
    public void deploy() {
        LOG.info("Starting OpenTelemetryCollector container");
        container = new OpenTelemetryCollectorContainer(image(), getConfiguration().toString());
        container.start();
        LOG.info("OpenTelemetryCollector container started");
    }

    @Override
    public void undeploy() {
        if (container != null) {
            LOG.info("Stopping OpenTelemetryCollector container");
            container.stop();
        }
    }

    @Override
    public void openResources() {
    }

    @Override
    public void closeResources() {
    }

    @Override
    public String getLog() {
        return container.getLogs();
    }

    @Override
    public String getGrpcEndpoint() {
        return "http://localhost:" + Optional.ofNullable(container).map(c -> c.getMappedPort(4317)).orElse(4317);
    }

    @Override
    public String getHttpEndpoint() {
        return "http://localhost:" + Optional.ofNullable(container).map(c -> c.getMappedPort(4318)).orElse(4318);
    }

    @Override
    public String defaultImage() {
        return "registry.redhat.io/rhosdt/opentelemetry-collector-rhel8:latest";
    }
}
