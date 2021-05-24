package org.jboss.fuse.tnb.product.standalone.application;

import org.jboss.fuse.tnb.common.config.TestConfiguration;
import org.jboss.fuse.tnb.common.utils.IOUtils;
import org.jboss.fuse.tnb.product.application.App;
import org.jboss.fuse.tnb.product.integration.IntegrationBuilder;
import org.jboss.fuse.tnb.product.integration.IntegrationGenerator;
import org.jboss.fuse.tnb.product.log.MavenLog;
import org.jboss.fuse.tnb.product.util.maven.BuildRequest;
import org.jboss.fuse.tnb.product.util.maven.Maven;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StandaloneApp extends App {
    private static final Logger LOG = LoggerFactory.getLogger(StandaloneApp.class);

    private final ExecutorService executorService = Executors.newFixedThreadPool(1);
    private BuildRequest buildRequest;
    boolean isRunning = true;

    public StandaloneApp(IntegrationBuilder integrationBuilder) {
        super(integrationBuilder.getIntegrationName());

        LOG.info("Creating Camel Standalone application project for integration {}", name);
        if (!TestConfiguration.appLocation().resolve(TestConfiguration.appTemplateName()).toFile().exists()) {
            IOUtils.createDirectory(TestConfiguration.appLocation());

            LOG.debug("Creating standalone app template in {}", TestConfiguration.appLocation().toAbsolutePath());
            Maven.invoke(new BuildRequest.Builder()
                .withBaseDirectory(TestConfiguration.appLocation())
                .withGoals("archetype:generate")
                .withProperties(Map.of(
                    "archetypeGroupId", "org.apache.camel.archetypes",
                    "archetypeArtifactId", "camel-archetype-main",
                    "archetypeVersion", TestConfiguration.camelVersion(),
                    "groupId", TestConfiguration.appGroupId(),
                    "artifactId", TestConfiguration.appTemplateName()))
                .build()
            );
        }

        IOUtils.createDirectory(TestConfiguration.appLocation().resolve(name));
        IOUtils.copyDirectory(TestConfiguration.appLocation().resolve(TestConfiguration.appTemplateName()),
            TestConfiguration.appLocation().resolve(name));

        IntegrationGenerator.toFile(integrationBuilder, TestConfiguration.appLocation().resolve(name));

        Maven.addCamelComponentDependencies(TestConfiguration.appLocation().resolve(name), integrationBuilder.getCamelDependencies());
    }

    @Override
    public void start() {
        buildRequest = new BuildRequest.Builder()
            .withBaseDirectory(TestConfiguration.appLocation().resolve(name))
            .withGoals("compile", "camel:run")
            .withLogFile(TestConfiguration.appLocation().resolve(name + ".log"))
            .build();

        log = new MavenLog(buildRequest.getOutputHandler());

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
}
