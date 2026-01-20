package software.tnb.jaeger.service;

import software.tnb.common.account.NoAccount;
import software.tnb.common.service.ConfigurableService;
import software.tnb.jaeger.client.JaegerClient;
import software.tnb.jaeger.service.configuration.JaegerConfiguration;
import software.tnb.jaeger.validation.JaegerValidation;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public abstract class Jaeger extends ConfigurableService<NoAccount, JaegerClient, JaegerValidation, JaegerConfiguration> {
    public abstract String getCollectorUrl(JaegerConfiguration.CollectorPort port);

    public abstract String getQueryUrl(JaegerConfiguration.QueryPort port);

    public abstract String getExternalUrl();

    protected JaegerValidation validation;

    @Override
    protected void defaultConfiguration() {
    }

    protected Map<String, String> env() {
        Map<String, String> env = new HashMap<>();
        env.put("COLLECTOR_OTLP_ENABLED", "true");
        env.put("SPAN_STORAGE_TYPE", "memory");
        env.put("JAEGER_DISABLED", "false");
        if (StringUtils.isNotEmpty(getConfiguration().getPrometheusUrl())) {
            env.put("PROMETHEUS_SERVER_URL", getConfiguration().getPrometheusUrl());
            env.put("METRICS_STORAGE_TYPE", "prometheus");
        }
        return env;
    }

    public JaegerValidation validation() {
        return Optional.ofNullable(validation).orElseGet(() -> validation = new JaegerValidation(client()));
    }
}
