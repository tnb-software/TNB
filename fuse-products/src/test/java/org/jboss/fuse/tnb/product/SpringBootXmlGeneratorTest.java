package org.jboss.fuse.tnb.product;

import org.jboss.fuse.tnb.product.application.App;
import org.jboss.fuse.tnb.product.integration.IntegrationBuilder;
import org.jboss.fuse.tnb.product.util.maven.Maven;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public class SpringBootXmlGeneratorTest {

    @Test
    public void test() throws Exception {
        String productName = "camelspringboot";

        System.setProperty("test.product", productName);
        Maven.setupMaven();
        Product product = ProductFactory.create();

        String appName = "xml-timer-app-" + productName;

        App application = product.createIntegration(new IntegrationBuilder(appName)
            .fromRouteBuilder(new DirectToLogRoute())
            .fromSpringBootXmlCamelContext(
                SpringBootXmlGeneratorTest.class.getPackageName().replace(".", File.separator) + File.separator
                    + "camel-context.xml")
            .dependencies("cron", "log")
        );

        Path app = Paths.get("target", appName);

        Optional<Path> routeBuilderFile = Files.walk(app).filter(file -> "camel-context.xml".equals(file.getFileName().toString()))
            .findAny();

        Assertions.assertTrue(routeBuilderFile.isPresent(), "camel context xml not present");

        Assertions.assertTrue(application.getLog().contains("The message contains I was fired at"));
    }
}
