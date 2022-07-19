package software.tnb.lracoordinator.resource.local;

import software.tnb.lracoordinator.service.LRACoordinator;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.util.Map;

public class LRACoordinatorContainer extends GenericContainer<LRACoordinatorContainer> {
       public LRACoordinatorContainer(String image, Map<String, String> env) {
        super(image);
        withNetworkMode("host");
        withEnv(env);
        waitingFor(Wait.forLogMessage(".*Profile prod activated.*", 1));
    }

    public int getPort() {
        return LRACoordinator.DEFAULT_PORT;
    }
}
