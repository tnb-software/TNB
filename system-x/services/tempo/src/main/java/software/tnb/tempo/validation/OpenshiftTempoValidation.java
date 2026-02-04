package software.tnb.tempo.validation;

import software.tnb.common.openshift.OpenshiftClient;

import org.junit.jupiter.api.Assertions;

import org.testcontainers.shaded.org.awaitility.Awaitility;

import java.net.URL;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import io.fabric8.kubernetes.client.http.HttpClient;
import io.fabric8.kubernetes.client.http.HttpRequest;
import io.fabric8.kubernetes.client.http.HttpResponse;

public class OpenshiftTempoValidation extends TempoValidation {
    public static final String GATEWAY_TEMPO_BASE_URL = "/api/traces/v1/application/tempo";

    private final HttpClient client;
    private final String token;

    public OpenshiftTempoValidation(String gatewayUrl, String token) {
        super(gatewayUrl, GATEWAY_TEMPO_BASE_URL);
        this.token = token;
        this.client = OpenshiftClient.get().authorization().getHttpClient();
    }

    @Override
    protected String callWithRetry(URL url) {
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
        return resp.get().body();
    }
}
