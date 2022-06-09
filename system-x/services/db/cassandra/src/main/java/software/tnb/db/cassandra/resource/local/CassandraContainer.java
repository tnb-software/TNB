package software.tnb.db.cassandra.resource.local;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class CassandraContainer extends GenericContainer<CassandraContainer> {

    private final int port;

    public CassandraContainer(String image, int port, Map<String, String> env) {
        super(image);
        this.port = port;
        withExposedPorts(port);
        Map<String, String> localEnv = new HashMap<>(env);
        localEnv.put("MAX_HEAP_SIZE", "256M");
        localEnv.put("HEAP_NEWSIZE", "128M");
        withEnv(localEnv);
        withCreateContainerCmdModifier(cmd -> cmd.getHostConfig().withMemory(1024L * 1024 * 1024));
        waitingFor(Wait
            .forLogMessage(".*Startup complete.*", 1)
            .withStartupTimeout(Duration.ofMinutes(5))
        );
    }

    public int getPort() {
        return getMappedPort(port);
    }
}
