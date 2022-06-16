package software.tnb.db.cassandra.resource.local;

import software.tnb.common.deployment.Deployable;
import software.tnb.db.cassandra.service.Cassandra;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.config.DefaultDriverOption;
import com.datastax.oss.driver.api.core.config.DriverConfigLoader;
import com.google.auto.service.AutoService;

import java.net.InetSocketAddress;
import java.time.Duration;

@AutoService(Cassandra.class)
public class LocalCassandra extends Cassandra implements Deployable {

    private static final Logger LOG = LoggerFactory.getLogger(LocalCassandra.class);
    private CassandraContainer container;
    private CqlSession session;

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
    public void openResources() {

        // default timeout was failing on jenkins in some cases, increase to 30s
        DriverConfigLoader loader =
            DriverConfigLoader.programmaticBuilder()
                .withDuration(DefaultDriverOption.REQUEST_TIMEOUT, Duration.ofSeconds(30))
                .build();

        session = CqlSession.builder()
            .withConfigLoader(loader)
            .addContactPoint(new InetSocketAddress(host(), port()))
            .withAuthCredentials(account().username(), account().password())
            .withLocalDatacenter(account().datacenter())
            .build();
    }

    @Override
    public void closeResources() {
        if (session != null) {
            session.close();
            session = null;
        }
    }

    @Override
    public String host() {
        return container.getContainerIpAddress();
    }

    @Override
    public int port() {
        return container.getMappedPort(CASSANDRA_PORT);
    }

    @Override
    protected CqlSession session() {
        return session;
    }

}
