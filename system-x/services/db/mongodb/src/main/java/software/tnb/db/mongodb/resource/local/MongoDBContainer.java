package software.tnb.db.mongodb.resource.local;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.util.Map;

public class MongoDBContainer extends GenericContainer<MongoDBContainer> {
    private final int port;

    public MongoDBContainer(String image, int port, Map<String, String> env) {
        super(image);
        this.port = port;
        withExposedPorts(port);
        withEnv(env);
        waitingFor(Wait.forLogMessage(".*Transition to primary complete.*", 1));
    }

    public int getPort() {
        return getMappedPort(port);
    }
}
