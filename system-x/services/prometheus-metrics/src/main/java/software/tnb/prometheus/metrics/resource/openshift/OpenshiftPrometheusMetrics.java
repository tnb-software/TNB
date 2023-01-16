package software.tnb.prometheus.metrics.resource.openshift;

import software.tnb.common.deployment.OpenshiftDeployable;
import software.tnb.common.openshift.OpenshiftClient;
import software.tnb.prometheus.metrics.service.PrometheusMetrics;
import software.tnb.prometheus.metrics.validation.PrometheusMetricsValidation;

import org.junit.jupiter.api.extension.ExtensionContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.auto.service.AutoService;

import io.fabric8.openshift.api.model.Route;

@AutoService(PrometheusMetrics.class)
public class OpenshiftPrometheusMetrics extends PrometheusMetrics implements OpenshiftDeployable {

    private static final String OPENSHIFT_MONITORING_NS = "openshift-monitoring";
    private static final String THANOS_OCP_NAME = "thanos-querier";
    private static final Logger LOG = LoggerFactory.getLogger(PrometheusMetrics.class);

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        Route route = OpenshiftClient.get(OPENSHIFT_MONITORING_NS).getRoute(THANOS_OCP_NAME);
        String url = "https://" + route.getSpec().getHost();
        String token = OpenshiftClient.get().authorization().getConfiguration().getOauthToken();
        LOG.info("Prometheus url: {}", url);
        validation = new PrometheusMetricsValidation(url, token, OpenshiftClient.get().getNamespace());
    }

    @Override
    public void afterAll(ExtensionContext context) throws Exception {
    }

    @Override
    public boolean isDeployed() {
        return true;
    }

    @Override
    public void undeploy() {
    }

    @Override
    public void openResources() {
    }

    @Override
    public void closeResources() {
    }

    @Override
    public void create() {
    }

    @Override
    public boolean isReady() {
        return true;
    }
}
