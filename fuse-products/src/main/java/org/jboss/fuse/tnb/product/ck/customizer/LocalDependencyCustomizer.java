package org.jboss.fuse.tnb.product.ck.customizer;

import org.jboss.fuse.tnb.common.config.QuarkusConfiguration;
import org.jboss.fuse.tnb.common.openshift.OpenshiftClient;
import org.jboss.fuse.tnb.product.util.maven.BuildRequest;
import org.jboss.fuse.tnb.product.util.maven.Maven;

import org.apache.maven.model.Dependency;

import java.io.File;
import java.nio.file.Path;
import java.util.Map;

import io.fabric8.kubernetes.api.model.Pod;

/**
 * Adds a given local folder as a maven dependency of the integration for Camel-K.
 * <p>
 * If the project brings any quarkus features, it needs to be declared as a dependency (it is not enough to place a jar to classpath for example),
 * so this customizer builds the project if the jar doesn't already exist and then copies the .jar and pom.xml inside the local maven repository
 * in the camel-k operator pod.
 */
public class LocalDependencyCustomizer extends CamelKCustomizer {
    private final String mavenCoordinates;
    private final Path folder;
    private final String jar;

    /**
     * Constructor.
     *
     * @param mavenCoordinates maven coordinates in form of groupId:artifactId:version
     * @param resourceFolder folder in src/test/resources directory
     * @param jar name of the jar file inside target folder
     */
    public LocalDependencyCustomizer(String mavenCoordinates, String resourceFolder, String jar) {
        this(mavenCoordinates, new File("src/test/resources/" + resourceFolder).toPath(), jar);
    }

    /**
     * Constructor.
     *
     * @param mavenCoordinates maven coordinates in form of groupId:artifactId:version
     * @param folder path to the folder with maven project
     * @param jar name of the jar file inside target folder
     */
    public LocalDependencyCustomizer(String mavenCoordinates, Path folder, String jar) {
        this.mavenCoordinates = mavenCoordinates;
        this.folder = folder;
        this.jar = jar;
    }

    @Override
    public void customize() {
        Dependency dep = Maven.toDependency(mavenCoordinates);

        if (dep.getVersion() == null) {
            throw new IllegalArgumentException("Maven coordinates for local dependency must always contain a version"
                + " (to be in form groupId:artifactId:version)");
        }

        if (!folder.toFile().exists()) {
            throw new IllegalArgumentException("Folder " + folder.toAbsolutePath() + " does not exist");
        }

        Path jarPath = folder.resolve("target").resolve(jar);
        if (!jarPath.toFile().exists()) {
            buildJar(folder);
        }
        if (!jarPath.toFile().exists()) {
            throw new IllegalArgumentException("Jar file " + jarPath + " doesn't exist after building the project in " + folder);
        }

        final String mavenPath = String.format("/tmp/artifacts/m2/%s/%s/%s/",
            dep.getGroupId().replaceAll("\\.", "/"), dep.getArtifactId(), dep.getVersion());
        final String mavenArtifactName = String.format("%s-%s", dep.getArtifactId(), dep.getVersion());

        Pod operator = OpenshiftClient.get().getLabeledPods("name", "camel-k-operator").get(0);

        // Copy .jar and pom to operator pod
        Map.of(
            jarPath, ".jar",
            folder.resolve("pom.xml"), ".pom"
        ).forEach((file, extension) -> {
            String to = mavenPath + mavenArtifactName + extension;

            LOG.debug("Uploading {} to {} inside camel-k operator pod", file, to);

            if (!OpenshiftClient.get().pods()
                .withName(operator.getMetadata().getName())
                .file(to)
                .upload(file)) {
                throw new RuntimeException("Unable to upload " + file + " to " + to + " inside operator pod");
            }
        });

        // Add this artifact as the dependency of the project
        getIntegrationBuilder().dependencies(dep);
    }

    private void buildJar(Path folder) {
        Maven.setupMaven();

        Maven.invoke(new BuildRequest.Builder()
            .withBaseDirectory(folder)
            .withGoals("clean", "package")
            .withProperties(Map.of(
                "skipTests", "true",
                "quarkus.version", QuarkusConfiguration.quarkusVersion(),
                "camel.quarkus.version", QuarkusConfiguration.camelQuarkusVersion()
            ))
            .build());
    }
}
