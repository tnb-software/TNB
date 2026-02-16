package software.tnb.tempo.resource.local;

import software.tnb.common.deployment.ContainerDeployable;
import software.tnb.common.deployment.WithDockerImage;
import software.tnb.common.utils.NetworkUtils;
import software.tnb.tempo.service.Tempo;
import software.tnb.tempo.validation.LocalTempoValidation;
import software.tnb.tempo.validation.TempoValidation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.auto.service.AutoService;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@AutoService(Tempo.class)
public class LocalTempo extends Tempo implements ContainerDeployable<TempoContainer>, WithDockerImage {

    private static final Logger LOG = LoggerFactory.getLogger(LocalTempo.class);

    public static final int OTLP_GATEWAY_PORT_GRPC = 4317;
    public static final int OTLP_GATEWAY_PORT_HTTP = 4318;
    public static final int GRAFANA_PORT = 3000;
    public static final int TEMPO_PORT = 3200;
    private static final int HOST_OTLP_GATEWAY_PORT_GRPC = NetworkUtils.getFreePort();

    private TempoContainer container =
        new TempoContainer(image(), List.of(OTLP_GATEWAY_PORT_HTTP, TEMPO_PORT, GRAFANA_PORT),
            Map.of(HOST_OTLP_GATEWAY_PORT_GRPC, OTLP_GATEWAY_PORT_GRPC));

    @Override
    public TempoContainer container() {
        return container;
    }

    @Override
    public void openResources() {
        LOG.info("Grafana URL available at http://localhost:" + container.getMappedPort(GRAFANA_PORT));
    }

    @Override
    public void closeResources() {
        this.validation = null;
    }

    @Override
    public String getDistributorHostname() {
        return getGatewayHostname();
    }

    @Override
    public String getGatewayHostname() {
        return "http://localhost";
    }

    @Override
    public String getGatewayUrl() {
        return getGatewayHostname() + ":" + HOST_OTLP_GATEWAY_PORT_GRPC;
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
