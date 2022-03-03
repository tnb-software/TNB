package org.jboss.fuse.tnb.product.generator;

import static org.junit.jupiter.api.Assertions.fail;

import static org.assertj.core.api.Assertions.assertThat;

import org.jboss.fuse.tnb.common.config.TestConfiguration;
import org.jboss.fuse.tnb.common.product.ProductType;
import org.jboss.fuse.tnb.common.utils.IOUtils;
import org.jboss.fuse.tnb.product.integration.Resource;
import org.jboss.fuse.tnb.product.integration.builder.AbstractIntegrationBuilder;
import org.jboss.fuse.tnb.product.integration.builder.IntegrationBuilder;
import org.jboss.fuse.tnb.product.integration.generator.IntegrationGenerator;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Tag("unit")
public class IntegrationGeneratorToFileTest extends AbstractIntegrationGeneratorTest {
    private static final Path TEST_DIR = new File("./target/integrationGeneratorTest").toPath();

    @BeforeEach
    public void createDir() {
        try {
            Files.createDirectories(TEST_DIR.resolve("src/main/resources"));
        } catch (IOException e) {
            fail("Unable to create directory", e);
        }
    }

    @AfterEach
    public void deleteDir() {
        try {
            FileUtils.deleteDirectory(TEST_DIR.toFile());
        } catch (IOException e) {
            fail("Unable to delete directory", e);
        }
    }

    @Override
    public String process(AbstractIntegrationBuilder<?> ib) {
        IntegrationGenerator.toFile(ib, TEST_DIR);
        return null;
    }

    @Override
    @Test
    public void shouldProcessRouteBuilderTest() {
        setProduct(ProductType.CAMEL_SPRINGBOOT);

        IntegrationBuilder ib = dummyIb();
        process(ib);

        final Path expectedPath = TEST_DIR
            .resolve("src/main/java/")
            .resolve(TestConfiguration.appGroupId().replaceAll("\\.", "/"))
            .resolve(ib.getFileName());

        assertThat(expectedPath).exists();
        assertThat(expectedPath).content().isEqualTo(ib.getRouteBuilder().get().toString());
    }

    @Test
    public void shouldCreatePropertiesFileTest() {
        setProduct(ProductType.CAMEL_QUARKUS);
        final String key = "Hello";
        final String value = "world";

        IntegrationBuilder ib = dummyIb().addToProperties(key, value);
        process(ib);

        final Path expectedPath = TEST_DIR.resolve("src/main/resources/application.properties");

        assertThat(expectedPath).exists();
        assertThat(expectedPath).content().isEqualTo(key + "=" + value);
    }

    @Override
    @Test
    public void shouldProcessAdditionalClassesTest() {
        setProduct(ProductType.CAMEL_SPRINGBOOT);
        IntegrationBuilder ib = builderWithAdditionalClass();
        final String classContent = ib.getAdditionalClasses().get(0).toString();

        process(ib);

        final Path expectedPath = TEST_DIR
            .resolve("src/main/java/")
            .resolve(StringUtils.substringBetween(classContent, "package ", ";").replaceAll("\\.", "/"))
            .resolve("AddedClass.java");

        assertThat(expectedPath).exists();
        assertThat(expectedPath).content().isEqualTo(classContent);

    }

    @Test
    public void shouldProcessResourcesTest() {
        setProduct(ProductType.CAMEL_SPRINGBOOT);
        final String resourceName = "my-file.txt";
        final String resourceContent = "File content";
        final Path expectedPath = TEST_DIR.resolve("src/main/resources").resolve(resourceName);

        process(dummyIb().addResource(new Resource(resourceName, resourceContent)));

        assertThat(expectedPath).exists();
        assertThat(expectedPath).content().isEqualTo(resourceContent);
    }

    @Test
    public void shouldAddResourcesToQuarkusPropertyTest() {
        setProduct(ProductType.CAMEL_QUARKUS);
        final String resourceName = "my-file.txt";

        IntegrationBuilder ib = dummyIb().addResource(new Resource(resourceName, ""));

        process(ib);

        assertThat(ib.getProperties()).hasSize(1);
        assertThat(ib.getProperties()).containsKey("quarkus.native.resources.includes");
        assertThat(ib.getProperties().get("quarkus.native.resources.includes")).isEqualTo(resourceName);
    }

    @Test
    public void shouldAddImportForAdditionalClassTest() {
        setProduct(ProductType.CAMEL_SPRINGBOOT);
        IntegrationBuilder ib = builderWithAdditionalClass();

        IntegrationGenerator.toFile(ib, TEST_DIR);

        final Path routeBuilderPath = TEST_DIR
            .resolve("src/main/java/")
            .resolve(TestConfiguration.appGroupId().replaceAll("\\.", "/"))
            .resolve(ib.getFileName());
        assertThat(IOUtils.readFile(routeBuilderPath)).contains("import org.jboss.fuse.tnb.product.generator.AddedClass;");
    }
}
