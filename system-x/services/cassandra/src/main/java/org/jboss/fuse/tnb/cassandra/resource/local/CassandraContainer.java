package org.jboss.fuse.tnb.cassandra.resource.local;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.time.Duration;
import java.util.Map;

public class CassandraContainer extends GenericContainer<CassandraContainer> {

    private final int port;

    public CassandraContainer(String image, int port, Map<String, String> env) {
        super(image);
        this.port = port;
        withExposedPorts(port);
        withEnv(env);
        waitingFor(Wait
            .forLogMessage(".*Startup complete.*", 1)
            .withStartupTimeout(Duration.ofMinutes(5))
        );
    }

    public int getPort() {
        return getMappedPort(port);
    }
}
