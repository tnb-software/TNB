package software.tnb.prometheus.metrics.resource.local;

import software.tnb.common.deployment.Deployable;
import software.tnb.common.deployment.WithDockerImage;
import software.tnb.prometheus.metrics.service.PrometheusMetrics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.auto.service.AutoService;

import java.util.HashMap;
import java.util.Map;

@AutoService(PrometheusMetrics.class)
public class LocalPrometheusMetrics extends PrometheusMetrics implements Deployable, WithDockerImage {

    private static final Logger LOG = LoggerFactory.getLogger(LocalPrometheusMetrics.class);
    private PrometheusContainer container;

    @Override
    public String getUrl() {
        //necessary to get URL at configuration time
        if (container == null) {
            deploy();
        }
        return String.format("http://%s:%d", container.getHost(), container.getPort());
    }

    @Override
    public String defaultImage() {
        return "quay.io/prometheus/prometheus";
    }

    @Override
    public void deploy() {
        if (container == null) {
            LOG.info("Starting Prometheus container");
            container = new PrometheusContainer(image(), env());
            container.start();
            LOG.info("Prometheus container started");
        }
    }

    @Override
    public void undeploy() {
        if (container != null) {
            LOG.info("Stopping Prometheus container");
            container.stop();
        }
    }

    @Override
    public void openResources() {

    }

    @Override
    public void closeResources() {

    }

    public Map<String, String> env() {
        Map<String, String> env = new HashMap<>();
        return env;
    }
}
