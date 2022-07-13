package software.tnb.prometheus.metrics.validation;

import software.tnb.common.utils.HTTPUtils;
import software.tnb.common.utils.HTTPUtils.Response;
import software.tnb.prometheus.metrics.service.PrometheusMetricsConfiguration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.Map;

public class PrometheusMetricsValidation {

    private static final Logger LOG = LoggerFactory.getLogger(PrometheusMetricsValidation.class);

    private String url;
    private String token;
    private String targetNamespace;
    private HTTPUtils client;

    public enum Operation {
        AVG("avg_over_time"), STDDEV("stddev_over_time"), MAX("max_over_time");

        private String prometheusOperation;

        public String getPrometheusOperation() {
            return prometheusOperation;
        }

        Operation(String prometheusOperation) {
            this.prometheusOperation = prometheusOperation;
        }
    }

    public enum Metric {
        MEMORY("sum by(pod, namespace) (%s(container_memory_working_set_bytes{namespace=\"%s\",pod=~\"%s-.*\"}[%ss]))"),
        CPU("%s(pod:container_cpu_usage:sum{namespace=\"%s\",pod=~\"%s-.*\"}[%ss])");

        private String query;

        public String getQuery() {
            return query;
        }

        Metric(String query) {
            this.query = query;
        }
    }

    public PrometheusMetricsValidation(String url, String token, String targetNamespace) {
        this.url = url;
        this.token = token;
        this.targetNamespace = targetNamespace;
        HTTPUtils.OkHttpClientBuilder okHttpClientBuilder = new HTTPUtils.OkHttpClientBuilder();
        okHttpClientBuilder.trustAllSslClient();
        if (PrometheusMetricsConfiguration.isHttpLogEnabled()) {
            okHttpClientBuilder.log();
        }
        this.client = HTTPUtils.getInstance(okHttpClientBuilder.build());
    }

    /**
     * executes a generic query
     *
     * @param query
     * @return
     */
    public Number executeQuery(String query, long terminatedTime) {
        LOG.info("getting metrics until instant in EPOCH seconds: {}", terminatedTime);
        Response response = client.get(url + "/api/v1/query?query=" + query + "&time=" + terminatedTime,
            Map.of("Authorization", "Bearer " + token));
        JsonParser parser = new JsonParser();
        JsonObject json = parser.parse(response.getBody()).getAsJsonObject();
        if (!json.get("status").getAsString().equalsIgnoreCase("success")) {
            throw new IllegalStateException("metrics reading failed");
        }
        JsonArray result = json.get("data").getAsJsonObject().get("result").getAsJsonArray();
        if (result.size() == 0) {
            return null;
        }
        Number value = result.get(0).getAsJsonObject().get("value").getAsJsonArray().get(1).getAsNumber();
        return value;
    }

    /**
     * executes one of the preset queries
     *
     * @param metric
     * @param op
     * @param vars
     * @return
     */
    public Number executeQuery(Metric metric, Operation operation, String integrationName, long duration,
        long terminatedTime) {
        LOG.info("getting metrics for a period {} seconds long and for the integration {} in the {} namespace",
            duration, integrationName, targetNamespace);
        String query = String.format(metric.query, operation.getPrometheusOperation(), targetNamespace, integrationName, duration);
        return executeQuery(query, terminatedTime);
    }

    /**
     * replacement for convenience of executeQuery(Metric.MEMORY, ...)
     *
     * @param op
     * @param vars
     * @return
     */
    public Number executeQueryForMemory(Operation operation, String integrationName, long duration,
        long terminatedTime) {
        return executeQuery(Metric.MEMORY, operation, integrationName, duration, terminatedTime);
    }

    /**
     * replacement for convenience of executeQuery(Metric.CPU, ...)
     *
     * @param op
     * @param vars
     * @return
     */
    public Number executeQueryForCPU(Operation operation, String integrationName, long duration, long terminatedTime) {
        return executeQuery(Metric.CPU, operation, integrationName, duration, terminatedTime);
    }
}
