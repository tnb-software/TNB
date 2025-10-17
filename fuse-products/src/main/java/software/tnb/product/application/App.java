package software.tnb.product.application;

import software.tnb.common.config.TestConfiguration;
import software.tnb.common.exception.FailureConditionMetException;
import software.tnb.common.utils.WaitUtils;
import software.tnb.common.utils.waiter.Waiter;
import software.tnb.product.endpoint.Endpoint;
import software.tnb.product.integration.builder.AbstractIntegrationBuilder;
import software.tnb.product.integration.generator.IntegrationGenerator;
import software.tnb.product.log.Log;
import software.tnb.product.log.OpenshiftLog;
import software.tnb.product.log.stream.FileLogStream;
import software.tnb.product.log.stream.LogStream;
import software.tnb.product.rp.Attachments;
import software.tnb.product.util.maven.Maven;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.javaparser.ast.CompilationUnit;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public abstract class App {
    private static final Logger LOG = LoggerFactory.getLogger(App.class);

    protected AbstractIntegrationBuilder<?> integrationBuilder;
    protected final String logFilePrefix;
    protected Log log;
    protected LogStream logStream;
    protected Endpoint endpoint;
    protected boolean started = false;
    // store the name from the integration builder, as when creating multiple apps from the same integration builder by just overriding its name
    // it would always report the last name set
    private final String name;
    // Append the counter to the log file name in case the app is restarted during the test
    protected int logCounter = 0;

    private static final String JBANG_SCRIPT_NAME = "camel";
    protected static boolean camelInPath = false;

    public App(AbstractIntegrationBuilder<?> integrationBuilder) {
        this(integrationBuilder.getIntegrationName());
        this.integrationBuilder = integrationBuilder;
    }

    protected App(String name) {
        this.name = name;
        ensureDirNotPresent(name);
        logFilePrefix = name + "-" + new Date().getTime() + "-";
    }

    private void ensureDirNotPresent(String name) {
        final File target = TestConfiguration.appLocation().resolve(name).toFile();
        if (target.exists()) {
            try {
                LOG.debug("Deleting directory with existing application sources {}", target);
                FileUtils.deleteDirectory(target);
            } catch (IOException e) {
                throw new RuntimeException("Couldn't delete existing application sources directory", e);
            }
        }
    }

    protected boolean shouldRun() {
        return true;
    }

    public abstract void start();

    public void stop() {
        if (logStream != null) {
            logStream.stop();
        }

        if (getLog() != null) {
            if (getLog() instanceof OpenshiftLog) {
                ((OpenshiftLog) getLog()).save(started);
            } else {
                getLog().save();
            }
        }

        started = false;
    }

    public abstract void kill();

    /**
     * Stops the app, starts it back again and waits until it's ready.
     */
    public void restart() {
        if (started) {
            stop();
        }
        start();
        waitUntilReady();
    }

    public abstract boolean isReady();

    public abstract boolean isFailed();

    public Log getLog() {
        return log;
    }

    public String getEndpoint() {
        return endpoint.getAddress();
    }

    public String getName() {
        return name;
    }

    public Path getLogPath(Phase phase) {
        // only append the logCounter when the app was already started (so not for the first build/run)
        return TestConfiguration.appLocation().resolve(logFilePrefix + phase.name().toLowerCase()
            + (logCounter > 1 ? "-" + logCounter : "") + ".log");
    }

    public Path getLogPath() {
        return getLogPath(Phase.RUN);
    }

    public void waitUntilReady() {
        if (shouldRun()) {
            Supplier<FailureConditionMetException> exception = () -> {
                String message = "The Camel app failed to start";

                if (!TestConfiguration.streamLogs()) {
                    // Append the application log to the exception message
                    message += ":\n" + getLog().toString();
                }

                return new FailureConditionMetException(message);
            };

            WaitUtils.waitFor(new Waiter(() -> isReady() && getLog().containsRegex(integrationBuilder.getStartupRegex()),
                "Waiting until the integration " + getName() + " is running")
                .failureCondition(this::isFailed)
                .retryTimeout(1000L)
                .failureException(exception)
            );
            started = true;
        }
    }

    protected void customizePlugins(List<Plugin> mavenPlugins) {
        File pom = TestConfiguration.appLocation().resolve(getName()).resolve("pom.xml").toFile();
        Model model = Maven.loadPom(pom);

        if (model.getBuild() != null && model.getBuild().getPlugins() != null) {
            mavenPlugins.forEach(model.getBuild().getPlugins()::add);
        }

        Maven.writePom(pom, model);
    }

    protected List<String> systemProperties() {
        return integrationBuilder.getSystemProperties().entrySet().stream().map(e -> "-D" + e.getKey() + "=" + e.getValue()).toList();
    }

    /**
     * Creates an application skeleton using camel export command.
     *
     * @param arguments command to execute. "camel export --kamelets-version xxx --dir xxx --gav xxx integrationClass.java" are added automatically.
     */
    protected void createUsingJBang(List<String> arguments) {
        if (!camelInPath) {
            if (Arrays.stream(System.getenv("PATH").split(Pattern.quote(File.pathSeparator))).map(Paths::get)
                .noneMatch(dir -> Files.exists(dir.resolve(JBANG_SCRIPT_NAME)))) {
                throw new RuntimeException("To use JBang a script named '" + JBANG_SCRIPT_NAME + "' must be in your PATH");
            }

            camelInPath = true;
        }

        ProcessBuilder processBuilder = new ProcessBuilder("camel", "--version");
        String version;
        try {
            Process process = processBuilder.start();
            version = new String(process.getInputStream().readAllBytes());
        } catch (IOException e) {
            version = "N/A";
        }

        LOG.info("Creating {} application project for integration {} using Camel JBang {}", TestConfiguration.product(), getName(), version);

        final Path appDir = IntegrationGenerator.createApplicationDirectory(integrationBuilder);

        IntegrationGenerator.processCustomizers(integrationBuilder);
        IntegrationGenerator.createAdditionalClasses(integrationBuilder, appDir);
        IntegrationGenerator.createRouteBuilderClasses(integrationBuilder, appDir);

        List<String> command = new ArrayList<>(List.of(
            "camel", "export",
            "--gav", TestConfiguration.appGroupId() + ":" + getName() + ":" + TestConfiguration.appVersion(),
            "--dir", ".",
            "--logging"
        ));

        command.addAll(arguments);

        if (TestConfiguration.kameletsVersion() != null) {
            command.add("--kamelets-version");
            command.add(TestConfiguration.kameletsVersion());
        }

        if (!integrationBuilder.getDependencies().isEmpty()) {
            command.add("--dep");
            command.add(integrationBuilder.getDependencies().stream().map(d -> {
                // Convert runtime-specific camel dependencies to camel:<dependency> format
                if (d.getArtifactId().startsWith("camel-quarkus-")) {
                    return "camel:" + StringUtils.substringAfter(d.getArtifactId(), "camel-quarkus-");
                } else if (d.getArtifactId().startsWith("camel-") && d.getArtifactId().endsWith("-starter")) {
                    return "camel:" + StringUtils.substringBetween(d.getArtifactId(), "camel-", "-starter");
                } else {
                    String dep = d.getGroupId() + ":" + d.getArtifactId();
                    if (d.getVersion() != null) {
                        dep += ":" + d.getVersion();
                    }
                    return dep;
                }
            }).collect(Collectors.joining(",")));
        }

        List<CompilationUnit> classes = new ArrayList<>();
        classes.addAll(integrationBuilder.getRouteBuilders());
        classes.addAll(integrationBuilder.getAdditionalClasses());

        command.addAll(classes.stream().map(cu -> cu.getPrimaryTypeName().orElse(cu.getType(0).getNameAsString()) + ".java").toList());

        // application.properties are loaded automatically when they are in the root of the dir
        IntegrationGenerator.createApplicationProperties(integrationBuilder, appDir);

        LOG.trace("Camel JBang command: {}", String.join(" ", command));

        int exitCode = -1;
        boolean hasExited;
        File logFile = new File(getLogPath(Phase.GENERATE).toAbsolutePath().toString());
        LogStream generateLogStream = new FileLogStream(logFile.toPath(), LogStream.marker(getName(), Phase.GENERATE));
        try {
            ProcessBuilder pb = new ProcessBuilder(command);
            pb.redirectOutput(logFile);
            pb.redirectErrorStream(true);
            pb.directory(TestConfiguration.appLocation().resolve(getName()).toFile());
            final Process process = pb.start();
            hasExited = process.waitFor(20, TimeUnit.MINUTES);
            if (hasExited) {
                exitCode = process.exitValue();
            }
        } catch (Exception e) {
            throw new RuntimeException("Unable to export integration using Camel JBang: ", e);
        } finally {
            generateLogStream.stop();
        }

        Attachments.addAttachment(logFile.toPath());

        if (!hasExited) {
            throw new RuntimeException("camel export invocation did not end in 1 hour, check " + logFile + " for more details");
        }

        if (exitCode != 0) {
            throw new RuntimeException("camel export invocation failed with exit code " + exitCode + ", check " + logFile + " for more details");
        }

        IntegrationGenerator.createResourceFiles(integrationBuilder, appDir);
    }

    public int getPort() {
        return integrationBuilder.getPort();
    }
}
