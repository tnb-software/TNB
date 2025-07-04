package software.tnb.observability.validation;

import software.tnb.common.openshift.OpenshiftClient;
import software.tnb.common.validation.Validation;
import software.tnb.observability.validation.model.Span;
import software.tnb.observability.validation.model.Trace;

import org.junit.jupiter.api.Assertions;

import org.awaitility.Awaitility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import io.fabric8.kubernetes.client.http.HttpClient;
import io.fabric8.kubernetes.client.http.HttpRequest;
import io.fabric8.kubernetes.client.http.HttpResponse;

public class ObservabilityValidation implements Validation {

    private static final Logger LOG = LoggerFactory.getLogger(ObservabilityValidation.class);

    private final HttpClient client;
    private final String baseUrl;
    private final ObjectMapper objectMapper;

    public ObservabilityValidation(String tempoStackName) {
        this.client = OpenshiftClient.get().authorization().getHttpClient();
        this.baseUrl = "%s/api/proxy/plugin/distributed-tracing-console-plugin/backend/proxy/%s/%s/application/api"
            .formatted(OpenshiftClient.get().getConsoleUrl(), OpenshiftClient.get().getNamespace(), tempoStackName);
        this.objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public boolean isTraceEndpointAvailable() {
        try {
            return getResponseFromMenuPage().isSuccessful();
        } catch (Exception e) {
            LOG.warn("ignored exception during connectivity test: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Returns the map with spanId as key and @{@link software.tnb.observability.validation.model.Span} as value
     * @param traceId String, the trace id
     * @return Map
     */
    public Map<String, Span> getFullTrace(String traceId) {
        try {
            final Map<String, Span> fullTrace = new HashMap<>();
            final HttpResponse<String> resp = callWithRetry(new URL("%s/traces/%s".formatted(baseUrl, traceId)), getResponseFromMenuPage());
            final Trace trace = objectMapper.readValue(resp.body(), Trace.class);
            trace.getBatches().forEach(batch -> batch.getScopeSpans().forEach(scopeSpan -> scopeSpan.getSpans()
                .forEach(span -> fullTrace.put(span.getSpanId(), span))));
            return fullTrace;
        } catch (JsonProcessingException | MalformedURLException e) {
            throw new RuntimeException("unable to read json from response", e);
        }
    }

    public List<Span> getSpans(String traceId) {
        return getFullTrace(traceId).values().stream().toList();
    }

    public List<String> getTraces(String serviceName) {
        final List<String> traces = new ArrayList<>();

        AtomicReference<Map> result = new AtomicReference<>();
        Awaitility.await("await for trace to be elaborated")
            .atMost(30, TimeUnit.SECONDS)
            .pollInterval(2, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                final HttpResponse<String> resp = callWithRetry(new URL(baseUrl + "/search?q="
                        + URLEncoder.encode("{ resource.service.name = \"" + serviceName + "\" }", StandardCharsets.UTF_8))
                    , getResponseFromMenuPage());
                result.set(objectMapper.readValue(resp.body(), Map.class));
                Assertions.assertFalse(((List<?>) result.get().get("traces")).isEmpty());
            });

        ((List<Map<String, Object>>) result.get().get("traces")).forEach(trace -> traces.add((String) trace.get("traceID")));

        return traces;
    }

    private HttpResponse<String> callWithRetry(URL url, HttpResponse<String> landingPage) {
        final Map<String, String> secHeaders = getSecurityHeaders(landingPage);
        final HttpRequest.Builder proxyReqBuilder = client.newHttpRequestBuilder()
            .url(url);
        proxyReqBuilder.header("Cookie", String.join("; ", secHeaders.entrySet().stream()
            .map(e -> e.getKey() + "=" + e.getValue())
            .toList()));
        final HttpRequest proxyReq = proxyReqBuilder.build();
        final AtomicReference<HttpResponse<String>> resp = new AtomicReference<>();
        Awaitility.await("wait for response on " + url).atMost(30, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                resp.set(client.sendAsync(proxyReq, String.class).get());
                Assertions.assertTrue(resp.get().isSuccessful(), "Check response code " + url + " : " + resp.get().code());
            });
        return resp.get();
    }

    private HttpResponse<String> getResponseFromMenuPage() {
        final HttpRequest proxyReq;
        try {
            proxyReq = client.newHttpRequestBuilder()
                    .url(new URL("%s/observe/traces".formatted(OpenshiftClient.get().getConsoleUrl()))).build();
            return client.sendAsync(proxyReq, String.class).get();
        } catch (MalformedURLException | ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private Map<String, String> getSecurityHeaders(HttpResponse<String> response) {
        final Map<String, String> headers = new HashMap<>();
        headers.put("openshift-session-token", OpenshiftClient.get().getOauthToken());
        final List<String> setCookie = response.headers("set-cookie");
        if (setCookie != null) {
            setCookie.forEach(header -> headers.putAll(Arrays.stream(header.split(";"))
                    .map(String::trim)
                    .filter(h -> !h.toLowerCase().startsWith("path"))
                    .filter(h -> !h.toLowerCase().startsWith("httponly"))
                    .filter(h -> !h.toLowerCase().startsWith("secure"))
                    .filter(h -> !h.toLowerCase().startsWith("samesite"))
                    .map(h -> h.split("="))
                    .collect(Collectors.toMap(strings -> strings[0], strings -> strings[1]))));
        }
        return headers;
    }
}
