package software.tnb.db.cassandra.resource.local;

import software.tnb.common.deployment.ContainerDeployable;
import software.tnb.db.cassandra.service.Cassandra;

import com.google.auto.service.AutoService;

@AutoService(Cassandra.class)
public class LocalCassandra extends Cassandra implements ContainerDeployable<CassandraContainer> {
    private final CassandraContainer container = new CassandraContainer(image(), PORT, containerEnvironment());

    @Override
    public String host() {
        return container.getHost();
    }

    @Override
    public int port() {
        return container.getMappedPort(PORT);
    }

    @Override
    public CassandraContainer container() {
        return container;
    }
}
