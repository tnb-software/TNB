package software.tnb.tempo.validation;

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

public abstract class TempoValidation implements Validation {

    private static final Logger LOG = LoggerFactory.getLogger(TempoValidation.class);
    private final ObjectMapper objectMapper;

    private final String searchUrl;
    private final String traceUrl;

    public TempoValidation(String gatewayUrl, String gatewayTempoBaseUrl) {
        this.searchUrl = gatewayUrl + gatewayTempoBaseUrl + "/api/search";
        this.traceUrl = gatewayUrl + gatewayTempoBaseUrl + "/api/traces";
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
            String body = callWithRetry(new URL("%s?q=%s".formatted(searchUrl, URLEncoder.encode("%s".formatted(query)
                , StandardCharsets.UTF_8))));
            LOG.debug("query result:\n{}", body);
            return objectMapper.readValue(body, new TypeReference<SearchResult>() {
            });
        } catch (IOException e) {
            throw new RuntimeException("unable to read value from " + searchUrl, e);
        }
    }

    /**
     * Returns the map with spanId as key and @{@link software.tnb.tempo.validation.model.Span} as value
     *
     * @param traceId String, the trace id
     * @return Map
     */
    public Map<String, Span> getFullTrace(String traceId) {
        try {
            final Map<String, Span> fullTrace = new HashMap<>();
            LOG.debug("search for trace {}", traceId);
            String body = callWithRetry(new URL("%s/%s".formatted(traceUrl, traceId)));
            LOG.debug("trace result:\n{}", body);
            Trace trace = objectMapper.readValue(body, new TypeReference<Trace>() {
            });
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

    protected abstract String callWithRetry(URL url);
}
