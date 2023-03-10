package software.tnb.prometheus.metrics.service;

import software.tnb.common.service.Service;
import software.tnb.prometheus.metrics.validation.PrometheusMetricsValidation;

public abstract class PrometheusMetrics implements Service {

    protected PrometheusMetricsValidation validation;

    public PrometheusMetricsValidation validation() {
        return validation;
    }

    public abstract String getUrl();
}
