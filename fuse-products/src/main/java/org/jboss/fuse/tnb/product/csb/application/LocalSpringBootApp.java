package org.jboss.fuse.tnb.product.csb.application;

import org.jboss.fuse.tnb.common.config.TestConfiguration;
import org.jboss.fuse.tnb.common.utils.WaitUtils;
import org.jboss.fuse.tnb.product.endpoint.Endpoint;
import org.jboss.fuse.tnb.product.integration.builder.AbstractIntegrationBuilder;
import org.jboss.fuse.tnb.product.integration.builder.AbstractMavenGitIntegrationBuilder;
import org.jboss.fuse.tnb.product.log.FileLog;
import org.jboss.fuse.tnb.product.log.stream.FileLogStream;
import org.jboss.fuse.tnb.product.log.stream.LogStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class LocalSpringBootApp extends SpringBootApp {
    private static final Logger LOG = LoggerFactory.getLogger(LocalSpringBootApp.class);
    private final Path logFile;
    private final List<String> command;
    private final String fileName;
    private Process appProcess;

    public LocalSpringBootApp(AbstractIntegrationBuilder<?> integrationBuilder) {
        super(integrationBuilder);

        List<String> args;
        String jarName;

        final Path projectPath;
        if (mavenGitApp != null) {
            args = ((AbstractMavenGitIntegrationBuilder<?>) integrationBuilder).getJavaProperties().entrySet()
                .stream().map(e -> "-D" + e.getKey() + "=" + e.getValue()).collect(Collectors.toList());
            jarName = mavenGitApp.getFinalName().map(n -> n + ".jar").orElse(name);
            projectPath = mavenGitApp.getProjectLocation();
        } else {
            args = integrationBuilder.getProperties() != null ? integrationBuilder.getProperties().entrySet().stream()
                .map(e -> "-D" + e.getKey() + "=" + e.getValue()).collect(Collectors.toList()) : Collections.emptyList();
            jarName = name + "-1.0.0-SNAPSHOT.jar";
            projectPath = TestConfiguration.appLocation().resolve(name);
        }

        Path integrationTarget = projectPath.resolve("target");

        command = new ArrayList<>(List.of(System.getProperty("java.home") + "/bin/java"));

        command.addAll(args);
        command.add("-jar");
        fileName = integrationTarget.resolve(jarName).toAbsolutePath().toString();

        command.add(fileName);

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
        logStream = new FileLogStream(logFile, LogStream.marker(name));
    }

    @Override
    public void stop() {
        if (logStream != null) {
            logStream.stop();
        }
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
        if (!new File(fileName).exists()) {
            throw new IllegalArgumentException("Expected file " + fileName + " does not exist, check if the maven build was successful");
        }

        LOG.debug("ProcessBuilder command: " + String.join(" ", command));
        return command;
    }
}
