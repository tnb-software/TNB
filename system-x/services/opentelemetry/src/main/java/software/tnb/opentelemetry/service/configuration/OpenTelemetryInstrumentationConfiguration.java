package software.tnb.opentelemetry.service.configuration;

import software.tnb.common.service.configuration.ServiceConfiguration;

import org.yaml.snakeyaml.Yaml;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class OpenTelemetryInstrumentationConfiguration extends ServiceConfiguration {

    private Map<String, Object> exporter = new LinkedHashMap<>();
    private Map<String, Object> java = new LinkedHashMap<>();

    public OpenTelemetryInstrumentationConfiguration() {
        withDefaultJava();
    }

    public OpenTelemetryInstrumentationConfiguration withExporterEndpoint(String endpoint) {
        exporter.put("endpoint", endpoint);
        return this;
    }

    public OpenTelemetryInstrumentationConfiguration withDefaultJava() {
        final List<Map<String, Object>> javaEnvs = new LinkedList<>();
        final Map<String, Object> otelServiceName = new HashMap<>();
        final Map<String, Object> otelServiceNameValueFrom = new HashMap<>();
        final Map<String, Object> otelServiceNameValueFromFieldRef = new HashMap<>();
        otelServiceName.put("name", "OTEL_SERVICE_NAME");
        otelServiceNameValueFromFieldRef.put("fieldPath", "metadata.labels['app']");
        otelServiceNameValueFrom.put("fieldRef", otelServiceNameValueFromFieldRef);
        otelServiceName.put("valueFrom", otelServiceNameValueFrom);
        javaEnvs.add(otelServiceName);
        java.put("env", javaEnvs);
        return this;
    }

    public Map<String, Object> getCollectorConfigurationAsMap() {
        final Map<String, Object> config = new LinkedHashMap<>();
        if (!exporter.isEmpty()) {
            config.put("exporter", exporter);
        }
        config.put("java", java);
        return config;
    }

    @Override
    public String toString() {
        Yaml confYaml = new Yaml();
        StringWriter stringWriter = new StringWriter();
        confYaml.dump(getCollectorConfigurationAsMap(), stringWriter);
        return stringWriter.toString();
    }
}
