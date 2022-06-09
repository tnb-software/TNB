package software.tnb.product;

import software.tnb.util.maven.TestMaven;
import software.tnb.common.utils.WaitUtils;
import software.tnb.product.application.App;
import software.tnb.product.csb.integration.builder.SpringBootIntegrationBuilder;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@Tag("integration")
public class SpringBootXmlGeneratorTest {

    @Test
    public void test() throws Exception {
        String productName = "camelspringboot";

        System.setProperty("test.product", productName);
        TestMaven.setupDefaultMaven();
        Product product = ProductFactory.create();

        String appName = "xml-timer-app-" + productName;

        App application = product.createIntegration(new SpringBootIntegrationBuilder(appName)
            .fromSpringBootXmlCamelContext(
                SpringBootXmlGeneratorTest.class.getPackageName().replace(".", File.separator) + File.separator
                    + "camel-context.xml")
            .dependencies("cron", "log")
        );

        Path app = Paths.get("target", appName);

        Optional<Path> routeBuilderFile = Files.walk(app).filter(file -> "camel-context.xml".equals(file.getFileName().toString()))
            .findAny();

        Assertions.assertTrue(routeBuilderFile.isPresent(), "camel context xml not present");

        WaitUtils.sleep(3000L);

        Assertions.assertTrue(application.getLog().contains("The message contains I was fired at"));
    }
}
