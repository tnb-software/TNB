package software.tnb.tempo.validation;

import software.tnb.common.utils.HTTPUtils;

import org.junit.jupiter.api.Assertions;

import org.testcontainers.shaded.org.awaitility.Awaitility;

import java.net.URL;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class LocalTempoValidation extends TempoValidation {
    private final HTTPUtils client;

    public LocalTempoValidation(String gatewayUrl) {
        super(gatewayUrl, "");
        HTTPUtils.OkHttpClientBuilder okHttpClientBuilder = new HTTPUtils.OkHttpClientBuilder();
        okHttpClientBuilder.trustAllSslClient();
        this.client = HTTPUtils.getInstance(okHttpClientBuilder.build());
    }

    @Override
    protected String callWithRetry(URL url) {
        final AtomicReference<HTTPUtils.Response> resp = new AtomicReference<>();
        Awaitility.await("wait for response on " + url).atMost(30, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                resp.set(client.get(url.toString()));
                Assertions.assertTrue(resp.get().isSuccessful(), "Check response code " + url + " : " + resp.get().getResponseCode());
            });
        return resp.get().getBody();
    }
}
