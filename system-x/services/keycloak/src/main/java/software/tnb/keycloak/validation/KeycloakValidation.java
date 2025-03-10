package software.tnb.keycloak.validation;

import static org.junit.jupiter.api.Assertions.assertTrue;

import software.tnb.common.utils.HTTPUtils;
import software.tnb.common.utils.WaitUtils;
import software.tnb.common.validation.Validation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class KeycloakValidation implements Validation {

    private static final Logger LOG = LoggerFactory.getLogger(KeycloakValidation.class);
    private static String serviceHost;
    private static HTTPUtils httpUtils;
    private static final String tokenApiUrl = "%s/realms/%s/protocol/openid-connect/token";
    private static final String realmsUrl = "%s/admin/realms";
    private static final String openIDClientForm = "grant_type=password&client_id=%s&client_secret=%s";

    public KeycloakValidation(String host, int port) {
        if ("localhost".equals(host)) {
            serviceHost = String.format("http://%s:%s", host, port);
        } else {
            serviceHost = String.format("https://%s", host);
        }

        httpUtils = HTTPUtils.getInstance(HTTPUtils.trustAllSslClient()).withRetry();
    }

    public String getServiceHost() {
        return serviceHost;
    }

    public Map<String, String> getTokenHeader(User user, String realm, String clientId, String clientSecret) {
        final String body = String.format(openIDClientForm, clientId, clientSecret)  + "&" + user;

        AtomicReference<HTTPUtils.Response> resp = new AtomicReference<>();

        WaitUtils.waitFor(() -> {
                try {
                    resp.set(postServerRealmsApi(tokenApiUrl, realm, body, "application/x-www-form-urlencoded"));
                } catch (RuntimeException e) {
                    LOG.warn("Waiting for service availability");
                }
                return (resp.get() != null && resp.get().isSuccessful());
            }, 5, 60000
            , "Wait until the call is successful");

        JsonObject jsonResp = new Gson().fromJson(resp.get().getBody(), JsonObject.class);

        assertTrue(jsonResp.isJsonObject());
        assertTrue(jsonResp.has("access_token"));

        return new HashMap<>(Map.of("Authorization", String.format("Bearer %s", jsonResp.get("access_token").getAsString())));
    }

    public HTTPUtils.Response uploadRealm(String url, String bodyRealmContent, Map<String, String> headers) {

        AtomicReference<HTTPUtils.Response> resp = new AtomicReference<>();

        WaitUtils.waitFor(() -> {
                try {
                    resp.set(postServerApi(realmsUrl, bodyRealmContent, "application/json", headers));
                } catch (RuntimeException e) {
                    LOG.warn("Waiting for service availability");
                }
                return (resp.get() != null && resp.get().isSuccessful());
            }, 5, 60000
            , "Wait until the call is successful");

        return resp.get();
    }

    public void deleteRealm(String url, String realmName, Map<String, String> headers) {

        WaitUtils.waitFor(() -> {
                try {
                    postServerApi(realmsUrl, realmName, "application/json", headers);
                } catch (RuntimeException e) {
                    LOG.warn("Waiting for service availability");
                    return false;
                }
                return true;
            }, 5, 60000
            , "Wait until the call is successful");
    }

    private HTTPUtils.Response postServerRealmsApi(String url, String realm, String body, String mediaType) {
        return httpUtils.post(String.format(url, serviceHost, realm),
            RequestBody.create(body, MediaType.parse(mediaType)));
    }

    private void deleteServerRealmsApi(String url, String realm, String mediaType, Map<String, String> headers) {
        httpUtils.delete(String.format(url, serviceHost, realm), headers);
    }

    private HTTPUtils.Response postServerApi(String url, String body, String mediaType, Map<String, String> headers) {
        return httpUtils.post(String.format(url, serviceHost),
            RequestBody.create(body, MediaType.parse(mediaType)), headers);
    }

    public record User(String username, String password) {
        @Override
        public String toString() {
            return "username=" + username + "&password=" + password;
        }
    }
}
