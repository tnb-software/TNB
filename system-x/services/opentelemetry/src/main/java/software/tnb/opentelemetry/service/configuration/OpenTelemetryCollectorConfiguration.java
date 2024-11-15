package software.tnb.opentelemetry.service.configuration;

import software.tnb.common.service.configuration.ServiceConfiguration;

import org.yaml.snakeyaml.Yaml;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class OpenTelemetryCollectorConfiguration extends ServiceConfiguration {

    protected static final String KEY_PORTS = "ports";
    protected static final String KEY_REPLICAS = "replicas";
    private Map<String, Object> receivers = new LinkedHashMap<>();
    private Map<String, Object> processors = new LinkedHashMap<>();
    private Map<String, Object> exporters = new LinkedHashMap<>();
    private Map<String, Object> service = new LinkedHashMap<>();

    public OpenTelemetryCollectorConfiguration() {
        withDefaultReceivers()
            .withDefaultProcessors()
            .withDefaultExporters()
            .withDefaultServices();
    }

    public OpenTelemetryCollectorConfiguration withDefaultReceivers() {
        Map<String, Object> protocols = new HashMap<>();
        protocols.put("grpc", new HashMap<>());
        protocols.put("http", new HashMap<>());
        Map<String, Object> otlp = new HashMap<>();
        otlp.put("protocols", protocols);
        receivers.put("otlp", otlp);
        return this;
    }

    public OpenTelemetryCollectorConfiguration withDefaultProcessors() {
        Map<String, Object> batch = new HashMap<>();
        batch.put("send_batch_size", 10000);
        batch.put("timeout", "10s");
        processors.put("batch", batch);
        return this;
    }

    public OpenTelemetryCollectorConfiguration withDefaultExporters() {
        exporters.put("debug", new HashMap<>());
        return this;
    }

    public OpenTelemetryCollectorConfiguration withDefaultServices() {
        Map<String, Object> metrics = new LinkedHashMap<>();
        Map<String, Object> traces = new LinkedHashMap<>();
        Map<String, Object> logs = new LinkedHashMap<>();
        Map<String, Object> pipelines = new LinkedHashMap<>();

        addServiceOnPipeline(metrics, "receivers", "otlp");
        addServiceOnPipeline(metrics, "processors", "batch");
        addServiceOnPipeline(metrics, "exporters", "debug");

        addServiceOnPipeline(traces, "receivers", "otlp");
        addServiceOnPipeline(traces, "processors", "batch");
        addServiceOnPipeline(traces, "exporters", "debug");

        addServiceOnPipeline(logs, "receivers", "otlp");
        addServiceOnPipeline(logs, "processors", "batch");
        addServiceOnPipeline(logs, "exporters", "debug");

        pipelines.put("metrics", metrics);
        pipelines.put("traces", traces);
        pipelines.put("logs", logs);
        service.put("pipelines", pipelines);
        return this;
    }

    //service are metrics,traces,logs
    //element are receivers,processors,exporters
    //item are string
    private void addServiceOnPipeline(final Map<String, Object> service, String element, String item) {
        List<String> nullSafeElement = (List<String>) Optional.ofNullable(service.get(element))
            .filter(o -> o instanceof List).orElseGet(LinkedList::new);
        if (!nullSafeElement.contains(item)) {
            nullSafeElement.add(item);
        }
        service.put(element, nullSafeElement);
    }

    public OpenTelemetryCollectorConfiguration addPort(Map<String, Object> portConfig) {
        final List<Map<String, Object>> ports = this.getPorts();
        ports.add(portConfig);
        withPorts(ports);
        return this;
    }

    public OpenTelemetryCollectorConfiguration withPorts(List<Map<String, Object>> ports) {
        this.set(KEY_PORTS, ports);
        return this;
    }

    public List<Map<String, Object>> getPorts() {
        return Optional.ofNullable(this.get(KEY_PORTS, List.class)).orElseGet(LinkedList::new);
    }

    public OpenTelemetryCollectorConfiguration withReplicas(int replicas) {
        this.set(KEY_REPLICAS, replicas);
        return this;
    }

    public int getReplicas() {
        return Optional.ofNullable(this.get(KEY_REPLICAS, Integer.class)).orElse(1);
    }

    public OpenTelemetryCollectorConfiguration withPrometheusExporter(Map<String, Object> prometheusConfiguration) {
        exporters.put("prometheus", prometheusConfiguration);
        addServiceOnPipeline((Map) ((Map) service.get("pipelines")).get("metrics"), "exporters", "prometheus");
        return this;
    }

    public OpenTelemetryCollectorConfiguration withPrometheusExporter() {

        addPort(Map.of("name", "promexporter"
            , "port", 8889
            , "targetPort", 8889
            , "protocol", "TCP"));

        Map<String, Object> promConfig = new HashMap<>();
        promConfig.put("endpoint", "0.0.0.0:8889");
        promConfig.put("metric_expiration", "180m");
        return withPrometheusExporter(promConfig);
    }

    public OpenTelemetryCollectorConfiguration withOtlpTracesExporter(Map<String, Object> otlpTracesConfiguration) {
        exporters.put("otlp/traces", otlpTracesConfiguration);
        addServiceOnPipeline((Map) ((Map) service.get("pipelines")).get("traces"), "exporters", "otlp/traces");
        return this;
    }

    public OpenTelemetryCollectorConfiguration withOtlpTracesExporter(String otlpEndpoint) {
        Map<String, Object> otlpTracesConfiguration = new HashMap<>();
        otlpTracesConfiguration.put("endpoint", otlpEndpoint);
        otlpTracesConfiguration.put("tls", Map.of("insecure", true));
        return withOtlpTracesExporter(otlpTracesConfiguration);
    }

    public Map<String, Object> getCollectorConfigurationAsMap() {
        return Map.of("receivers", receivers
            , "processors", processors
            , "exporters", exporters
            , "service", service);
    }

    @Override
    public String toString() {
        Yaml confYaml = new Yaml();
        StringWriter stringWriter = new StringWriter();
        confYaml.dump(getCollectorConfigurationAsMap(), stringWriter);
        return stringWriter.toString();
    }
}
