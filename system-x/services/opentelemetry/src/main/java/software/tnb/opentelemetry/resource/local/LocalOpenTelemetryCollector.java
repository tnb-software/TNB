package software.tnb.opentelemetry.resource.local;

import software.tnb.common.deployment.ContainerDeployable;
import software.tnb.common.deployment.WithDockerImage;
import software.tnb.opentelemetry.service.OpenTelemetryCollector;

import org.junit.jupiter.api.extension.ExtensionContext;

import com.google.auto.service.AutoService;

import java.util.function.Supplier;

@AutoService(OpenTelemetryCollector.class)
public class LocalOpenTelemetryCollector extends OpenTelemetryCollector implements ContainerDeployable<OpenTelemetryCollectorContainer>,
    WithDockerImage {
    // configuration is initialized only after the instance is created, so keep a ref to the supplier and initialize the container only when needed
    private final Supplier<OpenTelemetryCollectorContainer> containerSupplier = () ->
        new OpenTelemetryCollectorContainer(image(), getConfiguration().toString());
    private OpenTelemetryCollectorContainer container;

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        runTempoIfNeeded(context);
        ContainerDeployable.super.beforeAll(context);
    }

    @Override
    public void afterAll(ExtensionContext context) throws Exception {
        cleanTempoIfNeeded(context);
        ContainerDeployable.super.afterAll(context);
    }

    @Override
    public void openResources() {
    }

    @Override
    public void closeResources() {
    }

    @Override
    public String getGrpcEndpoint() {
        return "http://localhost:" + getConfiguration().getGrpcReceiverPort();
    }

    @Override
    public String getHttpEndpoint() {
        return "http://localhost:" + getConfiguration().getHttpReceiverPort();
    }

    @Override
    public String defaultImage() {
        return "registry.redhat.io/rhosdt/opentelemetry-collector-rhel8:latest";
    }

    @Override
    public OpenTelemetryCollectorContainer container() {
        if (container == null) {
            container = containerSupplier.get();
        }
        return container;
    }

    @Override
    public String getLogs() {
        return ContainerDeployable.super.getLogs();
    }
}
