package software.tnb.tempo.resource.local;

import software.tnb.common.deployment.ContainerDeployable;
import software.tnb.common.deployment.WithDockerImage;
import software.tnb.tempo.service.Tempo;
import software.tnb.tempo.validation.LocalTempoValidation;
import software.tnb.tempo.validation.TempoValidation;

import com.google.auto.service.AutoService;

import java.util.List;
import java.util.Optional;

@AutoService(Tempo.class)
public class LocalTempo extends Tempo implements ContainerDeployable<TempoContainer>, WithDockerImage {

    public static final int OTLP_GATEWAY_PORT_GRPC = 4317;
    public static final int OTLP_GATEWAY_PORT_HTTP = 4318;
    public static final int TEMPO_PORT = 3200;

    TempoContainer container;

    @Override
    public TempoContainer container() {
        return container;
    }

    @Override
    public void openResources() {

    }

    @Override
    public void closeResources() {

    }

    @Override
    public void deploy() {
        container = new TempoContainer(image(), List.of(OTLP_GATEWAY_PORT_GRPC, OTLP_GATEWAY_PORT_HTTP, TEMPO_PORT));

        ContainerDeployable.super.deploy();
    }

    @Override
    public String getDistributorHostname() {
        return getGatewayHostname();
    }

    @Override
    public String getGatewayHostname() {
        return "http://" + container.getHost();
    }

    @Override
    public String getGatewayUrl() {
        return getGatewayHostname() + ":" + container.getMappedPort(OTLP_GATEWAY_PORT_GRPC);
    }

    @Override
    public String getGatewayExternalUrl() {
        return getGatewayHostname() + ":" + container.getMappedPort(OTLP_GATEWAY_PORT_HTTP);
    }

    public String getTempoUrl() {
        return getGatewayHostname() + ":" + container.getMappedPort(TEMPO_PORT);
    }

    @Override
    public TempoValidation validation() {
        validation = Optional.ofNullable(validation)
            .orElseGet(() -> new LocalTempoValidation(getTempoUrl()));
        return validation;
    }

    @Override
    public String defaultImage() {
        return "quay.io/fuse_qe/otel-lgtm:0.17.0";
    }
}
