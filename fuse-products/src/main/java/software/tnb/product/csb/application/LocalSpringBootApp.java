package software.tnb.product.csb.application;

import software.tnb.common.config.TestConfiguration;
import software.tnb.common.utils.WaitUtils;
import software.tnb.product.endpoint.Endpoint;
import software.tnb.product.integration.builder.AbstractIntegrationBuilder;
import software.tnb.product.integration.builder.AbstractMavenGitIntegrationBuilder;
import software.tnb.product.log.FileLog;
import software.tnb.product.log.stream.FileLogStream;
import software.tnb.product.log.stream.LogStream;

import org.apache.commons.lang3.StringUtils;
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
    private final List<String> command;
    private final String fileName;
    private Process appProcess;

    public LocalSpringBootApp(AbstractIntegrationBuilder<?> integrationBuilder) {
        super(integrationBuilder);

        List<String> args;
        String jarName;

        final Path projectPath;
        final Path existingJarPath = getExistingJar(integrationBuilder);
        if (mavenGitApp != null) {
            args = ((AbstractMavenGitIntegrationBuilder<?>) integrationBuilder).getJavaProperties().entrySet()
                .stream().map(e -> "-D" + e.getKey() + "=" + e.getValue()).collect(Collectors.toList());
            jarName = existingJarPath != null ? existingJarPath.getFileName().toString()
                    : mavenGitApp.getFinalName().map(n -> n + ".jar").orElse(name);
            projectPath = existingJarPath != null ? existingJarPath.getParent().getParent() : mavenGitApp.getProjectLocation();
        } else {
            args = integrationBuilder.getProperties() != null ? integrationBuilder.getProperties().entrySet().stream()
                .map(e -> "-D" + e.getKey() + "=" + e.getValue()).collect(Collectors.toList()) : Collections.emptyList();
            jarName = existingJarPath != null ? existingJarPath.getFileName().toString() : name + "-1.0.0-SNAPSHOT.jar";
            projectPath = existingJarPath != null ? existingJarPath.getParent().getParent() : TestConfiguration.appLocation().resolve(name);
        }

        Path integrationTarget = projectPath.resolve("target");

        command = new ArrayList<>(List.of(System.getProperty("java.home") + "/bin/java"));

        if (StringUtils.isNotEmpty(integrationBuilder.getJvmAgentPath())) {
           command.add("-javaagent:" + projectPath.resolve("src/main/resources/").resolve(integrationBuilder.getJvmAgentPath()).toAbsolutePath());
        }

        command.addAll(args);
        command.add("-jar");
        fileName = integrationTarget.resolve(jarName).toAbsolutePath().toString();

        command.add(fileName);

        endpoint = new Endpoint(() -> "http://localhost:" + integrationBuilder.getPort());
    }

    @Override
    public void start() {
        if (shouldRun()) {
            Path logFile = getLogPath();
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
                WaitUtils.waitFor(() -> !isReady(), 600, 100, "Waiting until the process is stopped");
            }
        }
    }

    @Override
    public boolean isReady() {
        return appProcess != null && appProcess.isAlive();
    }

    @Override
    public boolean isFailed() {
        return appProcess != null && !appProcess.isAlive();
    }

    private List<String> getCommand() {
        if (!new File(fileName).exists()) {
            throw new IllegalArgumentException("Expected file " + fileName + " does not exist, check if the maven build was successful");
        }

        LOG.debug("ProcessBuilder command: " + String.join(" ", command));
        return command;
    }
}
