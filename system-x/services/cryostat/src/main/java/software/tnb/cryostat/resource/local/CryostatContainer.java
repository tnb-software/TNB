package software.tnb.cryostat.resource.local;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.time.Duration;
import java.util.Map;

public class CryostatContainer extends GenericContainer<CryostatContainer> {

    static final int PORT = 8181;

    public CryostatContainer(String image, CryostatDbContainer db, CryostatStorageContainer s3) {
        super(image);
        this.withNetworkMode("host");
        int dbPort = db.getMappedPort(CryostatDbContainer.PORT);
        int s3Port = s3.getMappedPort(CryostatStorageContainer.PORT);
        this.withEnv(Map.ofEntries(
            Map.entry("QUARKUS_HTTP_HOST", "0.0.0.0"),
            Map.entry("QUARKUS_HTTP_PORT", String.valueOf(PORT)),
            Map.entry("QUARKUS_DATASOURCE_JDBC_URL",
                "jdbc:postgresql://localhost:" + dbPort + "/" + CryostatDbContainer.DB_NAME),
            Map.entry("QUARKUS_DATASOURCE_USERNAME", CryostatDbContainer.DB_USER),
            Map.entry("QUARKUS_DATASOURCE_PASSWORD", CryostatDbContainer.DB_PASSWORD),
            Map.entry("QUARKUS_S3_ENDPOINT_OVERRIDE", "http://localhost:" + s3Port),
            Map.entry("QUARKUS_S3_PATH_STYLE_ACCESS", "true"),
            Map.entry("QUARKUS_S3_AWS_REGION", "us-east-1"),
            Map.entry("AWS_ACCESS_KEY_ID", CryostatStorageContainer.ACCESS_KEY),
            Map.entry("AWS_SECRET_ACCESS_KEY", CryostatStorageContainer.SECRET_KEY),
            Map.entry("STORAGE_BUCKETS_ARCHIVES_NAME", "archivedrecordings"),
            Map.entry("CRYOSTAT_SERVICES_REPORTS_STORAGE_CACHE_NAME", "archivedreports"),
            Map.entry("STORAGE_BUCKETS_EVENT_TEMPLATES_NAME", "eventtemplates"),
            Map.entry("STORAGE_BUCKETS_PROBE_TEMPLATES_NAME", "probetemplates"),
            Map.entry("STORAGE_BUCKETS_HEAP_DUMPS_NAME", "heapdumps"),
            Map.entry("STORAGE_BUCKETS_THREAD_DUMPS_NAME", "threaddumps"),
            Map.entry("STORAGE_BUCKETS_METADATA_NAME", "cryostatmeta"),
            Map.entry("CRYOSTAT_DISCOVERY_JDP_ENABLED", "true"),
            Map.entry("CRYOSTAT_AGENT_TLS_REQUIRED", "false"),
            Map.entry("CRYOSTAT_DISABLE_SSL", "true")
        ));
        this.waitingFor(Wait.forLogMessage(".*Listening on.*", 1)
            .withStartupTimeout(Duration.ofMinutes(2)));
    }
}
