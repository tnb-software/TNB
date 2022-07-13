package software.tnb.prometheus.metrics.service;

import software.tnb.common.config.Configuration;

public class PrometheusMetricsConfiguration extends Configuration {

    public static final String HTTP_LOG_ENABLED = "prometheus.metrics.http.log.enabled";

    public static boolean isHttpLogEnabled() {
        return getBoolean(HTTP_LOG_ENABLED, false);
    }
}
