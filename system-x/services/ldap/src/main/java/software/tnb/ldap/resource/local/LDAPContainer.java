package software.tnb.ldap.resource.local;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.util.Map;

public class LDAPContainer extends GenericContainer<LDAPContainer> {
    private final int port;

    public LDAPContainer(String image, int port, Map<String, String> env) {
        super(image);
        this.port = port;
        withExposedPorts(port);
        this.withEnv(env);
        this.waitingFor(Wait.forLogMessage(".*slapd starting.*", 2));
    }

    public int getPort() {
        return getMappedPort(port);
    }
}
