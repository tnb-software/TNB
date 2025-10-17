package software.tnb.tempo.validation;

import software.tnb.common.openshift.OpenshiftClient;
import software.tnb.common.validation.Validation;
import software.tnb.tempo.validation.model.FoundTrace;
import software.tnb.tempo.validation.model.SearchResult;
import software.tnb.tempo.validation.model.Span;
import software.tnb.tempo.validation.model.Trace;

import org.junit.jupiter.api.Assertions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.shaded.org.awaitility.Awaitility;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import io.fabric8.kubernetes.client.http.HttpClient;
import io.fabric8.kubernetes.client.http.HttpRequest;
import io.fabric8.kubernetes.client.http.HttpResponse;

public class TempoValidation implements Validation {

    private static final Logger LOG = LoggerFactory.getLogger(TempoValidation.class);
    public static final String GATEWAY_TEMPO_BASE_URL = "/api/traces/v1/application/tempo";

    private final HttpClient client;
    private final ObjectMapper objectMapper;
    private final String token;
    private final String searchUrl;
    private final String traceUrl;

    public TempoValidation(String gatewayUrl, String token) {
        this.token = token;
        this.client = OpenshiftClient.get().authorization().getHttpClient();
        this.searchUrl = gatewayUrl + GATEWAY_TEMPO_BASE_URL + "/api/search";
        this.traceUrl = gatewayUrl + GATEWAY_TEMPO_BASE_URL + "/api/traces";
        this.objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    /**
     * Search on tempo storage using TraceQL
     *
     * @param query String, TraceQL query
     * @return {@link SearchResult}, the result
     */
    public SearchResult search(String query) {
        try {
            LOG.debug("search traces with query {}", query);
            HttpResponse<String> resp = callWithRetry(new URL("%s?%s".formatted(searchUrl, URLEncoder.encode("q=%s".formatted(query)
                , StandardCharsets.UTF_8))));
            LOG.debug("query result:\n{}", resp.body());
            return objectMapper.readValue(resp.body(), new TypeReference<SearchResult>() { });
        } catch (IOException e) {
            throw new RuntimeException("unable to read value from " + searchUrl);
        }
    }

    /**
     * Returns the map with spanId as key and @{@link software.tnb.tempo.validation.model.Span} as value
     * @param traceId String, the trace id
     * @return Map
     */
    public Map<String, Span> getFullTrace(String traceId) {
        try {
            final Map<String, Span> fullTrace = new HashMap<>();
            LOG.debug("search for trace {}", traceId);
            HttpResponse<String> resp = callWithRetry(new URL("%s/%s".formatted(traceUrl, traceId)));
            LOG.debug("trace result:\n{}", resp.body());
            Trace trace = objectMapper.readValue(resp.body(), new TypeReference<Trace>() { });
            trace.getBatches().forEach(batch -> batch.getScopeSpans().forEach(scopeSpan -> scopeSpan.getSpans()
                .forEach(span -> fullTrace.put(span.getSpanId(), span))));
            return fullTrace;
        } catch (IOException e) {
            throw new RuntimeException("unable to read json from response", e);
        }
    }

    public List<Span> getSpans(String traceId) {
        return getFullTrace(traceId).values().stream().toList();
    }

    public List<String> getTraces(String serviceName) {
        final List<String> traces = new ArrayList<>();

        AtomicReference<List<FoundTrace>> result = new AtomicReference<>();
        Awaitility.await("await for traces to be elaborated")
            .atMost(30, TimeUnit.SECONDS)
            .pollInterval(2, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                SearchResult res = search("{ resource.service.name = \"" + serviceName + "\" }");
                result.set(res.getTraces());
                Assertions.assertFalse(res.getTraces().isEmpty());
            });

        result.get().forEach(trace -> traces.add(trace.getTraceID()));

        return traces;
    }

    private HttpResponse<String> callWithRetry(URL url) {
        final HttpRequest.Builder proxyReqBuilder = client.newHttpRequestBuilder()
            .header("Authorization", "Bearer %s".formatted(token))
            .url(url);
        final HttpRequest proxyReq = proxyReqBuilder.build();
        final AtomicReference<HttpResponse<String>> resp = new AtomicReference<>();
        Awaitility.await("wait for response on " + url).atMost(30, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                resp.set(client.sendAsync(proxyReq, String.class).get());
                Assertions.assertTrue(resp.get().isSuccessful(), "Check response code " + url + " : " + resp.get().code());
            });
        return resp.get();
    }
}
