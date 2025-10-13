package software.tnb.product.csb.application;

import software.tnb.common.config.TestConfiguration;
import software.tnb.common.exception.TimeoutException;
import software.tnb.common.utils.WaitUtils;
import software.tnb.common.utils.waiter.Waiter;
import software.tnb.product.endpoint.Endpoint;
import software.tnb.product.integration.builder.AbstractIntegrationBuilder;
import software.tnb.product.integration.builder.AbstractMavenGitIntegrationBuilder;
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
        final Path existingJarPath = getExistingJar();
        if (mavenGitApp != null) {
            args = ((AbstractMavenGitIntegrationBuilder<?>) integrationBuilder).getJavaProperties().entrySet()
                .stream().map(e -> "-D" + e.getKey() + "=" + e.getValue()).collect(Collectors.toList());
            jarName = existingJarPath != null ? existingJarPath.getFileName().toString()
                : mavenGitApp.getFinalName().map(n -> n + ".jar").orElse(getName());
            projectPath = existingJarPath != null ? existingJarPath.getParent().getParent() : mavenGitApp.getProjectLocation();
        } else {
            args = systemProperties();
            jarName = existingJarPath != null ? existingJarPath.getFileName().toString() : getName() + "-1.0.0-SNAPSHOT.jar";
            projectPath = existingJarPath != null ? existingJarPath.getParent().getParent() : TestConfiguration.appLocation().resolve(getName());
        }

        Path integrationTarget = projectPath.resolve("target");

        command = new ArrayList<>(List.of(System.getProperty("java.home") + "/bin/java"));

        command.addAll(integrationBuilder.getJavaAgents().stream().map(a -> "-javaagent:"
            + (a.contains(projectPath.toString()) ? a : projectPath.resolve(a).toAbsolutePath())).toList());

        command.addAll(args);

        if (TestConfiguration.appDebug()) {
            command.add("-Xdebug");
            command.add("-Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=0.0.0.0:" + TestConfiguration.appDebugPort());
        }

        if (!integrationBuilder.getVmArguments().isEmpty()) {
            integrationBuilder.getVmArguments().stream()
                .map(vmArgument -> "-" + vmArgument)
                .forEach(command::add);
        }

        command.add("-Dserver.port=" + integrationBuilder.getPort());

        command.add("-jar");
        fileName = integrationTarget.resolve(jarName).toAbsolutePath().toString();

        command.add(fileName);

        endpoint = new Endpoint(() -> "http://localhost:" + integrationBuilder.getPort());
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
            logStream = new FileLogStream(logFile, LogStream.marker(getName()));

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

        LOG.debug("ProcessBuilder command: {}", String.join(" ", command));
        return command;
    }
}
