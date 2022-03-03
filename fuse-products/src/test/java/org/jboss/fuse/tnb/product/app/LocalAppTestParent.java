package org.jboss.fuse.tnb.product.app;

import static org.junit.jupiter.api.Assertions.fail;

import static org.assertj.core.api.Assertions.assertThat;

import org.jboss.fuse.tnb.common.config.TestConfiguration;
import org.jboss.fuse.tnb.common.product.ProductType;
import org.jboss.fuse.tnb.product.parent.ProductTestParent;
import org.jboss.fuse.tnb.product.util.maven.Maven;
import org.jboss.fuse.tnb.util.maven.TestMaven;
import org.jboss.fuse.tnb.util.maven.TestMavenInvoker;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;

import org.apache.commons.io.FileUtils;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.DependencyManagement;
import org.apache.maven.model.Model;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Test parent for testing local maven applications.
 *
 * Simulates the maven execution by creating expected files
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class LocalAppTestParent extends ProductTestParent {
    protected static final Path APP_PATH = TestConfiguration.appLocation().resolve(name());
    protected static final String PACKAGE_DIRECTORIES = TestConfiguration.appGroupId().replace(".", File.separator);
    protected static final Path POM_PATH = APP_PATH.resolve("pom.xml");
    protected static final TestMavenInvoker TEST_INVOKER = new TestMavenInvoker();

    @BeforeAll
    public void beforeAll() {
        // Will be overriden later, the product is needed in setupMaven
        setProduct(ProductType.CAMEL_QUARKUS);
        TestMaven.setupTestMaven(TEST_INVOKER);
    }

    @BeforeEach
    public void prepare() throws IOException {
        // Create necessary directories + pom.xml file
        TEST_INVOKER.mockExecution(
            () -> {
                try {
                    Files.createDirectories(TestConfiguration.appLocation().resolve(name()).resolve("src/main/resources"));
                    Files.createDirectories(TestConfiguration.appLocation().resolve(name()).resolve("src/main/java").resolve(PACKAGE_DIRECTORIES));
                } catch (IOException e) {
                    fail("Unable to create directories", e);
                }
            },
            () -> {
                Model model = new Model();
                model.setDependencyManagement(new DependencyManagement());
                Maven.writePom(TestConfiguration.appLocation().resolve(name()).resolve("pom.xml").toFile(), model);
            });

        FileUtils.deleteDirectory(TestConfiguration.appLocation().resolve(name()).toFile());
    }

    @AfterEach
    public void clear() {
        TEST_INVOKER.clearRequests();
    }

    public void verifyDependencies(String groupId, String artifactId, String version) {
        final Model pom = Maven.loadPom(APP_PATH.resolve("pom.xml").toFile());
        assertThat(pom.getDependencies()).hasSize(1);

        Dependency d = pom.getDependencies().get(0);
        verifyDependency(d, groupId, artifactId, version);
    }
}
