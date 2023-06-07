package software.tnb.ldap.resource.local;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

public class LDAPContainer extends GenericContainer<LDAPContainer> {

    public LDAPContainer(String image, int port) {
        super(image);
        this.withExposedPorts(port);
        this.waitingFor(Wait.forListeningPort());
    }
}
