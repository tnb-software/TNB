package software.tnb.product.customizer.byteman;

import software.tnb.common.config.TestConfiguration;
import software.tnb.product.config.BytemanConfiguration;
import software.tnb.product.customizer.Customizer;
import software.tnb.product.integration.Resource;
import software.tnb.product.util.maven.Maven;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.Collectors;

public class BytemanCustomizer extends Customizer {
    private static final Logger LOG = LoggerFactory.getLogger(BytemanCustomizer.class);
    private static final String BYTEMAN_FILE_NAME = "byteman.jar";

    @Override
    public void customize() {
        if (!getIntegrationBuilder().getBytemanRules().isEmpty()) {
            String bytemanPath = Maven.downloadArtifact(
                BytemanConfiguration.dir(),
                BytemanConfiguration.groupId(),
                BytemanConfiguration.artifactId(),
                BytemanConfiguration.version(),
                "jar"
            );
            getIntegrationBuilder().getBytemanRules().forEach(r -> getIntegrationBuilder().addResource(r));
            getIntegrationBuilder().addResource(new Resource(BYTEMAN_FILE_NAME, bytemanPath, true));

            String directory = TestConfiguration.appLocation().resolve(getIntegrationBuilder().getIntegrationName())
                .resolve("src/main/resources").toAbsolutePath().toString();

            String bytemanAgent = directory + "/" + BYTEMAN_FILE_NAME
                + "="
                + getIntegrationBuilder().getBytemanRules().stream().map(r -> "script:" + directory + "/" + r.getName())
                .collect(Collectors.joining(","));
            getIntegrationBuilder().addJavaAgent(bytemanAgent);
        }
    }
}
