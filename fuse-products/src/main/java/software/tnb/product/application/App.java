package software.tnb.product.application;

import software.tnb.common.config.TestConfiguration;
import software.tnb.common.utils.WaitUtils;
import software.tnb.product.endpoint.Endpoint;
import software.tnb.product.integration.builder.AbstractIntegrationBuilder;
import software.tnb.product.integration.generator.IntegrationGenerator;
import software.tnb.product.log.Log;
import software.tnb.product.log.stream.LogStream;
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
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public abstract class App {
    private static final Pattern LOG_STARTED_REGEX = Pattern.compile("(?m)^.*Apache Camel.*started in.*$");
    private static final Logger LOG = LoggerFactory.getLogger(App.class);

    protected AbstractIntegrationBuilder<?> integrationBuilder;
    // Integrations for camel-k can be created without integration builder object, therefore keeping track also of "name" only
    protected String name;
    protected final String logFilePrefix;
    protected Log log;
    protected LogStream logStream;
    protected Endpoint endpoint;
    protected boolean started = false;

    private static final String JBANG_SCRIPT_NAME = "camel";
    protected static boolean camelInPath = false;

    public App(AbstractIntegrationBuilder<?> integrationBuilder) {
        this(integrationBuilder.getIntegrationName());
        this.integrationBuilder = integrationBuilder;
    }

    protected App(String name) {
        this.name = name;
        ensureDirNotPresent();
        logFilePrefix = name + "-" + new Date().getTime() + "-";
    }

    private void ensureDirNotPresent() {
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

    public abstract void stop();

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
        return TestConfiguration.appLocation().resolve(logFilePrefix + phase.name().toLowerCase() + ".log");
    }

    public Path getLogPath() {
        return getLogPath(Phase.RUN);
    }

    public void waitUntilReady() {
        if (shouldRun()) {
            WaitUtils.waitFor(() -> isReady() && isCamelStarted(), this::isFailed, 1000L, "Waiting until the integration " + name + " is running");
            started = true;
        }
    }

    private boolean isCamelStarted() {
        return getLog().containsRegex(LOG_STARTED_REGEX);
    }

    protected void customizePlugins(List<Plugin> mavenPlugins) {
        File pom = TestConfiguration.appLocation().resolve(name).resolve("pom.xml").toFile();
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

        LOG.info("Creating Camel Quarkus application project for integration {} using Camel JBang", name);

        final Path appDir = IntegrationGenerator.createApplicationDirectory(integrationBuilder);

        IntegrationGenerator.processCustomizers(integrationBuilder);
        IntegrationGenerator.createAdditionalClasses(integrationBuilder, appDir);
        IntegrationGenerator.createIntegrationClass(integrationBuilder, appDir);

        List<String> command = new ArrayList<>(List.of(
            "camel", "export",
            "--kamelets-version", TestConfiguration.kameletsVersion(),
            "--gav", TestConfiguration.appGroupId() + ":" + integrationBuilder.getIntegrationName() + ":" + TestConfiguration.appVersion(),
            "--dir", "."
        ));

        command.addAll(arguments);

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

        command.add(integrationBuilder.getFileName());

        List<CompilationUnit> additionalClasses = integrationBuilder.getAdditionalClasses();
        if (!additionalClasses.isEmpty()) {
            command.addAll(additionalClasses.stream().map(cu -> cu.getPrimaryTypeName().orElse(cu.getType(0).getNameAsString()) + ".java")
                .toList());
        }

        // application.properties are loaded automatically when they are in the root of the dir
        IntegrationGenerator.createApplicationProperties(integrationBuilder, appDir);

        LOG.trace("Camel JBang command: {}", String.join(" ", command));

        int exitCode;
        File logFile = new File(getLogPath(Phase.BUILD).toAbsolutePath().toString());
        try {
            ProcessBuilder pb = new ProcessBuilder(command);
            pb.redirectOutput(logFile);
            pb.redirectErrorStream(true);
            pb.directory(TestConfiguration.appLocation().resolve(integrationBuilder.getIntegrationName()).toFile());
            final Process start = pb.start();
            exitCode = start.waitFor();
        } catch (Exception e) {
            throw new RuntimeException("Unable to export integration using Camel JBang: ", e);
        }

        if (exitCode != 0) {
            throw new RuntimeException("camel export invocation failed with exit code " + exitCode + ", check " + logFile + " for more details");
        }

        IntegrationGenerator.createResourceFiles(integrationBuilder, appDir);
    }
}
