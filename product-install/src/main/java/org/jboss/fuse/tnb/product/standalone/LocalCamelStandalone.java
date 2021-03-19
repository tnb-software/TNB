package org.jboss.fuse.tnb.product.standalone;

import org.apache.commons.io.FileUtils;
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

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;

@AutoService(Product.class)
public class LocalCamelStandalone extends Product {
    private static final Logger log = LoggerFactory.getLogger("LocalCamelStandalone");
    private final ExecutorService executorService = Executors.newFixedThreadPool(1);
    private String logFile;

    @Override
    public void deploy() {
    }

    @Override
    public void undeploy() {
    }

    @Override
    public void deployIntegration(String name, CodeBlock routeDefinition, String... camelComponents) {
        File location = new File("target");
        Maven.createFromArchetype(
            "org.apache.camel.archetypes",
            "camel-archetype-main",
            TestConfiguration.camelVersion(),
            TestConfiguration.appGroupId(),
            name,
            location
        );

        RouteBuilderGenerator.toFile(routeDefinition, Paths.get("target", name, "src", "main", "java"));

        File folder = new File(location, name);
        File pom = new File(folder, "pom.xml");

        Maven.addComponentDependencies(pom, camelComponents);

        logFile = String.format("target/%s.log", name);

        executorService.submit(() -> Maven.invoke(
            folder,
            pom,
            Arrays.asList("compile", "exec:java"),
            MapUtils.toProperties(MapUtils.map("exec.mainClass", "com.test.MyApplication")),
            logFile
            )
        );

        waitForIntegration(name);
    }

    @Override
    public void waitForIntegration(String name) {
        log.info("Waiting until integration {} is running", name);
        try {
            // todo fail fast when there is an exception / error
            WaitUtils.waitFor(() -> {
                try {
                    return FileUtils.readFileToString(new File(logFile), "UTF-8").contains("started and consuming");
                } catch (IOException e) {
                    // Ignored as the file may not exist yet
                }
                return false;
            }, 300, 1000L);
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void undeployIntegration() {
        executorService.shutdown();
    }
}
