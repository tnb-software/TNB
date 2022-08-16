package software.tnb.common.account;

import org.junit.jupiter.api.function.ThrowingSupplier;

import com.bettercloud.vault.Vault;
import com.bettercloud.vault.VaultConfig;
import com.bettercloud.vault.VaultException;
import com.bettercloud.vault.json.JsonObject;
import com.bettercloud.vault.response.AuthResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class VaultCredentialsLoader implements CredentialsLoader {

    private final Vault vault;
    private final ObjectMapper mapper;
    private final String pathPattern;
    private final VaultConfig config;
    private ThrowingSupplier<AuthResponse> authSupplier;

    private VaultCredentialsLoader(String address, String pathPattern) throws VaultException {
        config = new VaultConfig()
            .address(address)
            .engineVersion(2)
            .build();
        vault = new Vault(config);

        this.pathPattern = pathPattern;

        mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public VaultCredentialsLoader(String address, String pathPattern, String ghToken) throws VaultException {
        this(address, pathPattern);
        authSupplier = () -> vault.auth().loginByGithub(ghToken);
        refreshAuthToken();
    }

    public VaultCredentialsLoader(String address, String pathPattern, String roleId, String secretId) throws VaultException {
        this(address, pathPattern);
        authSupplier = () -> vault.auth().loginByAppRole(roleId, secretId);
        refreshAuthToken();
    }

    private void refreshAuthToken() {
        try {
            config.token(authSupplier.get().getAuthClientToken()).build();
        } catch (Throwable e) {
            throw new RuntimeException("Vault reauth failed", e);
        }
    }

    @Override
    public <T extends Account> T get(String credentialsId, Class<T> accountClass) {
        try {
            JsonObject account = get(String.format(pathPattern, credentialsId));
            if (account == null) {
                refreshAuthToken();
                account = get(String.format(pathPattern, credentialsId));
            }
            return mapper.readValue(account.toString(), accountClass);
        } catch (VaultException | JsonProcessingException e) {
            throw new RuntimeException("Couldnt get credentials from vault: " + credentialsId, e);
        }
    }

    public JsonObject get(String path) throws VaultException {
        return vault.logical()
            .read(path)
            .getDataObject();
    }
}
