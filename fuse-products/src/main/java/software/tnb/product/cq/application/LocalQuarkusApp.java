package software.tnb.product.cq.application;

import software.tnb.common.config.TestConfiguration;
import software.tnb.common.exception.TimeoutException;
import software.tnb.common.utils.HTTPUtils;
import software.tnb.common.utils.WaitUtils;
import software.tnb.common.utils.waiter.Waiter;
import software.tnb.product.application.Phase;
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
        logCounter++;
        Path logFile = getLogPath();
        ProcessBuilder processBuilder = new ProcessBuilder(getCommand()).redirectOutput(logFile.toFile());

        LOG.info("Starting integration {}", getName());
        try {
            appProcess = processBuilder.start();
        } catch (IOException e) {
            throw new RuntimeException("Unable to start integration process: ", e);
        }
        WaitUtils.waitFor(new Waiter(() -> logFile.toFile().exists(), "Waiting until the logfile is created"));

        log = new FileLog(logFile);
        logStream = new FileLogStream(logFile, LogStream.marker(getName(), Phase.RUN));

        if (TestConfiguration.appDebug()) {
            LOG.warn("App started with debug mode enabled. Connect the debugger to port {}, otherwise the app never reaches ready state",
                TestConfiguration.appDebugPort());
        }
    }

    @Override
    public void stop() {
        if (appProcess != null) {
            LOG.info("Stopping integration {}", getName());
            if (appProcess.isAlive()) {
                LOG.debug("Killing integration process");
                appProcess.destroy();
                try {
                    WaitUtils.waitFor(new Waiter(() -> !isReady(), "Waiting until the process is stopped").timeout(600, 100));
                } catch (TimeoutException e) {
                    LOG.warn("Integration process did not terminate normally, calling force destroy");
                    appProcess.destroyForcibly();
                }
            }
        }

        super.stop();
    }

    @Override
    public void kill() {
        LOG.info("Killing integration {}", getName());
        appProcess.destroyForcibly();
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
        Path appDir = TestConfiguration.appLocation().resolve(getName()).toAbsolutePath();
        Path integrationTarget = appDir.resolve("target");

        if (QuarkusConfiguration.isQuarkusNative()) {
            fileName = integrationTarget.resolve(getName() + "-1.0.0-SNAPSHOT-runner").toAbsolutePath().toString();
            cmd.add(fileName);
            cmd.addAll(systemProperties());
        } else {
            cmd.add(System.getProperty("java.home") + "/bin/java");
            cmd.addAll(systemProperties());

            if (TestConfiguration.appDebug()) {
                cmd.add("-Xdebug");
                cmd.add("-Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=0.0.0.0:" + TestConfiguration.appDebugPort());
            }

            cmd.addAll(integrationBuilder.getJavaAgents().stream().map(a -> "-javaagent:"
                + (a.contains(appDir.toString()) ? a : appDir.resolve(a).toAbsolutePath())).toList());
            integrationBuilder.getVmArguments().stream().map(vmArgument -> "-" + vmArgument).forEach(cmd::add);

            cmd.add("-jar");
            fileName = integrationTarget.resolve("quarkus-app/quarkus-run.jar").toAbsolutePath().toString();
            cmd.add(fileName);
        }

        if (!new File(fileName).exists()) {
            throw new IllegalArgumentException("Expected file " + fileName + " does not exist, check if the maven build was successful");
        }

        LOG.debug("ProcessBuilder command: {}", String.join(" ", cmd));
        return cmd;
    }
}
