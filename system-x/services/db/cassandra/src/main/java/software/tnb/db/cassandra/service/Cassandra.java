package software.tnb.db.cassandra.service;

import software.tnb.common.deployment.WithDockerImage;
import software.tnb.common.service.Service;
import software.tnb.db.cassandra.account.CassandraAccount;
import software.tnb.db.cassandra.validation.CassandraValidation;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.config.DefaultDriverOption;
import com.datastax.oss.driver.api.core.config.DriverConfigLoader;

import java.net.InetSocketAddress;
import java.time.Duration;
import java.util.Map;

public abstract class Cassandra extends Service<CassandraAccount, CqlSession, CassandraValidation> implements WithDockerImage {
    protected static final int CASSANDRA_PORT = 9042;

    public String defaultImage() {
        // official library image required hacks in openshift, bitnami works out of the box
        return "quay.io/fuse_qe/cassandra:4.1.4";
    }

    public CassandraValidation validation() {
        if (validation == null) {
            validation = new CassandraValidation(client());
        }
        return validation;
    }

    public int port() {
        return CASSANDRA_PORT;
    }

    public abstract String host();

    public String cassandraUrl(String keyspace) {
        return String.format("cql:%s:%s/%s?username=%s&password=%s", host(), port(), keyspace, account().username(), account().password());
    }

    public Map<String, String> containerEnvironment() {
        return Map.of(
            "CASSANDRA_USER", account().username(),
            "CASSANDRA_PASSWORD", account().password(),
            "CASSANDRA_PASSWORD_SEEDER", "yes"
        );
    }

    public void openResources() {
        // default timeout was failing on jenkins in some cases, increase to 30s
        DriverConfigLoader loader =
            DriverConfigLoader.programmaticBuilder()
                .withDuration(DefaultDriverOption.REQUEST_TIMEOUT, Duration.ofSeconds(30))
                .build();

        client = CqlSession.builder()
            .withConfigLoader(loader)
            .addContactPoint(new InetSocketAddress(host(), port()))
            .withAuthCredentials(account().username(), account().password())
            .withLocalDatacenter(account().datacenter())
            .build();
    }

    public void closeResources() {
        validation = null;
        if (client != null) {
            client.close();
            client = null;
        }
    }
}
