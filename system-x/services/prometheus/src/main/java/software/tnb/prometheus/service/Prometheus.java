package software.tnb.prometheus.service;

import software.tnb.common.account.NoAccount;
import software.tnb.common.client.NoClient;
import software.tnb.common.service.Service;
import software.tnb.prometheus.validation.PrometheusMetricsValidation;

public abstract class Prometheus extends Service<NoAccount, NoClient, PrometheusMetricsValidation> {

    public abstract String getUrl();
}
