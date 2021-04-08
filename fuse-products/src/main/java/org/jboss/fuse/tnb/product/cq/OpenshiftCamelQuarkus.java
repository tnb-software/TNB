package org.jboss.fuse.tnb.product.cq;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.jboss.fuse.tnb.common.config.TestConfiguration;
import org.jboss.fuse.tnb.common.openshift.OpenshiftClient;
import org.jboss.fuse.tnb.common.utils.MapUtils;
import org.jboss.fuse.tnb.common.utils.WaitUtils;
import org.jboss.fuse.tnb.product.OpenshiftProduct;
import org.jboss.fuse.tnb.product.Product;
import org.jboss.fuse.tnb.product.steps.CreateIntegrationStep;
import org.jboss.fuse.tnb.product.steps.Step;
import org.jboss.fuse.tnb.product.util.Maven;
import org.junit.jupiter.api.extension.ExtensionContext;
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

import cz.xtf.core.openshift.helpers.ResourceFunctions;
import io.fabric8.kubernetes.api.model.DeletionPropagation;
import io.fabric8.kubernetes.api.model.HasMetadata;

@AutoService(Product.class)
public class OpenshiftCamelQuarkus extends OpenshiftProduct implements Quarkus {
    private static final Logger LOG = LoggerFactory.getLogger(OpenshiftCamelQuarkus.class);

    // Resources generated from quarkus maven plugin and created in openshift
    private List<HasMetadata> createdResources;

    @Override
    public boolean isReady() {
        return true;
    }

    @Override
    public void setupProduct() {
        Maven.setupMaven();
    }

    @Override
    public void teardownProduct() {
    }

    public void createIntegration(CreateIntegrationStep step) {
        String name = step.getName();
        CodeBlock routeDefinition = step.getRouteDefinition();
        String[] camelComponents = step.getCamelComponents();
        createApp(name, routeDefinition, camelComponents);

        LOG.info("Building {} application project ({})", name, TestConfiguration.isQuarkusNative() ? "native" : "JVM");
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

        Path openshiftResources = TestConfiguration.appLocation().resolve(name).resolve("target/kubernetes/openshift.yml");
        try (InputStream is = IOUtils.toInputStream(Files.readString(openshiftResources), "UTF-8")) {
            LOG.info("Creating openshift resources for integration from file {}", openshiftResources.toAbsolutePath());
            createdResources = OpenshiftClient.get().load(is).createOrReplace();
        } catch (IOException e) {
            throw new RuntimeException("Unable to read openshift.yml resource: ", e);
        }

        waitForImageStream(name);

        // for non-native with >= 1.12 quarkus
        // filePath = createTar(name, integrationTarget.resolve("quarkus-app"));
        Path filePath = createTar(prepareS2iBuildDirectory(name));
        OpenshiftClient.doS2iBuild(name, filePath);
        waitForIntegration(name);
    }

    public void waitForIntegration(String name) {
        LOG.info("Waiting until integration {} is running", name);
        WaitUtils.waitFor(() -> {
            if (ResourceFunctions.areExactlyNPodsRunning(1).apply(OpenshiftClient.get().getLabeledPods("app.kubernetes.io/name", name))) {
                return OpenshiftClient.get().getPodLog(name).contains("started and consuming");
            } else {
                return false;
            }
        }, 24, 5000L, String.format("Waiting until the integration %s is running", name));
    }

    @Override
    public void afterEach(ExtensionContext extensionContext) throws Exception {
        LOG.info("Undeploying integration resources");
        for (HasMetadata createdResource : createdResources) {
            LOG.debug("Undeploying {} {}", createdResource.getKind(), createdResource.getMetadata().getName());
            OpenshiftClient.get().resource(createdResource).withPropagationPolicy(DeletionPropagation.BACKGROUND).delete();
        }
    }

    /**
     * Copies necessary files into a new temporary directory and returns its path.
     * @param name integration name
     * @return path to a new directory
     */
    private Path prepareS2iBuildDirectory(String name) {
        LOG.debug("Preparing s2i directory");
        final Path directory;
        try {
            directory = Files.createTempDirectory(TestConfiguration.appLocation(), "app");
            LOG.debug("s2i directory: {}", directory.toAbsolutePath());
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

    /**
     * Creates a new tar file from given directory.
     * @param dir path to the directory
     * @return path to the tar file
     */
    private Path createTar(Path dir) {
        Path output;
        try {
            output = Files.createTempFile(TestConfiguration.appLocation(), "tar", "");
        } catch (IOException e) {
            throw new RuntimeException("Unable to create temp file: ", e);
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
            throw new RuntimeException("Unable to create tar file: ", e);
        }
        return output;
    }

    /**
     * Parses the needed imagestream:tag from the build config and waits until the imagestream contains that tag.
     * @param bcName buildconfig name
     */
    private void waitForImageStream(String bcName) {
        final String[] imageStreamTag = OpenshiftClient.get().buildConfigs().withName(bcName).get().getSpec().getStrategy()
            .getSourceStrategy().getFrom().getName().split(":");
        OpenshiftClient.waitForImageStream(imageStreamTag[0], imageStreamTag[1]);
    }

    @Override
    public <U extends Step> void runStep(U step) {
        if (step instanceof CreateIntegrationStep) {
            createIntegration((CreateIntegrationStep)step);
        } else {
            super.runStep(step);
        }
    }
}
