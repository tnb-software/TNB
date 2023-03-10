package software.tnb.jaeger.client;

import software.tnb.common.utils.HTTPUtils;
import software.tnb.common.utils.WaitUtils;
import software.tnb.jaeger.validation.model.Span;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class UnauthenticatedJaegerClient extends BaseJaegerClient implements JaegerClient {

    private static final int MAX_RETRIES_IN_SECONDS = 15;
    private final HTTPUtils apiClient;

    public UnauthenticatedJaegerClient(String queryUrl) {
        super(queryUrl);
        apiClient = HTTPUtils.getInstance(HTTPUtils.trustAllSslClient());
    }

    private String getRawJsonTrace(String traceId) {
        final String url = apiTraceId(traceId);
        WaitUtils.waitFor(() -> apiClient.get(url).isSuccessful(), MAX_RETRIES_IN_SECONDS, 1000L, "Wait for the trace to be elaborated");
        return apiClient.get(url).getBody();
    }

    @Override
    public Map<String, Object> getTrace(String traceId) {
        try {
            return objectMapper.readValue(getRawJsonTrace(traceId), Map.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("unable to read json from response", e);
        }
    }

    @Override
    public List<Span> getSpans(String traceId) {
        final Map data = ((List<Map>) getTrace(traceId).get("data")).get(0);
        String serviceName = (String) ((Map) Optional.ofNullable(((Map) data.get("processes")).get("p1"))
                .orElseGet(() -> Map.of("serviceName", ""))).get("serviceName");
        final List<Span> spans = objectMapper.convertValue(data.get("spans"), new TypeReference<List<Span>>() {
        });
        return spans.stream().map(span -> span.withServiceName(serviceName)).collect(Collectors.toList());
    }
}
