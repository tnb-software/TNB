package org.jboss.fuse.tnb.product.cq.application;

import org.jboss.fuse.tnb.common.config.QuarkusConfiguration;
import org.jboss.fuse.tnb.common.config.TestConfiguration;
import org.jboss.fuse.tnb.common.utils.WaitUtils;
import org.jboss.fuse.tnb.product.endpoint.Endpoint;
import org.jboss.fuse.tnb.product.integration.builder.AbstractIntegrationBuilder;
import org.jboss.fuse.tnb.product.log.FileLog;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LocalQuarkusApp extends QuarkusApp {
    private static final Logger LOG = LoggerFactory.getLogger(LocalQuarkusApp.class);
    private final Path logFile;
    private Process appProcess;

    public LocalQuarkusApp(AbstractIntegrationBuilder<?> integrationBuilder) {
        super(integrationBuilder);

        logFile = TestConfiguration.appLocation().resolve(name + ".log");
        endpoint = new Endpoint(() -> "http://localhost:" + integrationBuilder.getPort());
    }

    @Override
    public void start() {
        ProcessBuilder processBuilder = new ProcessBuilder(getCommand()).redirectOutput(logFile.toFile());

        LOG.info("Starting integration {}", name);
        try {
            appProcess = processBuilder.start();
        } catch (IOException e) {
            throw new RuntimeException("Unable to start integration process: ", e);
        }
        WaitUtils.waitFor(() -> logFile.toFile().exists(), "Waiting until the logfile is created");
        log = new FileLog(logFile);
    }

    @Override
    public void stop() {
        if (appProcess != null) {
            LOG.info("Stopping integration {}", name);
            if (appProcess.isAlive()) {
                LOG.debug("Killing integration process");
                appProcess.destroy();
            }
        }
    }

    @Override
    public boolean isReady() {
        return appProcess.isAlive();
    }

    @Override
    public boolean isFailed() {
        return !appProcess.isAlive();
    }

    private List<String> getCommand() {
        List<String> cmd = new ArrayList<>();
        String fileName;
        Path integrationTarget = TestConfiguration.appLocation().resolve(name).resolve("target");

        if (QuarkusConfiguration.isQuarkusNative()) {
            fileName = integrationTarget.resolve(name + "-1.0.0-SNAPSHOT-runner").toAbsolutePath().toString();
        } else {
            cmd.addAll(Arrays.asList(System.getProperty("java.home") + "/bin/java", "-jar"));
            fileName = integrationTarget.resolve("quarkus-app/quarkus-run.jar").toAbsolutePath().toString();
        }
        cmd.add(fileName);

        if (!new File(fileName).exists()) {
            throw new IllegalArgumentException("Expected file " + fileName + " does not exist, check if the maven build was successful");
        }

        LOG.debug("ProcessBuilder command: " + String.join(" ", cmd));
        return cmd;
    }
}
