package software.tnb.jaeger.service.configuration;

import software.tnb.common.service.configuration.ServiceConfiguration;

public class JaegerConfiguration extends ServiceConfiguration {

    public interface WithPort {
        int portNumber();
    }

    public enum QueryPort implements WithPort {
        GRPC(16685),
        HTTP(16686);
        private int port;

        QueryPort(int port) {
            this.port = port;
        }

        @Override
        public int portNumber() {
            return this.port;
        }
    }

    public enum CollectorPort implements WithPort {
        HTTP_ZIPKIN(9411),
        TLS_GRPC_JAEGER(14250),
        HTTP_C_TCHAN_TRFT(14267),
        HTTP_C_BINARY_TRFT(14268),
        GRPC_OTLP(4317),
        HTTP_OTLP(4318);
        private int port;

        CollectorPort(int port) {
            this.port = port;
        }

        @Override
        public int portNumber() {
            return this.port;
        }
    }

    protected enum ConfigurationKeys {
        CONF_PROMETHEUS_URL
    }

    public String getPrometheusUrl() {
        return get(ConfigurationKeys.CONF_PROMETHEUS_URL.toString(), String.class);
    }

    public JaegerConfiguration withPrometheusUrl(final String prometheusUrl) {
        set(ConfigurationKeys.CONF_PROMETHEUS_URL.toString(), prometheusUrl);
        return this;
    }
}
