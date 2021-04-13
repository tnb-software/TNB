package org.jboss.fuse.tnb.mongodb.resource.local;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.util.Map;

public class MongoContainer extends GenericContainer<MongoContainer> {
    private final int port;

    public MongoContainer(String image, int port, Map<String, String> env) {
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
