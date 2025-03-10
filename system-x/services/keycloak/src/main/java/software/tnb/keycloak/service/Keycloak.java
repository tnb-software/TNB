package software.tnb.keycloak.service;

import software.tnb.common.client.NoClient;
import software.tnb.common.deployment.WithDockerImage;
import software.tnb.common.service.Service;
import software.tnb.keycloak.account.KeycloakAccount;
import software.tnb.keycloak.validation.KeycloakValidation;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;

public abstract class Keycloak extends Service<KeycloakAccount, NoClient, KeycloakValidation> implements WithDockerImage {

    protected static final int PORT = 8080;

    public KeycloakValidation validation() {
        if (validation == null) {
            validation = new KeycloakValidation(host(), port());
        }
        return validation;
    }

    protected Map<String, String> getEnv() {
        return Map.of("KC_BOOTSTRAP_ADMIN_USERNAME", account().adminUser(),
            "KC_BOOTSTRAP_ADMIN_PASSWORD", account().adminPassword());
    }

    protected String[] startupParams() {
        return new String[] {"start-dev"};
    }

    public String defaultImage() {
        return "quay.io/keycloak/keycloak:26.1";
    }

    public boolean isLocalHostname() {
        String clientHostname = "";
        try {
            clientHostname = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        return "127.0.0.1".equals(clientHostname);
    }

    public abstract String host();

    public abstract int port();

    public void openResources() {
        // no-op
    }

    public void closeResources() {
        // no-op
    }
}
