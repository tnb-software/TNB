package software.tnb.flink.resource.local;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

public class FlinkTaskManagerContainer extends GenericContainer<FlinkTaskManagerContainer> {

    public FlinkTaskManagerContainer(String image, FlinkJobManagerContainer jobManager) {
        super(image);
        this.withNetwork(jobManager.getNetwork());
        this.withEnv("FLINK_PROPERTIES", "jobmanager.rpc.address: jobmanager");
        this.withCommand("taskmanager");
        this.dependsOn(jobManager);
        this.waitingFor(Wait.forListeningPort());
    }
}
