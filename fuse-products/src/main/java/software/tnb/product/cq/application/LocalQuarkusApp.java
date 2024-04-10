package software.tnb.product.cq.application;

import software.tnb.common.config.TestConfiguration;
import software.tnb.common.utils.HTTPUtils;
import software.tnb.common.utils.WaitUtils;
import software.tnb.product.cq.configuration.QuarkusConfiguration;
import software.tnb.product.customizer.Customizer;
import software.tnb.product.customizer.component.rest.RestCustomizer;
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
import java.util.Optional;

public class LocalQuarkusApp extends QuarkusApp {
    private static final Logger LOG = LoggerFactory.getLogger(LocalQuarkusApp.class);
    private Process appProcess;

    public LocalQuarkusApp(AbstractIntegrationBuilder<?> integrationBuilder) {
        super(integrationBuilder);

        endpoint = new Endpoint(() -> "http://localhost:" + integrationBuilder.getPort());

        Optional<Customizer> restCustomizer = integrationBuilder.getCustomizers().stream().filter(c -> c instanceof RestCustomizer).findFirst();
        // For local quarkus app, the HTTP request will fail when the endpoint is not ready, so check if an exception was raised or not
        restCustomizer.ifPresent(customizer ->
            readinessCheck = () -> {
                try {
                    HTTPUtils.getInstance().get(getEndpoint() + ((RestCustomizer) customizer).getReadinessCheckPath());
                    return true;
                } catch (Exception e) {
                    return false;
                }
            }
        );
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

        if (TestConfiguration.appDebug()) {
            LOG.warn("App started with debug mode enabled. Connect the debugger to port {}, otherwise the app never reaches ready state",
                TestConfiguration.appDebugPort());
        }
    }

    @Override
    public void stop() {
        if (logStream != null) {
            logStream.stop();
        }

        if (log != null) {
            log.save();
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
                .map(e -> "-D" + e.getKey() + "=" + e.getValue()).toList() : Collections.emptyList();

            cmd.add(System.getProperty("java.home") + "/bin/java");
            cmd.addAll(args);

            if (TestConfiguration.appDebug()) {
                cmd.add("-Xdebug");
                cmd.add("-Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=0.0.0.0:" + TestConfiguration.appDebugPort());
            }
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
