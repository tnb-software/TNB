package software.tnb.cryostat.resource.local;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.wait.strategy.Wait;

import java.util.Map;

public class CryostatStorageContainer extends GenericContainer<CryostatStorageContainer> {

    static final String HOSTNAME = "s3";
    static final int PORT = 8333;
    static final String ACCESS_KEY = "access_key";
    static final String SECRET_KEY = "secret_key";
    private static final String BUCKETS = "archivedrecordings,archivedreports,eventtemplates,probetemplates,heapdumps,threaddumps,cryostatmeta";

    public CryostatStorageContainer(Network network) {
        super("quay.io/cryostat/cryostat-storage:latest");
        this.withNetwork(network);
        this.withNetworkAliases(HOSTNAME);
        this.withEnv(Map.of(
            "CRYOSTAT_BUCKETS", BUCKETS,
            "CRYOSTAT_ACCESS_KEY", ACCESS_KEY,
            "CRYOSTAT_SECRET_KEY", SECRET_KEY,
            "DATA_DIR", "/data",
            "IP_BIND", "0.0.0.0"
        ));
        this.withExposedPorts(PORT);
        this.waitingFor(Wait.forListeningPort());
    }
}
