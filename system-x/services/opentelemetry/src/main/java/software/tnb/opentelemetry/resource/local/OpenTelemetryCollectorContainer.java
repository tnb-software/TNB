package software.tnb.opentelemetry.resource.local;

import software.tnb.common.utils.IOUtils;

import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.nio.file.Path;
import java.time.Duration;

public class OpenTelemetryCollectorContainer extends GenericContainer<OpenTelemetryCollectorContainer> {

    private static final Path CONF_PATH;

    static {
        CONF_PATH = IOUtils.createTempFolderStructure("otel-conf");
    }

    public OpenTelemetryCollectorContainer(String image, String collectorConfiguration) {
        super(image);
        withFileSystemBind(CONF_PATH.toAbsolutePath().toString(), "/conf", BindMode.READ_ONLY);
        setCommandParts(new String[]{"--config=/conf/collector.yaml"});
        withExposedPorts(4317, 4318);
        waitingFor(Wait.forLogMessage(".*Everything is ready. Begin running and processing data.*", 1)
            .withStartupTimeout(Duration.ofSeconds(20)));

        IOUtils.writeFile(Path.of(CONF_PATH.toAbsolutePath().toString(), "collector.yaml"), collectorConfiguration);
    }
}
