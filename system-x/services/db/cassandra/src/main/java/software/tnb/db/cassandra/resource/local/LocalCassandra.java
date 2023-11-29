package software.tnb.db.cassandra.resource.local;

import software.tnb.common.deployment.Deployable;
import software.tnb.db.cassandra.service.Cassandra;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.auto.service.AutoService;

@AutoService(Cassandra.class)
public class LocalCassandra extends Cassandra implements Deployable {
    private static final Logger LOG = LoggerFactory.getLogger(LocalCassandra.class);
    private CassandraContainer container;

    @Override
    public void deploy() {
        LOG.info("Starting cassandra container");
        container = new CassandraContainer(image(), CASSANDRA_PORT, containerEnvironment());
        container.start();
    }

    @Override
    public void undeploy() {
        if (container != null) {
            container.stop();
        }
    }

    @Override
    public String host() {
        return container.getHost();
    }

    @Override
    public int port() {
        return container.getMappedPort(CASSANDRA_PORT);
    }
}
