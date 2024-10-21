package software.tnb.webhook.validation;

import software.tnb.common.utils.HTTPUtils;
import software.tnb.common.validation.Validation;
import software.tnb.webhook.validation.model.WebhookSiteRequest;
import software.tnb.webhook.validation.model.WebhookSiteRequests;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class WebhookValidation implements Validation {
    private static final Logger LOG = LoggerFactory.getLogger(WebhookValidation.class);
    private static final String WEBHOOK_ENDPOINT = "https://webhook.site";

    private final HTTPUtils client;
    private String token;

    public WebhookValidation() {
        client = HTTPUtils.getInstance();
    }

    public String createEndpoint() {
        if (token == null) {
            LOG.debug("Creating new webhook endpoint");
            final String body = client.post(WEBHOOK_ENDPOINT + "/token",
                RequestBody.create("{}", MediaType.parse("application/json"))).getBody();
            token = new JSONObject(body).getString("uuid");
        }
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
        if (token != null) {
            final String url = String.format("%s/token/%s", WEBHOOK_ENDPOINT, token);
            LOG.debug("Deleting webhook endpoint {}", url);
            client.delete(url);
            token = null;
        }
    }

    public List<WebhookSiteRequest> getRequests() {
        return getRequests(token);
    }

    public List<WebhookSiteRequest> getRequests(String token) {
        return getRequests(token, null);
    }

    public List<WebhookSiteRequest> getRequests(RequestQueryParameters queryParameters) {
        return getRequests(token, queryParameters);
    }

    public List<WebhookSiteRequest> getRequests(String token, RequestQueryParameters queryParameters) {
        String parameters = queryParameters == null ? "" : queryParameters.toString();
        final String url = String.format("%s/token/%s/requests%s", WEBHOOK_ENDPOINT, token, parameters);
        String body = client.get(url).getBody();

        Gson gson = new GsonBuilder().registerTypeAdapter(
            LocalDateTime.class, (JsonDeserializer<LocalDateTime>) (json, type, jsonDeserializationContext)
                -> LocalDateTime.parse(json.getAsJsonPrimitive().getAsString(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).create();

        return gson.fromJson(body, WebhookSiteRequests.class).data();
    }

    public void deleteRequest(String requestId) {
        deleteRequest(token, requestId);
    }

    public void deleteRequest(String token, String requestId) {
        final String url = String.format("%s/token/%s/request/%s", WEBHOOK_ENDPOINT, token, requestId);
        LOG.debug("Deleting request {}", url);
        client.delete(url);
    }
}
