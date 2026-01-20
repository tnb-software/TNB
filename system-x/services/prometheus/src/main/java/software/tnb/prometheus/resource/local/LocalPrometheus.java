package software.tnb.prometheus.resource.local;

import software.tnb.common.deployment.ContainerDeployable;
import software.tnb.common.deployment.WithDockerImage;
import software.tnb.prometheus.service.Prometheus;

import com.google.auto.service.AutoService;

@AutoService(Prometheus.class)
public class LocalPrometheus extends Prometheus implements ContainerDeployable<PrometheusContainer>, WithDockerImage {
    private final PrometheusContainer container = new PrometheusContainer(image());

    @Override
    public String getUrl() {
        //necessary to get URL at configuration time
        if (!container.isCreated()) {
            deploy();
        }
        return String.format("http://%s:%d", container.getHost(), container.getPort());
    }

    @Override
    public String defaultImage() {
        return "quay.io/prometheus/prometheus";
    }

    @Override
    public void openResources() {
    }

    @Override
    public void closeResources() {
    }

    @Override
    public PrometheusContainer container() {
        return container;
    }
}
