package software.tnb.cyberark.cyberarkvault.validation;

import static org.junit.jupiter.api.Assertions.assertTrue;

import software.tnb.common.utils.HTTPUtils;
import software.tnb.common.validation.Validation;
import software.tnb.cyberark.cyberarkvault.account.CyberarkVaultAccount;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class CyberarkVaultValidation implements Validation {
    private static final Logger LOG = LoggerFactory.getLogger(CyberarkVaultValidation.class);
    private static final MediaType TEXT = MediaType.parse("text/plain");

    private final HTTPUtils client;
    private final String baseUrl;
    private final CyberarkVaultAccount account;

    public CyberarkVaultValidation(HTTPUtils client, String baseUrl, CyberarkVaultAccount account) {
        this.client = client;
        this.baseUrl = baseUrl;
        this.account = account;
    }

    private String createToken() {
        HTTPUtils.Response response = client.post(
            String.format("%s/authn/%s/%s/authenticate", baseUrl, account.accountName(), account.userName()),
            RequestBody.create(account.apiKey(), TEXT),
            Map.of("Accept-Encoding", "base64"));
        assertTrue(response.isSuccessful(), "Unable to create CyberarkVault token, response code: " + response.getResponseCode());
        return response.getBody();
    }

    private Map<String, String> authorizationHeader() {
        return Map.of("Authorization", String.format("Token token=\"%s\"", createToken()));
    }

    /**
     * A variable must be declared by a policy before a value can be stored in it.
     *
     * @param branch the policy branch to load into
     * @param policy the policy body (YAML)
     */
    private void loadPolicy(String branch, String policy) {
        HTTPUtils.Response response = client.post(
            String.format("%s/policies/%s/policy/%s", baseUrl, account.accountName(), branch),
            RequestBody.create(policy, TEXT),
            authorizationHeader());
        assertTrue(response.isSuccessful(), "Unable to load policy, response code: " + response.getResponseCode());
    }

    public void createVariable(String variable) {
        loadPolicy("root", String.format("- !variable %s%n", variable));
    }

    public void storeSecret(String variable, String value) {
        LOG.debug("Storing CyberarkVault secret {}", variable);
        createVariable(variable);
        HTTPUtils.Response response = client.post(
            String.format("%s/secrets/%s/variable/%s", baseUrl, account.accountName(), variable),
            RequestBody.create(value, TEXT),
            authorizationHeader());
        assertTrue(response.isSuccessful(), "Unable to store secret " + variable + ", response code: " + response.getResponseCode());
    }

    public String retrieveSecret(String variable) {
        LOG.debug("Retrieving CyberarkVault secret {}", variable);
        HTTPUtils.Response response = client.get(
            String.format("%s/secrets/%s/variable/%s", baseUrl, account.accountName(), variable),
            authorizationHeader());
        assertTrue(response.isSuccessful(), "Unable to retrieve secret " + variable + ", response code: " + response.getResponseCode());
        return response.getBody();
    }
}
