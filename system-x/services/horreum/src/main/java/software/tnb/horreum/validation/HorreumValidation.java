package software.tnb.horreum.validation;

import software.tnb.common.utils.HTTPUtils;
import software.tnb.common.validation.Validation;
import software.tnb.horreum.account.HorreumAccount;
import software.tnb.horreum.configuration.HorreumConfiguration;
import software.tnb.horreum.tools.PrettyPrinter;
import software.tnb.horreum.validation.generated.ApiClient;
import software.tnb.horreum.validation.generated.ApiResponse;
import software.tnb.horreum.validation.generated.api.RunApi;
import software.tnb.horreum.validation.generated.auth.ApiKeyAuth;
import software.tnb.horreum.validation.generated.model.Access;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.reflect.TypeToken;

import java.lang.reflect.Type;

import okhttp3.Call;

public class HorreumValidation implements Validation {
    private static final Logger LOG = LoggerFactory.getLogger(HorreumValidation.class);

    private static final PrettyPrinter prettyPrinter = new PrettyPrinter();

    private final RunApi runApi;
    private HorreumAccount horreumAccount;

    /**
     * Horreum API KEY specification: https://horreum.hyperfoil.io/docs/tasks/api-keys/
     *
     * @param horreumAccount
     */
    public HorreumValidation(HorreumAccount horreumAccount) {
        HTTPUtils.OkHttpClientBuilder okHttpClientBuilder = new HTTPUtils.OkHttpClientBuilder();
        okHttpClientBuilder.trustAllSslClient();
        if (HorreumConfiguration.isHttpLogEnabled()) {
            okHttpClientBuilder.log();
        }
        //Horreum specific authentication header
        ApiKeyAuth auth = new ApiKeyAuth("header", "X-Horreum-API-Key");
        //We don't want to expose any secrets in console
        prettyPrinter.addSensitiveHeader("X-Horreum-API-Key");
        ApiClient apiClient = new ApiClient(okHttpClientBuilder.build(), "apikey", auth);
        apiClient.setBasePath(HorreumConfiguration.getUrl());
        apiClient.setVerifyingSsl(true);
        runApi = new RunApi(apiClient);
        this.horreumAccount = horreumAccount;
    }

    /**
     * Requirements: 1) Horreum account specified in credentials.yaml.
     * 2) horreum.username property specified
     * 3) % method parameters
     * <p>
     * Call is initialized in tnb-tests test suite.
     *
     * @param start Test start time
     * @param stop Test end time
     * @param testName Horreum test name
     * @param owner Horreum owner
     * @param access Horreum test access type (e.g. public)
     * @param schema Horreum test schema
     * @param description Test run description
     * @param body Run data
     * @return Uploaded run data ID
     * @throws Exception
     */
    public String postRunData(String start, String stop, String testName, String owner, Access access,
        String schema, String description, String body) throws Exception {
        runApi.getApiClient().setApiKey(horreumAccount.apiKey(HorreumConfiguration.getUserName()));
        Call uploadCall = runApi.addRunFromDataCall(start, stop, testName, owner, access, schema, description, body, null);
        if (HorreumConfiguration.isRequestLogEnabled()) {
            LOG.info("Horreum upload request:");
            prettyPrinter.printRequest(uploadCall.request());
        }
        if (!HorreumConfiguration.isUploadDisabled()) {
            Type responseType = new TypeToken<String>() {
            }.getType();
            ApiResponse<String> horreumResp = runApi.getApiClient().execute(uploadCall, responseType);
            if (HorreumConfiguration.isRequestLogEnabled()) {
                LOG.info("Horreum upload response: ");
                prettyPrinter.printResponse(horreumResp);
            }
            return horreumResp.getData();
        } else {
            return "horreum.upload.disabled=true -> results upload skipped";
        }
    }
}
