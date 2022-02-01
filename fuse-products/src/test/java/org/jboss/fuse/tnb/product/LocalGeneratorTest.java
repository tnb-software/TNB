package org.jboss.fuse.tnb.product;

import org.jboss.fuse.tnb.common.product.ProductType;
import org.jboss.fuse.tnb.product.customizer.Customizers;
import org.jboss.fuse.tnb.product.integration.IntegrationBuilder;
import org.jboss.fuse.tnb.product.util.maven.Maven;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public class LocalGeneratorTest {
    private static final Logger log = LoggerFactory.getLogger(LoggerFactory.class);

    @ParameterizedTest
    @CsvSource({
        "camelspringboot,@Component,camel-direct-starter",
        "camelquarkus,@ApplicationScoped,camel-quarkus-direct"
    })
    public void test(String productName, String routeBuilderAnnotation, String directArtifactId) throws Exception {
        log.info("testing product {} with annotation {}", productName, routeBuilderAnnotation);

        System.setProperty("test.product", productName);
        Maven.setupMaven();
        Product product = ProductFactory.create();

        String emptyClass = "TestEmptyClass";

        String appName = "test-app-" + productName;

        product.createIntegration(new IntegrationBuilder(appName)
            .fromRouteBuilder(new DirectToLogRoute())
            .addCustomizer(
                new AddClassCustomizer(emptyClass),
                Customizers.QUARKUS.customize(i -> i.addToProperties("test.prop.not.exists", "not exists"))
            )
            .addToProperties("test.prop", "test prop")
            .dependencies("direct")
        );

        Path app = Paths.get("target", appName);

        Assertions.assertTrue(Files.walk(app).anyMatch(file -> (emptyClass + ".java").equals(file.getFileName().toString())),
            "Empty class is not generated");

        Optional<Path> routeBuilderFile = Files.walk(app).filter(file -> "MyRouteBuilder.java".equals(file.getFileName().toString()))
            .findAny();

        Assertions.assertTrue(routeBuilderFile.isPresent(), "Route Builder class is not generated");

        if (!routeBuilderAnnotation.isEmpty()) {
            Assertions.assertEquals(1, Files.lines(routeBuilderFile.get())
                    .filter(line -> line.contains(routeBuilderAnnotation)).count(),
                "Route Builder does not contains " + routeBuilderAnnotation + " annotation");
        }

        Optional<Path> propertiesFile = Files.walk(app).filter(file -> "application.properties".equals(file.getFileName().toString()))
            .findAny();

        Assertions.assertTrue(propertiesFile.isPresent(), "application.properties file is not present");

        Assertions.assertEquals(1, Files.lines(propertiesFile.get())
            .filter(line -> line.contains("test.prop=test prop")).count(), "application.properties does not contains test.prop property");

        if (!ProductType.CAMEL_QUARKUS.getValue().equals(productName)) {
            Assertions.assertEquals(0, Files.lines(propertiesFile.get())
                    .filter(line -> line.contains("test.prop.not.exists")).count(),
                "application.properties contains test.prop.not.exists property");
        }

        Optional<Path> pomFile = Files.walk(app).filter(file -> "pom.xml".equals(file.getFileName().toString()))
            .findAny();

        Assertions.assertTrue(pomFile.isPresent(), "pom.xml file is not present");

        Assertions.assertEquals(1, Files.lines(pomFile.get())
            .filter(line -> line.contains(directArtifactId)).count(), "pom.xml does not contains " + directArtifactId + " dependency");
    }
}
