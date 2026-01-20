package software.tnb.hyperfoil.resource.local;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

public class HyperfoilContainer extends GenericContainer<HyperfoilContainer> {

    public HyperfoilContainer(String image) {
        super(image);
        this.withNetworkMode("host");
        this.waitingFor(Wait.forHttp("/benchmark"));
        this.withCommand("standalone");
    }
}
