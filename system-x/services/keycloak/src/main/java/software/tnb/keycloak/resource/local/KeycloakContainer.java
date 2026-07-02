package software.tnb.keycloak.resource.local;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.time.Duration;
import java.util.Map;

public class KeycloakContainer extends GenericContainer<KeycloakContainer> {

    public KeycloakContainer(String image, Map<String, String> env, String[] startupParameters, int port) {
        super(image);
        this.withEnv(env);
        this.addExposedPort(port);
        this.withNetworkAliases("keycloak");
        setCommandParts(startupParameters);
        this.waitingFor(Wait.forLogMessage(".*Keycloak.*started.*", 1)).withStartupTimeout(Duration.ofMinutes(5));
    }
}
