package software.tnb.prometheus.metrics.service;

import software.tnb.common.account.NoAccount;
import software.tnb.common.client.NoClient;
import software.tnb.common.service.Service;
import software.tnb.prometheus.metrics.validation.PrometheusMetricsValidation;

public abstract class PrometheusMetrics extends Service<NoAccount, NoClient, PrometheusMetricsValidation> {

    public abstract String getUrl();
}
