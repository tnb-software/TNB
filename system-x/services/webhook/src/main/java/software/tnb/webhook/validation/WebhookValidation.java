package software.tnb.webhook.validation;

import software.tnb.common.utils.HTTPUtils;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class WebhookValidation {
    private static final Logger LOG = LoggerFactory.getLogger(WebhookValidation.class);
    private static final String WEBHOOK_ENDPOINT = "https://webhook.site";

    private final HTTPUtils client;
    private String token;

    public WebhookValidation() {
        client = HTTPUtils.getInstance();
    }

    public String createEndpoint() {
        LOG.debug("Creating new webhook endpoint");
        token = new JSONObject(client.post(WEBHOOK_ENDPOINT + "/token", RequestBody.create(MediaType.parse("application/json"), "{}")).getBody())
            .getString("uuid");
        final String endpoint = String.format("%s/%s", WEBHOOK_ENDPOINT, token);
        LOG.debug("Webhook endpoint: {}", endpoint);
        return endpoint;
    }

    public void clearEndpoint() {
        clearEndpoint(token);
    }

    public void clearEndpoint(String token) {
        final String url = String.format("%s/token/%s/request", WEBHOOK_ENDPOINT, token);
        LOG.debug("Cleaning endpoint {}", url);
        client.delete(url);
    }

    public void deleteEndpoint() {
        final String url = String.format("%s/token/%s", WEBHOOK_ENDPOINT, token);
        LOG.debug("Deleting webhook endpoint {}", url);
        client.delete(url);
    }

    public List<JSONObject> getRequests() {
        return getRequests(token);
    }

    public List<JSONObject> getRequests(String token) {
        return getRequests(token, null);
    }

    public List<JSONObject> getRequests(RequestQueryParameters queryParameters) {
        return getRequests(token, queryParameters);
    }

    public List<JSONObject> getRequests(String token, RequestQueryParameters queryParameters) {
        String parameters = queryParameters == null ? "" : queryParameters.toString();
        final String url = String.format("%s/token/%s/requests%s", WEBHOOK_ENDPOINT, token, parameters);
        return new JSONObject(client.get(url).getBody()).getJSONArray("data")
            .toList().stream().map(o -> new JSONObject((Map) o)).collect(Collectors.toList());
    }
}
