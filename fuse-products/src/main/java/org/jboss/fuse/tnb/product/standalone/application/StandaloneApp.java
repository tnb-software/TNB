package org.jboss.fuse.tnb.product.standalone.application;

import org.jboss.fuse.tnb.common.config.TestConfiguration;
import org.jboss.fuse.tnb.common.utils.IOUtils;
import org.jboss.fuse.tnb.product.application.App;
import org.jboss.fuse.tnb.product.integration.IntegrationBuilder;
import org.jboss.fuse.tnb.product.integration.IntegrationGenerator;
import org.jboss.fuse.tnb.product.util.BuildRequest;
import org.jboss.fuse.tnb.product.util.Maven;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StandaloneApp extends App {
    private static final Logger LOG = LoggerFactory.getLogger(StandaloneApp.class);

    private final ExecutorService executorService = Executors.newFixedThreadPool(1);
    private BuildRequest buildRequest;
    boolean isRunning = true;

    public StandaloneApp(String name, IntegrationBuilder integrationBuilder, String... camelComponents) {
        super(name);

        LOG.info("Creating Camel Standalone application project for integration {}", name);
        Maven.createFromArchetype(
            "org.apache.camel.archetypes",
            "camel-archetype-main",
            TestConfiguration.camelVersion(),
            TestConfiguration.appGroupId(),
            name,
            TestConfiguration.appLocation().toFile()
        );

        IntegrationGenerator.toFile(integrationBuilder, TestConfiguration.appLocation().resolve(name));

        Maven.addCamelComponentDependencies(TestConfiguration.appLocation().resolve(name), camelComponents);
    }

    @Override
    public void start() {
        buildRequest = new BuildRequest.Builder()
            .withBaseDirectory(TestConfiguration.appLocation().resolve(name))
            .withGoals("compile", "camel:run")
            .withLogFile(TestConfiguration.appLocation().resolve(name + ".log"))
            .build();

        executorService.submit(() -> {
            try {
                Maven.invoke(buildRequest);
            } catch (Exception e) {
                LOG.error("Error while running app: ", e);
            } finally {
                isRunning = false;
            }
        });
    }

    @Override
    public void stop() {
        IOUtils.writeFile(buildRequest.getLogFile(), buildRequest.getOutputHandler().getOutput());
        executorService.shutdown();
    }

    @Override
    public boolean isReady() {
        return isRunning && isCamelStarted();
    }

    @Override
    public boolean isFailed() {
        return !isRunning;
    }

    @Override
    public String getLogs() {
        return buildRequest.getOutputHandler().getOutput();
    }
}
