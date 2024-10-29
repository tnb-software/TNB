package software.tnb.hawtio.client.openshift;

import software.tnb.common.openshift.OpenshiftClient;
import software.tnb.hawtio.client.HawtioClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;

import io.fabric8.kubernetes.client.http.HttpClient;
import io.fabric8.kubernetes.client.http.HttpRequest;
import io.fabric8.kubernetes.client.http.HttpResponse;

public class OpenshiftHawtioClient implements HawtioClient {

    private static final Logger LOG = LoggerFactory.getLogger(OpenshiftHawtioClient.class);

    private final HttpClient client;
    private final String baseUrl;

    public OpenshiftHawtioClient(String hawtioUrl) {
        this.client = OpenshiftClient.get().authorization().getHttpClient();
        this.baseUrl = hawtioUrl;
    }

    @Override
    public boolean isConnectionAvailable(String jmxUrl) {
        try {
            final HttpRequest proxyReq = client.newHttpRequestBuilder()
                .url(getBaseJmxProxyUrl(jmxUrl)).build();
            final HttpResponse<String> resp = client.sendAsync(proxyReq, String.class).get();
            return resp.isSuccessful();
        } catch (Exception e) {
            LOG.warn("ignored exception during connectivity test: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public String jmxQuery(String jmxUrl, String rawQuery) {
        try {
            final HttpRequest proxyReq = client.newHttpRequestBuilder().url(getBaseJmxProxyUrl(jmxUrl))
                .post("application/json", rawQuery).build();

            final HttpResponse<String> resp = client.sendAsync(proxyReq, String.class).get();
            if (resp.isSuccessful()) {
                return resp.body();
            } else {
                LOG.warn("response is not successful {} : {} ", resp.code(), resp.body());
            }
        } catch (Exception e) {
            LOG.warn("ignored exception during response reading: {}", e.getMessage());
        }
        return null;
    }

    private URL getBaseJmxProxyUrl(String jmxUrl) throws MalformedURLException {
        return new URL(baseUrl + jmxUrl);
    }
}
