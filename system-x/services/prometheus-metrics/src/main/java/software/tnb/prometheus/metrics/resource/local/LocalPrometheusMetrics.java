package software.tnb.prometheus.metrics.resource.local;

import org.junit.jupiter.api.extension.ExtensionContext;

import com.google.auto.service.AutoService;

import software.tnb.prometheus.metrics.service.PrometheusMetrics;

@AutoService(PrometheusMetrics.class)
public class LocalPrometheusMetrics extends PrometheusMetrics {

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
    }

    @Override
    public void afterAll(ExtensionContext context) throws Exception {
    }
}
