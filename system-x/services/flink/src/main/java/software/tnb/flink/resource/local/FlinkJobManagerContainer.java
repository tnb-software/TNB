package software.tnb.flink.resource.local;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.wait.strategy.Wait;

public class FlinkJobManagerContainer extends GenericContainer<FlinkJobManagerContainer> {

    private final Network network = Network.newNetwork();

    public FlinkJobManagerContainer(String image, int port) {
        super(image);
        this.withNetwork(network);
        this.withExposedPorts(port);
        this.withNetworkAliases("jobmanager");
        this.withEnv("FLINK_PROPERTIES", "jobmanager.rpc.address: jobmanager");
        this.withCommand("jobmanager");
        this.withExposedPorts(port);
        this.waitingFor(Wait.forHttp("/").forPort(8081).forStatusCode(200));
    }
}
