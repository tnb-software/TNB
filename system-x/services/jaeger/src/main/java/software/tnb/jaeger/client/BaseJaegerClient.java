package software.tnb.jaeger.client;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

class BaseJaegerClient {

    private static final String API_TRACES = "/api/traces";
    protected final String queryUrl;

    protected final ObjectMapper objectMapper;

    BaseJaegerClient(final String queryUrl) {
        this.queryUrl = queryUrl;
        objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    protected String apiTraceId(String traceId) {
        return String.format("%s%s/%s", queryUrl, API_TRACES, traceId);
    }

    protected String apiTraces(String serviceName) {
        return String.format("%s%s?service=%s", queryUrl, API_TRACES, serviceName);
    }

}
