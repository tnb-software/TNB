package software.tnb.product.cq.application;

import software.tnb.common.config.TestConfiguration;
import software.tnb.common.exception.TimeoutException;
import software.tnb.common.utils.WaitUtils;
import software.tnb.common.utils.waiter.Waiter;
import software.tnb.product.application.Phase;
import software.tnb.product.cq.configuration.QuarkusConfiguration;
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

/**
 * Represents a local quarkus app that is packaged into some artifact (a jar file or native executable).
 */
public class LocalPackagedQuarkusApp extends LocalQuarkusApp {
    private static final Logger LOG = LoggerFactory.getLogger(LocalPackagedQuarkusApp.class);
    private Process appProcess;

    public LocalPackagedQuarkusApp(AbstractIntegrationBuilder<?> integrationBuilder) {
        super(integrationBuilder);

        Path appDirAbsolutePath = appDir.toAbsolutePath();
        Path integrationTarget = appDirAbsolutePath.resolve("target");
        String fileName;
        if (QuarkusConfiguration.isQuarkusNative()) {
            fileName = getName() + "-" + TestConfiguration.appVersion() + "-runner";
        } else {
            boolean isUberJar = integrationBuilder.getApplicationProperties().entrySet().stream()
                .anyMatch(e -> "quarkus.package.jar.type".equals(e.getKey()) && "uber-jar".equals(e.getValue()));
            boolean includeRunnerSuffix = Boolean.parseBoolean(integrationBuilder.getApplicationProperties()
                .getOrDefault("quarkus.package.jar.add-runner-suffix", "true").toString());

            if (isUberJar) {
                fileName = String.format("%s-%s" + (includeRunnerSuffix ? "-runner.jar" : ".jar"),
                    integrationBuilder.getIntegrationName(), TestConfiguration.appVersion());
            } else {
                fileName = "quarkus-app/quarkus-run.jar";
            }
        }
        path = integrationTarget.resolve(fileName).toAbsolutePath().toString();
    }

    @Override
    public void start() {
        if (shouldRun()) {
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
        Path appDirAbsolutePath = appDir.toAbsolutePath();

        if (QuarkusConfiguration.isQuarkusNative()) {
            cmd.add(getPath());
            cmd.addAll(systemProperties());
        } else {
            cmd.add(System.getProperty("java.home") + "/bin/java");
            cmd.addAll(systemProperties());

            if (TestConfiguration.appDebug()) {
                cmd.add("-Xdebug");
                cmd.add("-Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=0.0.0.0:" + TestConfiguration.appDebugPort());
            }

            cmd.addAll(integrationBuilder.getJavaAgents().stream().map(a -> "-javaagent:"
                + (a.contains(appDirAbsolutePath.toString()) ? a : appDirAbsolutePath.resolve(a).toAbsolutePath())).toList());
            integrationBuilder.getVmArguments().stream().map(vmArgument -> "-" + vmArgument).forEach(cmd::add);

            cmd.add("-jar");
            cmd.add(getPath());
        }

        if (!new File(getPath()).exists()) {
            throw new IllegalArgumentException("Expected file " + getPath() + " does not exist, check if the maven build was successful");
        }

        LOG.debug("ProcessBuilder command: {}", String.join(" ", cmd));
        return cmd;
    }
}
