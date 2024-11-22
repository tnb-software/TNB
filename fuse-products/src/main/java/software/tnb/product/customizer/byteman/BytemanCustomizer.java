package software.tnb.product.customizer.byteman;

import software.tnb.common.config.TestConfiguration;
import software.tnb.product.application.Phase;
import software.tnb.product.config.BytemanConfiguration;
import software.tnb.product.customizer.Customizer;
import software.tnb.product.integration.Resource;
import software.tnb.product.log.stream.LogStream;
import software.tnb.product.util.maven.BuildRequest;
import software.tnb.product.util.maven.Maven;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.stream.Collectors;

public class BytemanCustomizer extends Customizer {
    private static final Logger LOG = LoggerFactory.getLogger(BytemanCustomizer.class);
    private static final String BYTEMAN_FILE_NAME = "byteman.jar";

    @Override
    public void customize() {
        if (!getIntegrationBuilder().getBytemanRules().isEmpty()) {
            getIntegrationBuilder().getBytemanRules().forEach(r -> getIntegrationBuilder().addResource(r));
            getIntegrationBuilder().addResource(new Resource(BYTEMAN_FILE_NAME, downloadBytemanJar(), true));

            String directory = TestConfiguration.appLocation().resolve(getIntegrationBuilder().getIntegrationName())
                .resolve("src/main/resources").toAbsolutePath().toString();

            String bytemanAgent = directory + "/" + BYTEMAN_FILE_NAME
                + "="
                + getIntegrationBuilder().getBytemanRules().stream().map(r -> "script:" + directory + "/" + r.getName())
                .collect(Collectors.joining(","));
            getIntegrationBuilder().addJavaAgent(bytemanAgent);
        }
    }

    private String downloadBytemanJar() {
        Path bytemanDownloadDir = Paths.get("target", "byteman-download");
        Path downloadedJar = bytemanDownloadDir.resolve("byteman-" + BytemanConfiguration.version() + ".jar");

        try {
            if (Files.exists(bytemanDownloadDir)) {
                if (Files.exists(downloadedJar) && Files.size(downloadedJar) > 0) {
                    LOG.trace("Reusing previously downloaded byteman jar");
                    return downloadedJar.toAbsolutePath().toString();
                }
            } else {
                Files.createDirectory(bytemanDownloadDir);
            }
        } catch (IOException e) {
            throw new RuntimeException("Unable to initialize byteman download", e);
        }

        Maven.invoke(new BuildRequest.Builder()
            .withBaseDirectory(bytemanDownloadDir)
            .withGoals("dependency:copy")
            .withProperties(Map.of(
                "artifact", String.format("%s:%s:%s", BytemanConfiguration.mavenCoordinates(), BytemanConfiguration.version(), "jar"),
                "outputDirectory", "."
            ))
            .withLogFile(TestConfiguration.appLocation().resolve("byteman-download.log"))
            .withLogMarker(LogStream.marker(getIntegrationBuilder().getIntegrationName(), Phase.DOWNLOAD))
            .build()
        );

        return bytemanDownloadDir.resolve("byteman-" + BytemanConfiguration.version() + ".jar").toAbsolutePath().toString();
    }
}
