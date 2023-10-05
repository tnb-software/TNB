package software.tnb.product.cq.application;

import software.tnb.common.config.TestConfiguration;
import software.tnb.common.utils.WaitUtils;
import software.tnb.product.cq.configuration.QuarkusConfiguration;
import software.tnb.product.endpoint.Endpoint;
import software.tnb.product.integration.builder.AbstractIntegrationBuilder;
import software.tnb.product.log.FileLog;
import software.tnb.product.log.stream.FileLogStream;
import software.tnb.product.log.stream.LogStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class LocalQuarkusApp extends QuarkusApp {
    private static final Logger LOG = LoggerFactory.getLogger(LocalQuarkusApp.class);
    private Process appProcess;
    private final AbstractIntegrationBuilder<?> integrationBuilder;

    public LocalQuarkusApp(AbstractIntegrationBuilder<?> integrationBuilder) {
        super(integrationBuilder);

        this.integrationBuilder = integrationBuilder;
        endpoint = new Endpoint(() -> "http://localhost:" + integrationBuilder.getPort());
    }

    @Override
    public void start() {
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
            List<String> args = this.integrationBuilder.getProperties() != null ? this.integrationBuilder.getProperties().entrySet().stream()
                .map(e -> "-D" + e.getKey() + "=" + e.getValue()).collect(Collectors.toList()) : Collections.emptyList();

            cmd.add(System.getProperty("java.home") + "/bin/java");
            cmd.addAll(args);
            cmd.add("-jar");
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
