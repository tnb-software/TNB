package software.tnb.horreum.validation;

import software.tnb.common.utils.HTTPUtils;
import software.tnb.common.utils.HTTPUtils.Response;
import software.tnb.horreum.account.HorreumAccount;
import software.tnb.horreum.service.HorreumConfiguration;
import software.tnb.horreum.validation.generated.ApiClient;
import software.tnb.horreum.validation.generated.ApiException;
import software.tnb.horreum.validation.generated.JSON;
import software.tnb.horreum.validation.generated.api.ConfigServiceApi;
import software.tnb.horreum.validation.generated.api.DefaultApi;
import software.tnb.horreum.validation.generated.model.Access;
import software.tnb.horreum.validation.generated.model.Change;
import software.tnb.horreum.validation.generated.model.KeycloakConfig;
import software.tnb.horreum.validation.generated.model.Test;
import software.tnb.horreum.validation.generated.model.Variable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class HorreumValidation {

    public static final MediaType FORM_URLENC = MediaType.get("application/x-www-form-urlencoded; charset=utf-8");

    private static final Logger LOG = LoggerFactory.getLogger(HorreumValidation.class);
    private final DefaultApi defaultApi;
    private ConfigServiceApi configServiceApi;
    private HorreumAccount horreumAccount;

    public HorreumValidation(HorreumAccount horreumAccount) {
        HTTPUtils.OkHttpClientBuilder okHttpClientBuilder = new HTTPUtils.OkHttpClientBuilder();
        okHttpClientBuilder.trustAllSslClient();
        if (HorreumConfiguration.isHttpLogEnabled()) {
            okHttpClientBuilder.log();
        }
        ApiClient apiClient = new ApiClient(okHttpClientBuilder.build());
        String basePath = HorreumConfiguration.getUrl();
        apiClient.setBasePath(basePath);
        apiClient.setVerifyingSsl(false);
        defaultApi = new DefaultApi(apiClient);
        configServiceApi = new ConfigServiceApi(apiClient);
        this.horreumAccount = horreumAccount;
    }

    public Integer postRunData(String description, String owner, String schema, String start, String stop, String testName,
        String access, Object data) throws Exception {
        String runDataIdAsString =
            defaultApi.runServiceAddRunFromDataWithHttpInfo(start, stop, testName, data, Access.fromValue(access), description, owner, schema,
                horreumAccount.token(testName)).getData();
        return Integer.parseInt(runDataIdAsString);
    }

    public String getToken(String testName) throws Exception {
        KeycloakConfig keycloakConfig = configServiceApi.configServiceKeycloak();
        String oauthToken = getHorreumOauthToken(String.format("%s/realms/%s/protocol/openid-connect/token",
                keycloakConfig.getUrl(), keycloakConfig.getRealm()), keycloakConfig.getClientId(), horreumAccount.username(testName),
            horreumAccount.password(testName));
        return oauthToken;
    }

    private String getHorreumOauthToken(String url, String clientId, String username, String password) {
        HTTPUtils client = HTTPUtils.getInstance(HTTPUtils.trustAllSslClient());
        RequestBody body = RequestBody.create(FORM_URLENC,
            String.format("username=%s&password=%s&grant_type=password&client_id=%s", username, password, clientId)
        );
        Response response = client.post(url, body);
        JSON json = configServiceApi.getApiClient().getJSON();
        Map resp = json.deserialize(response.getBody(), Map.class);
        return (String) resp.get("access_token");
    }

    public List<Change> detectChanges(String testName, String accessToken, Integer runDataId) throws Exception {
        defaultApi.getApiClient().addDefaultHeader("Authorization", "Bearer " + accessToken);
        Test test = defaultApi.testServiceGetByNameOrId(testName);
        List<Variable> variables = defaultApi.alertingServiceVariables(test.getId());
        List<Change> changes = variables.stream().flatMap(var -> {
            try {
                return defaultApi.alertingServiceChanges(var.getId(), null).stream();
            } catch (ApiException e) {
                throw new RuntimeException(e);
            }
        }).filter(change -> change.getDataset().getRunId().equals(runDataId)).collect(Collectors.toList());
        return changes;
    }
}
