package org.jboss.fuse.tnb.product.git;

import org.jboss.fuse.tnb.common.config.TestConfiguration;
import org.jboss.fuse.tnb.product.git.customizer.UpdateProjectVersionCustomizer;
import org.jboss.fuse.tnb.product.integration.builder.AbstractMavenGitIntegrationBuilder;
import org.jboss.fuse.tnb.product.util.maven.BuildRequest;
import org.jboss.fuse.tnb.product.util.maven.Maven;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class MavenGitRepository extends GitRepository {
    private static final Logger LOG = LoggerFactory.getLogger(MavenGitRepository.class);
    protected Path projectLocation;
    protected Optional<String> finalName;

    public MavenGitRepository(AbstractMavenGitIntegrationBuilder<?> gitIntegrationBuilder, String name) {
        super(gitIntegrationBuilder);

        projectLocation = gitIntegrationBuilder.getSubDirectory().map(project -> getPath().resolve(project)).orElse(getPath());

        finalName = gitIntegrationBuilder.getFinalName();

        if (gitIntegrationBuilder.getProjectVersion() != null) {
            gitIntegrationBuilder.getPreMavenBuildCustomizers().add(
                new UpdateProjectVersionCustomizer(gitIntegrationBuilder.getProjectVersion(), name));
        }

        gitIntegrationBuilder.getPreMavenBuildCustomizers().forEach(customizer -> customizer.accept(projectLocation));

        Map<String, String> mavenBuildProperties = new HashMap<>(gitIntegrationBuilder.getMavenProperties());

        BuildRequest.Builder requestBuilder = new BuildRequest.Builder()
            .withBaseDirectory(projectLocation)
            .withGoals("clean", "package")
            .withProperties(mavenBuildProperties)
            .withLogFile(TestConfiguration.appLocation().resolve(name + "-build.log"));

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
