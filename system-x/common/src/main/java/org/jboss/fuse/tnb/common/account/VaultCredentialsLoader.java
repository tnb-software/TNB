package org.jboss.fuse.tnb.common.account;

import com.bettercloud.vault.Vault;
import com.bettercloud.vault.VaultConfig;
import com.bettercloud.vault.VaultException;
import com.bettercloud.vault.response.AuthResponse;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

public class VaultCredentialsLoader implements CredentialsLoader {

    private final Vault vault;
    private final ObjectMapper mapper;
    private final String pathPattern;
    private final VaultConfig config;

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
        AuthResponse authResponse = vault.auth().loginByGithub(ghToken);
        config.token(authResponse.getAuthClientToken()).build();
    }

    public VaultCredentialsLoader(String address, String pathPattern, String roleId, String secretId) throws VaultException {
        this(address, pathPattern);
        AuthResponse authResponse = vault.auth().loginByAppRole(roleId, secretId);
        config.token(authResponse.getAuthClientToken()).build();
    }

    @Override
    public <T extends Account> T get(String credentialsId, Class<T> accountClass) {
        try {
            return mapper.convertValue(get(String.format(pathPattern, credentialsId)), accountClass);
        } catch (VaultException e) {
            throw new RuntimeException("Couldnt get credentials from vault: " + credentialsId, e);
        }
    }

    public Map<String, String> get(String path) throws VaultException {
        return vault.logical()
            .read(path)
            .getData();
    }
}
