package software.tnb.keycloak.resource.local;

import software.tnb.common.deployment.ContainerDeployable;
import software.tnb.keycloak.service.Keycloak;

import com.google.auto.service.AutoService;

@AutoService(Keycloak.class)
public class LocalKeycloak extends Keycloak implements ContainerDeployable<KeycloakContainer> {
    private final KeycloakContainer container = new KeycloakContainer(image(), getEnv(), startupParams(), PORT);

    @Override
    public String host() {
        return container.getHost();
    }

    @Override
    public int port() {
        return PORT;
    }

    @Override
    public KeycloakContainer container() {
        return container;
    }
}
