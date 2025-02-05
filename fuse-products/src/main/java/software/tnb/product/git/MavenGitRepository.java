package software.tnb.product.git;

import software.tnb.product.application.Phase;
import software.tnb.product.git.customizer.UpdateProjectVersionCustomizer;
import software.tnb.product.integration.builder.AbstractMavenGitIntegrationBuilder;
import software.tnb.product.log.stream.LogStream;
import software.tnb.product.util.maven.BuildRequest;
import software.tnb.product.util.maven.Maven;

import org.apache.maven.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class MavenGitRepository extends GitRepository {
    private static final Logger LOG = LoggerFactory.getLogger(MavenGitRepository.class);
    protected Path projectLocation;
    protected Optional<String> finalName;

    public MavenGitRepository(AbstractMavenGitIntegrationBuilder<?> gitIntegrationBuilder, String name, Path logFile, boolean buildProject) {
        super(gitIntegrationBuilder);

        projectLocation = gitIntegrationBuilder.getSubDirectory().map(project -> getPath().resolve(project)).orElse(getPath());

        finalName = gitIntegrationBuilder.getFinalName();

        if (gitIntegrationBuilder.getProjectVersion() != null) {
            gitIntegrationBuilder.getPreMavenBuildCustomizers().add(
                new UpdateProjectVersionCustomizer(gitIntegrationBuilder.getProjectVersion(), name));
        }

        gitIntegrationBuilder.getPreMavenBuildCustomizers().forEach(customizer -> customizer.accept(projectLocation));

        Map<String, String> mavenBuildProperties = new HashMap<>(gitIntegrationBuilder.getMavenProperties());
        finalName.ifPresent(fName -> {
            final File pom = projectLocation.resolve("pom.xml").toFile();
            final Model model = Maven.loadPom(pom);
            if (model.getBuild() != null) {
                model.getBuild().setFinalName(finalName.get());
                Maven.writePom(pom, model);
            }
        });

        if (buildProject) {
            buildProject(gitIntegrationBuilder, name, logFile, mavenBuildProperties);
        }
    }

    private void buildProject(AbstractMavenGitIntegrationBuilder<?> gitIntegrationBuilder, String name, Path logFile,
        Map<String, String> mavenBuildProperties) {
        BuildRequest.Builder requestBuilder = new BuildRequest.Builder()
            .withBaseDirectory(projectLocation)
            .withArgs(gitIntegrationBuilder.cleanBeforeBuild() ? "clean" : "", "package")
            .withProperties(mavenBuildProperties)
            .withLogFile(logFile)
            .withLogMarker(LogStream.marker(name, Phase.BUILD));

        LOG.info("Building {} application project", name);
        Maven.invoke(requestBuilder.build());
    }

    public Path getProjectLocation() {
        return projectLocation;
    }

    public Optional<String> getFinalName() {
        return finalName;
    }
}
