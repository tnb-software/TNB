package org.jboss.fuse.tnb.product.cq;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.jboss.fuse.tnb.common.config.TestConfiguration;
import org.jboss.fuse.tnb.common.deployment.OpenshiftDeployable;
import org.jboss.fuse.tnb.common.openshift.OpenshiftClient;
import org.jboss.fuse.tnb.common.utils.MapUtils;
import org.jboss.fuse.tnb.common.utils.WaitUtils;
import org.jboss.fuse.tnb.product.Product;
import org.jboss.fuse.tnb.product.util.Maven;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.CodeBlock;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import cz.xtf.core.openshift.helpers.ResourceFunctions;
import io.fabric8.kubernetes.api.model.DeletionPropagation;
import io.fabric8.kubernetes.api.model.HasMetadata;

@AutoService(Product.class)
public class OpenshiftCamelQuarkus extends Quarkus implements OpenshiftDeployable {
    private static final Logger log = LoggerFactory.getLogger("OpenshiftCamelQuarkus");
    private List<HasMetadata> createdResources;

    @Override
    public boolean isReady() {
        return true;
    }

    @Override
    public boolean isDeployed() {
        return true;
    }

    @Override
    public void create() {
        Maven.setupMaven();
    }

    @Override
    public void undeploy() {
    }

    @Override
    public void deployIntegration(String name, CodeBlock routeDefinition, String... camelComponents) {
        createApp(name, routeDefinition, camelComponents);

        log.info("Building {} application project ({})", name, TestConfiguration.isQuarkusNative() ? "native" : "JVM");
        Maven.invoke(
            TestConfiguration.appLocation().resolve(name),
            Arrays.asList("clean", "package"),
            TestConfiguration.isQuarkusNative() ? Collections.singletonList("native") : null,
            MapUtils.toProperties(Map.of(
                "skipTests", "true",
                // todo: tmp due to issue with mongodb in 3.8.0 camel
                "quarkus.platform.version", "1.11.6.Final",
                "quarkus.native.container-build", "true"
            ))
        );

        Path integrationTarget = TestConfiguration.appLocation().resolve(name).resolve("target");

        try (InputStream is = IOUtils.toInputStream(Files.readString(integrationTarget.resolve("kubernetes/openshift.yml")), "UTF-8")) {
            log.info("Creating openshift resources for integration");
            createdResources = OpenshiftClient.get().load(is).createOrReplace();
        } catch (IOException e) {
            throw new RuntimeException("Unable to read openshift.yml resource: ", e);
        }

        waitForImageStream(name);

        // for non-native with >= 1.12 quarkus
        // filePath = createTar(name, integrationTarget.resolve("quarkus-app"));
        Path filePath = createTar(name, prepareS2iBuildDirectory(name));
        doS2iBuild(name, filePath);
        waitForIntegration(name);
    }

    @Override
    public void waitForIntegration(String name) {
        log.info("Waiting until integration {} is running", name);
        try {
            WaitUtils.waitFor(() -> {
                if (ResourceFunctions.areExactlyNPodsRunning(1).apply(OpenshiftClient.get().getLabeledPods("app.kubernetes.io/name", name))) {
                    return OpenshiftClient.get().getPodLog(name).contains("started and consuming");
                } else {
                    return false;
                }
            }, 24, 5000L);
        } catch (TimeoutException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void undeployIntegration() {
        log.info("Undeploying integration resources");
        for (HasMetadata createdResource : createdResources) {
            log.debug("Undeploying {} {}", createdResource.getKind(), createdResource.getMetadata().getName());
            OpenshiftClient.get().resource(createdResource).withPropagationPolicy(DeletionPropagation.BACKGROUND).delete();
        }
    }

    private Path prepareS2iBuildDirectory(String name) {
        log.debug("Preparing s2i directory");
        final Path directory;
        try {
            directory = Files.createTempDirectory(TestConfiguration.appLocation(), "app");
            log.debug("s2i directory: {}", directory.toAbsolutePath());
            if (TestConfiguration.isQuarkusNative()) {
                FileUtils.copyFile(
                    TestConfiguration.appLocation().resolve(name).resolve("target").resolve(name + "-1.0.0-SNAPSHOT-runner").toFile(),
                    directory.resolve(name + "-1.0.0-SNAPSHOT-runner").toFile()
                );
            } else {
                FileUtils
                    .copyDirectory(TestConfiguration.appLocation().resolve(name).resolve("target/lib").toFile(), directory.resolve("lib").toFile());
                FileUtils.copyFile(
                    TestConfiguration.appLocation().resolve(name).resolve("target").resolve(name + "-1.0.0-SNAPSHOT-runner.jar").toFile(),
                    directory.resolve(name + "-1.0.0-SNAPSHOT-runner.jar").toFile()
                );
            }
        } catch (IOException e) {
            throw new RuntimeException("Unable to prepare s2i build directory: ", e);
        }
        return directory;
    }

    private Path createTar(String name, Path dir) {
        Path output;
        try {
            output = Files.createTempFile(TestConfiguration.appLocation(), name, "");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try (TarArchiveOutputStream archive = new TarArchiveOutputStream(Files.newOutputStream(output))) {
            archive.setLongFileMode(TarArchiveOutputStream.LONGFILE_POSIX);

            Files.walkFileTree(dir, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attributes) {
                    if (attributes.isSymbolicLink()) {
                        return FileVisitResult.CONTINUE;
                    }
                    Path targetFile = dir.relativize(file);
                    try {
                        TarArchiveEntry tarEntry = new TarArchiveEntry(file.toFile(), targetFile.toString());
                        archive.putArchiveEntry(tarEntry);
                        Files.copy(file, archive);
                        archive.closeArchiveEntry();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) {
                    return FileVisitResult.CONTINUE;
                }
            });

            archive.finish();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return output;
    }

    private void doS2iBuild(String name, Path filePath) {
        log.info("Instantiating a new build for buildconfig {} from file {}", name, filePath.toAbsolutePath());
        OpenshiftClient.get().buildConfigs().withName(name).instantiateBinary().fromFile(filePath.toFile());
        log.info("Waiting until the build completes");
        try {
            WaitUtils.waitFor(() -> "complete".equalsIgnoreCase(OpenshiftClient.get()
                .getBuild(name + "-" + OpenshiftClient.get().getBuildConfig(name).getStatus().getLastVersion()).getStatus().getPhase()), 24, 5000L);
        } catch (TimeoutException e) {
            throw new RuntimeException(e);
        }
    }

    private void waitForImageStream(String bcName) {
        final String[] imageStreamTag = OpenshiftClient.get().buildConfigs().withName(bcName).get().getSpec().getStrategy()
            .getSourceStrategy().getFrom().getName().split(":");
        OpenshiftClient.waitForImageStream(imageStreamTag[0], imageStreamTag[1]);
    }
}
