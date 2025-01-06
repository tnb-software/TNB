package software.tnb.prometheus.metrics.validation;

import software.tnb.common.utils.HTTPUtils;
import software.tnb.common.utils.HTTPUtils.Response;
import software.tnb.common.validation.Validation;
import software.tnb.prometheus.metrics.configuration.PrometheusMetricsConfiguration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class PrometheusMetricsValidation implements Validation {

    private static final Logger LOG = LoggerFactory.getLogger(PrometheusMetricsValidation.class);

    private String url;
    private String token;
    private String targetNamespace;
    private HTTPUtils client;

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
     * executes a generic query using a <i>query</i> request with the <i>time</i> parameter
     *
     * @param query
     * @param time
     * @return the <i>gson</i> object got parsing the json response
     * @throws IllegalStateException if the status field is not success
     */
    public JsonObject executeQuery(String query, long time) {
        LOG.info("getting metrics until instant in EPOCH seconds: {}", time);
        Response response = client.get(url + "/api/v1/query?query=" + query + "&time=" + time,
            Map.of("Authorization", "Bearer " + token));
        JsonObject json = JsonParser.parseString(response.getBody()).getAsJsonObject();
        if (!json.get("status").getAsString().equalsIgnoreCase("success")) {
            throw new IllegalStateException("The metric query failed");
        }
        return json;
    }

    /**
     * executes a generic query using a <i>query</i> request
     *
     * @param query
     * @return the <i>gson</i> object got parsing the json response
     * @throws IllegalStateException if the status field is not success
     */
    public JsonObject executeQuery(String query) {
        Response response = client.get(url + "/api/v1/query?query=" + query,
            Map.of("Authorization", "Bearer " + token));
        JsonObject json = JsonParser.parseString(response.getBody()).getAsJsonObject();
        if (!json.get("status").getAsString().equalsIgnoreCase("success")) {
            throw new IllegalStateException("The metric query failed");
        }
        return json;
    }

    /**
     * executes a generic query using a <i>query_range</i> request
     * 
     * @param query the query of this <i>query_range</i> request
     * @param start start of the time interval of sample data you are interested in
     * @param end end of the time interval of sample data you are interested in
     * @param step step in seconds for the time interval sampling
     * @return the <i>gson</i> object got parsing the json response
     * @throws IllegalStateException if the status field is not success
     */
    public JsonObject executeQueryRange(String query, long start, long end, long step) {
        Response response = client.get(
            String.format("%s/api/v1/query_range?query=%s&start=%d&end=%d&step=%d", url, query, start, end, step),
            Map.of("Authorization", "Bearer " + token));
        JsonObject json = JsonParser.parseString(response.getBody()).getAsJsonObject();
        if (!json.get("status").getAsString().equalsIgnoreCase("success")) {
            throw new IllegalStateException("The metric query failed");
        }
        return json;
    }

    /**
     * executes one of the preset queries on target pod specified by the integration
     * name.<br>The metrics retrieved are typical pod metrics.<br>
     * You can use the real podName or a regular expression to specify the pod you
     * are interested in.<br>
     * The kind of request used is <i>query_range</i>.
     *
     * @param metric preset pod related query of a <i>query_range</i> request
     * @param podNamePattern a regular expression to find the pod(s) you are interested in, through his name (if the pod is only one) of the shared
     * part of their names (if there are many pods)
     * @param start start of the time interval of sample data you are interested in
     * @param end end of the time interval of sample data you are interested in
     * @param step step in seconds for the time interval sampling
     * @return the result is <b>expected to be a single vector</b> or a matrix with just a single vector
     */
    public List<InstantValue> executeQuery(PodMetric metric, String podNamePattern, long start, long end, long step) {
        return executeQueryRangeSingleMetricWithVector(metric.query, start, end, step, targetNamespace, podNamePattern);
    }

    /**
     * executes one of the preset queries on target pod specified by the integration
     * name.<br>This is used for special metrics like i.e.: <i>kube_pod_info</i>.<br>
     * You can use the real podName or a regular expression to specify the pod you
     * are interested in.
     * The kind of request used is <i>query</i>.
     * 
     * @param metric preset pod related query of a query request (not <i>query_range</i>)
     * @param podNamePattern a regular expression to find the pod(s) you are interested in, through his name (if the pod is only one) of the shared
     * part of their names (if there are many pods)
     * @param start start of the time interval of sample data you are interested in (it's the <i>time</i> parameter of the underlying query request
     * minus the time interval)
     * @param end end of the time interval of sample data you are interested in (the <i>time</i> parameter of the underlying query request)
     * @return
     */
    public List<MetricData> executeQuery(OtherPodMetric metric, String podNamePattern, long start, long end) {
        return executeQueryToGetMatrix(metric.query, end, List.of("pod", "node"), targetNamespace, podNamePattern,
            end - start);
    }

    /**
     * Executes a preset query composed with a function to aggregate instant vector.
     * 
     * @param operation a function to apply at every sampled instant to the related values retrieved by this <i>query_range</i> request (i.e.: sum
     * or average)
     * @param metric preset pod related query of a <i>query_range</i> request
     * @param podNamePattern a regular expression to find the pod(s) you are interested in, through his name (if the pod is only one) of the shared
     * part of their names (if there are many pods)
     * @param start start of the time interval of sample data you are interested in
     * @param end end of the time interval of sample data you are interested in
     * @param step step in seconds for the time interval sampling
     * @return the result <b>expected to be a single vector</b> or a matrix with just a single vector
     */
    public List<InstantValue> executeQuery(Operation operation, PodMetric metric, String podNamePattern, long start,
        long end, long step) {
        String query = operation.function + "(" + metric.query + ")";
        return executeQueryRangeSingleMetricWithVector(query, start, end, step, targetNamespace, podNamePattern);
    }

    /**
     * executes one of the preset queries on target node
     *
     * @param metric preset node related query of a <i>query_range</i> request
     * @param nodeName the name of the node that you are interested in
     * @param start start of the time interval of sample data you are interested in
     * @param end end of the time interval of sample data you are interested in
     * @param step step in seconds for the time interval sampling
     * @return the result is <b>expected to be a single vector</b> or a matrix with just a single vector
     */
    public List<InstantValue> executeQuery(NodeMetric metric, String nodeName, long start, long end, long step) {
        return executeQueryRangeSingleMetricWithVector(metric.query, start, end, step, nodeName);
    }

        /**
     * Executes a generic query using <i>query_range</i> request and returning a vector
     * 
     * @param query generic query, which can contain placeholders with the <i>String.format</i> convention, of a <i>query_range</i> request
     * @param start start of the time interval of sample data you are interested in
     * @param end end of the time interval of sample data you are interested in
     * @param step step in seconds for the time interval sampling
     * @param params values to apply with <i>String.format</i> to the query
     * @return the result is <b>expected to be a single vector</b> or a matrix with just a single vector
     */
    public List<InstantValue> executeQueryRangeSingleMetricWithVector(String query, long start, long end, long step,
        Object... params) {
        String queryToExecute = String.format(query, params);
        JsonObject json = executeQueryRange(queryToExecute, start, end, step);
        JsonArray result = json.get("data").getAsJsonObject().get("result").getAsJsonArray();
        if (result.size() == 0) {
            return Collections.emptyList();
        }
        JsonArray data = result.get(0).getAsJsonObject().get("values").getAsJsonArray();
        Iterator<JsonElement> it = data.iterator();
        List<InstantValue> instantValues = new ArrayList<>(data.size());
        while (it.hasNext()) {
            JsonArray jsonValues = (JsonArray) it.next();
            instantValues.add(new InstantValue(jsonValues.get(1).getAsNumber(), jsonValues.get(0).getAsLong()));
        }
        return instantValues;
    }

    public List<InstantValue> executeQueryToGetVector(String query, Object... params) {
        String queryToExecute = String.format(query, params);
        JsonObject json = executeQuery(queryToExecute);
        JsonArray result = json.get("data").getAsJsonObject().get("result").getAsJsonArray();
        if (result.size() == 0) {
            return Collections.emptyList();
        }
        JsonArray value = result.get(0).getAsJsonObject().get("value").getAsJsonArray();
        return Collections.singletonList(new InstantValue(value.get(1).getAsNumber(), value.get(0).getAsLong()));
    }

    public List<InstantValue> executeQueryToGetVector(String query, long time, Object... params) {
        String queryToExecute = String.format(query, params);
        JsonObject json = executeQuery(queryToExecute, time);
        JsonArray result = json.get("data").getAsJsonObject().get("result").getAsJsonArray();
        if (result.size() == 0) {
            return Collections.emptyList();
        }
        JsonArray value = result.get(0).getAsJsonObject().get("value").getAsJsonArray();
        return Collections.singletonList(new InstantValue(value.get(1).getAsNumber(), value.get(0).getAsLong()));
    }

    /**
     * Executes generic query returning a matrix using a <i>query</i> request
     * 
     * @param query generic query, which can contain placeholders with the <i>String.format</i> convention, of a query request (not
     * <i>query_range</i>)
     * @param time the time that you are interested in
     * @param metricPropertiesToExtract list of metric metadata names that you are interested to retrieve from the original result
     * @param params values to apply with <i>String.format</i> to the query
     * @return the matrix result transformed in list of <i>MetricData</i>
     */
    public List<MetricData> executeQueryToGetMatrix(String query, long time, List<String> metricPropertiesToExtract, Object... params) {
        String queryToExecute = String.format(query, params);
        JsonObject json = executeQuery(queryToExecute, time);
        JsonArray result = json.get("data").getAsJsonObject().get("result").getAsJsonArray();
        if (result.size() == 0) {
            return Collections.emptyList();
        }
        List<MetricData> metricDataList = new ArrayList<>(result.size());
        Iterator<JsonElement> iterator = result.iterator();
        while (iterator.hasNext()) {
            JsonObject o = ((JsonElement) iterator.next()).getAsJsonObject();
            JsonObject metricJson = o.get("metric").getAsJsonObject();
            Map<String, String> metric = new HashMap<>();
            metricPropertiesToExtract.forEach(p -> {
                if (metricJson.has(p)) {
                    metric.put(p, metricJson.get(p).getAsString());
                }
            });
            JsonArray values = o.get("values").getAsJsonArray();
            List<InstantValue> instantValues = new ArrayList<>(values.size());
            Iterator<JsonElement> valuesIt = values.iterator();
            while (valuesIt.hasNext()) {
                JsonArray instantValueJson = (JsonArray) valuesIt.next();
                InstantValue instantValue = new InstantValue(instantValueJson.get(1).getAsNumber(), instantValueJson.get(0).getAsLong());
                instantValues.add(instantValue);
            }
            metricDataList.add(new MetricData(metric, instantValues));
        }
        return metricDataList;
    }

    public enum Operation {
        SUM("sum"), AVG("avg");

        String function;

        Operation(String function) {
            this.function = function;
        }
    }

    public enum PodMetric {
        MEMORY("sum(container_memory_working_set_bytes{namespace=\"%s\",pod=~\"%s\"})"),
        CPU("pod:container_cpu_usage:sum{namespace=\"%s\",pod=~\"%s\"}");

        String query;

        PodMetric(String query) {
            this.query = query;
        }
    }

    public enum OtherPodMetric {
        POD_INFO("node_namespace_pod:kube_pod_info:{namespace=\"%s\",pod=~\"%s\"}[%ss]");

        String query;

        OtherPodMetric(String query) {
            this.query = query;
        }
    }

    public enum NodeMetric {
        MEMORY("node_memory_MemTotal_bytes - node_memory_MemAvailable_bytes{instance=\"%s\"}"),
        CPU("instance:node_cpu:ratio{instance=\"%s\"}");

        String query;

        NodeMetric(String query) {
            this.query = query;
        }
    }

    public String getUrl() {
        return this.url;
    }
}
