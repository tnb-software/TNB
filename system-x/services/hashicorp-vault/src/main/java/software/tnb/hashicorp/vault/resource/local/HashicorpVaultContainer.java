package software.tnb.hashicorp.vault.resource.local;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.util.Map;

public class HashicorpVaultContainer extends GenericContainer<HashicorpVaultContainer> {

    public HashicorpVaultContainer(String image, Map<String, String> env, int port) {
        super(image);
        this.withEnv(env);
        this.waitingFor(Wait.forListeningPort());
        this.waitingFor(Wait.forLogMessage(".*Development.*mode.*should.*", 1));
        this.withExposedPorts(port);
    }
}
