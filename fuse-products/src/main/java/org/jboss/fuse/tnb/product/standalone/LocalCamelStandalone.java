package org.jboss.fuse.tnb.product.standalone;

import org.jboss.fuse.tnb.common.config.TestConfiguration;
import org.jboss.fuse.tnb.common.utils.MapUtils;
import org.jboss.fuse.tnb.common.utils.WaitUtils;
import org.jboss.fuse.tnb.product.Product;
import org.jboss.fuse.tnb.product.util.Maven;
import org.jboss.fuse.tnb.product.util.RouteBuilderGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.CodeBlock;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@AutoService(Product.class)
public class LocalCamelStandalone extends Product {
    private static final Logger log = LoggerFactory.getLogger("LocalCamelStandalone");
    private final ExecutorService executorService = Executors.newFixedThreadPool(1);
    private Path logFile;

    @Override
    public void deploy() {
        Maven.setupMaven();
    }

    @Override
    public void undeploy() {
    }

    @Override
    public void deployIntegration(String name, CodeBlock routeDefinition, String... camelComponents) {
        log.info("Creating application project");
        Maven.createFromArchetype(
            "org.apache.camel.archetypes",
            "camel-archetype-main",
            TestConfiguration.camelVersion(),
            TestConfiguration.appGroupId(),
            name,
            TestConfiguration.appLocation().toFile()
        );

        RouteBuilderGenerator.toFile(routeDefinition, TestConfiguration.appLocation().resolve(name).resolve("src/main/java"));

        Maven.addComponentDependencies(TestConfiguration.appLocation().resolve(name), camelComponents);

        logFile = TestConfiguration.appLocation().resolve(name + ".log");

        executorService.submit(() -> Maven.invoke(
            TestConfiguration.appLocation().resolve(name),
            Arrays.asList("compile", "exec:java"),
            null,
            MapUtils.toProperties(Map.of("exec.mainClass", "com.test.MyApplication")),
            logFile.toAbsolutePath().toString()
            )
        );

        waitForIntegration(name);
    }

    @Override
    public void waitForIntegration(String name) {
        log.info("Waiting until integration {} is running", name);
        try {
            WaitUtils.waitFor(() -> {
                try {
                    return Files.readString(logFile).contains("started and consuming");
                } catch (IOException e) {
                    // Ignored as the file may not exist yet
                }
                return false;
            }, 60, 1000L);
        } catch (Exception e) {
            throw new RuntimeException("Wait for integration failed: ", e);
        }
    }

    @Override
    public void undeployIntegration() {
        executorService.shutdown();
    }
}
