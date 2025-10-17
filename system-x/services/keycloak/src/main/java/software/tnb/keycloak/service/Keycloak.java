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

    /** Rebuilded from:
     *  https://catalog.redhat.com/software/containers/rhbk/keycloak-rhel9/64f0add883a29ec473d40906?image=674f09b15573280ccffd2bb0
     *  to correct s390x manifest
     */
    public String defaultImage() {
        return "quay.io/fuse_qe/keycloak:26.0-6";
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
