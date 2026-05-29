package software.tnb.cryostat.resource.local;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.wait.strategy.Wait;

import java.util.Map;

public class CryostatDbContainer extends GenericContainer<CryostatDbContainer> {

    static final String HOSTNAME = "db";
    static final int PORT = 5432;
    static final String DB_USER = "cryostat";
    static final String DB_PASSWORD = "cryostat";
    static final String DB_NAME = "cryostat";

    public CryostatDbContainer(Network network) {
        super("quay.io/cryostat/cryostat-db:latest");
        this.withNetwork(network);
        this.withNetworkAliases(HOSTNAME);
        this.withEnv(Map.of(
            "POSTGRESQL_USER", DB_USER,
            "POSTGRESQL_PASSWORD", DB_PASSWORD,
            "POSTGRESQL_DATABASE", DB_NAME,
            "PG_ENCRYPT_KEY", "cryostat"
        ));
        this.withExposedPorts(PORT);
        this.waitingFor(Wait.forListeningPort());
    }
}
