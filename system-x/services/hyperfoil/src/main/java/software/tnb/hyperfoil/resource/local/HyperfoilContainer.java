package software.tnb.hyperfoil.resource.local;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.util.Map;

public class HyperfoilContainer extends GenericContainer<HyperfoilContainer> {

    public HyperfoilContainer(String image, Map<String, String> env) {
        super(image);
        this.withEnv(env);
        this.withNetworkMode("host");
        this.waitingFor(Wait.forHttp("/benchmark"));
        this.withCommand("standalone");
    }
}
