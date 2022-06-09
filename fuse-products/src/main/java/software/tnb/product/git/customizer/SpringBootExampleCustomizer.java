package software.tnb.product.git.customizer;

import software.tnb.product.util.maven.Maven;

import org.apache.maven.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class SpringBootExampleCustomizer implements Consumer<Path> {
    private static final Logger LOG = LoggerFactory.getLogger(SpringBootExampleCustomizer.class);
    private String projectVersion;
    private String name;
    private String camelVersion;

    public SpringBootExampleCustomizer(String projectVersion, String name, String camelVersion) {
        this.projectVersion = projectVersion;
        this.name = name;
        this.camelVersion = camelVersion;
    }

    @Override
    public void accept(Path projectPath) {
        LOG.info("Updating {} version to {}", name, projectVersion);

        try (Stream<Path> walkStream = Files.walk(projectPath.getParent())) {
            walkStream.filter(p -> p.toFile().isFile()).forEach(f -> {
                if (f.toString().endsWith("pom.xml")) {
                    Model model = Maven.loadPom(f.toFile());

                    model.getParent().setVersion(projectVersion);

                    model.getProperties().replace("camel-version", camelVersion);

                    Maven.writePom(f.toFile(), model);
                }
            });
        } catch (IOException e) {
            throw new RuntimeException("Error during parent version update", e);
        }
    }
}
