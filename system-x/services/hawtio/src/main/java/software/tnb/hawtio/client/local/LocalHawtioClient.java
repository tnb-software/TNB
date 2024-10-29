package software.tnb.hawtio.client.local;

import software.tnb.common.utils.HTTPUtils;
import software.tnb.hawtio.client.HawtioClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class LocalHawtioClient implements HawtioClient {

    private static final Logger LOG = LoggerFactory.getLogger(LocalHawtioClient.class);

    private final String baseUrl;
    private final HTTPUtils client;

    public LocalHawtioClient(String hawtioUrl) {
        this.baseUrl = hawtioUrl;
        this.client = HTTPUtils.getInstance();
    }

    @Override
    public boolean isConnectionAvailable(String jmxUrl) {
        return client.get(getBaseJmxProxyUrl(jmxUrl), false).isSuccessful();
    }

    @Override
    public String jmxQuery(String jmxUrl, String rawQuery) {
        try {
            final HTTPUtils.Response resp = client.post(getBaseJmxProxyUrl(jmxUrl)
                , RequestBody.create(rawQuery, MediaType.parse("application/json")));
            if (resp.isSuccessful()) {
                return resp.getBody();
            } else {
                LOG.warn("response is not successful {} : {} ", resp.getResponseCode(), resp.getBody());
            }
        } catch (Exception e) {
            LOG.warn("ignored exception during response reading: {}", e.getMessage());
        }
        return null;
    }

    private String getBaseJmxProxyUrl(String jmxUrl) {
        return String.format("%s/hawtio%s", baseUrl, jmxUrl);
    }
}
