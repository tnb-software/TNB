package software.tnb.keycloak.resource.local;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.time.Duration;
import java.util.List;
import java.util.Map;

public class KeycloakContainer extends GenericContainer<KeycloakContainer> {

    public KeycloakContainer(String image, Map<String, String> env, String[] startupParameters, int port) {
        super(image);
        this.setPortBindings(List.of("8080:8080"));
        this.withEnv(env);
        this.withNetworkMode("host");
        this.withNetworkAliases("keycloak");
        setCommandParts(startupParameters);
        this.waitingFor(Wait.forLogMessage(".*Keycloak.*started.*", 1)).withStartupTimeout(Duration.ofMinutes(2));
    }
}
