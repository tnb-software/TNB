package software.tnb.common.account.loader;

import org.junit.jupiter.api.function.ThrowingSupplier;

import com.bettercloud.vault.Vault;
import com.bettercloud.vault.VaultConfig;
import com.bettercloud.vault.VaultException;
import com.bettercloud.vault.json.JsonObject;
import com.bettercloud.vault.response.AuthResponse;
import com.bettercloud.vault.response.LogicalResponse;

public class VaultCredentialsLoader extends CredentialsLoader {
    private final Vault vault;
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
    }

    public VaultCredentialsLoader(String address, String pathPattern, String ghToken) throws VaultException {
        this(address, pathPattern);
        authSupplier = () -> vault.auth().loginByGithub(ghToken);
    }

    public VaultCredentialsLoader(String address, String pathPattern, String roleId, String secretId) throws VaultException {
        this(address, pathPattern);
        authSupplier = () -> vault.auth().loginByAppRole(roleId, secretId);
    }

    private void refreshAuthToken() {
        try {
            config.token(authSupplier.get().getAuthClientToken()).build();
        } catch (Throwable e) {
            throw new RuntimeException("Vault reauth failed", e);
        }
    }

    @Override
    public Object loadCredentials(String credentialsId) {
        refreshAuthToken();
        return get(String.format(pathPattern, credentialsId));
    }

    @Override
    public String toJson(Object credentials) {
        return credentials.toString();
    }

    private JsonObject get(String path) {
        final LogicalResponse response;
        try {
            response = vault.logical().read(path);
        } catch (VaultException e) {
            throw new RuntimeException("Unable to read credentials from vault", e);
        }
        if (response.getRestResponse().getStatus() == 200) {
            return response.getDataObject();
        } else if (response.getRestResponse().getStatus() == 404) {
            return null;
        } else {
            throw new RuntimeException("Unable to get credentials from vault, response code: " + response.getRestResponse().getStatus());
        }
    }
}
