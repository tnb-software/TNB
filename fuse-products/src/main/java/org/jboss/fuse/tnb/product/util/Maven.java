package org.jboss.fuse.tnb.product.util;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.DefaultInvoker;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.InvocationResult;
import org.apache.maven.shared.invoker.Invoker;
import org.apache.maven.shared.invoker.MavenInvocationException;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.jboss.fuse.tnb.common.utils.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

public final class Maven {
    private static final Logger log = LoggerFactory.getLogger("Maven");
    private static Invoker invoker;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("HH-mm-ss").withZone(ZoneId.systemDefault());

    private static InvocationRequest newRequest() {
        if (invoker == null) {
            invoker = new DefaultInvoker();
        }
        return new DefaultInvocationRequest();
    }

    public static void setupMaven() {
        if (System.getProperty("maven.home") != null) {
            // Do nothing as the maven.home is what we need
            return;
        }

        log.debug("M2_HOME env property is " + System.getenv("M2_HOME"));
        if (System.getenv("M2_HOME") != null) {
            System.setProperty("maven.home", System.getenv("M2_HOME"));
            return;
        }

        log.debug("M2_HOME system property is " + System.getProperty("M2_HOME"));
        if (System.getProperty("M2_HOME") != null) {
            System.setProperty("maven.home", System.getProperty("M2_HOME"));
            return;
        }

        String mvnLocation = null;
        for (String option : Arrays.asList("maven", "mvn")) {
            final Optional<String> mvn = Arrays.stream(System.getenv("PATH").split(File.pathSeparator))
                .filter(p -> p.contains(option))
                .findFirst();
            if (mvn.isPresent()) {
                mvnLocation = StringUtils.substringBeforeLast(mvn.get(), "bin");
                log.debug("Using maven from {}", mvnLocation);
                System.setProperty("maven.home", mvnLocation);
                break;
            }
        }

        if (mvnLocation == null) {
            throw new RuntimeException("No maven found in system/environment properties nor in PATH");
        }
    }

    public static void createFromArchetype(String archetypeGroupId, String archetypeArtifactId, String archetypeVersion, String appGroupId,
        String appArtifactId, File location) {
        if (!location.exists()) {
            if (!location.mkdirs()) {
                throw new RuntimeException("Unable to create directory: " + location.getAbsolutePath());
            }
        }
        invoke(location.toPath(), Collections.singletonList("archetype:generate"), MapUtils.toProperties(Map.of(
            "archetypeGroupId", archetypeGroupId,
            "archetypeArtifactId", archetypeArtifactId,
            "archetypeVersion", archetypeVersion,
            "groupId", appGroupId,
            "artifactId", appArtifactId
        )));
    }

    public static void invoke(Path dir, List<String> goals, Properties properties) {
        invoke(dir, goals, null, properties);
    }

    public static void invoke(Path dir, List<String> goals, List<String> profiles, Properties properties) {
        String logFile = "target/maven-invocation-" + FORMATTER.format(Instant.now()) + ".log";
        log.debug("Using {} log file for the invocation", logFile);
        invoke(dir, goals, profiles, properties, logFile);
    }

    public static void invoke(Path dir, List<String> goals, List<String> profiles, Properties properties, String logFile) {
        InvocationResult result;

        StringBuilder propertiesLog = new StringBuilder("Invoking maven with:" + "\n" +
            "  Base dir: " + dir.toAbsolutePath() + "\n" +
            "  Goals: " + goals.toString() + "\n"
        );
        if (properties != null && !properties.isEmpty()) {
            propertiesLog.append("  Properties").append("\n");
            properties.forEach((key, value) -> propertiesLog.append("    ").append(key).append(": ").append(value).append("\n"));
        }
        log.debug(propertiesLog.substring(0, propertiesLog.length() - 1));
        try {
            InvocationRequest request = newRequest()
                .setBaseDirectory(dir.toFile())
                .setBatchMode(true)
                .setGoals(goals)
                .setProfiles(profiles)
                .setProperties(properties)
                .setOutputHandler(s -> FileUtils.writeStringToFile(new File(logFile), s + "\n", "UTF-8", true));
            result = invoker.execute(request);
        } catch (MavenInvocationException e) {
            throw new RuntimeException("Error while executing maven: ", e);
        }

        if (result.getExitCode() != 0) {
            throw new RuntimeException("Maven invocation failed with exit code " + result.getExitCode() + ", check " + logFile + " for more details");
        }
    }

    public static void addComponentDependencies(Path dir, String... dependencies) {
        if (dependencies == null || dependencies.length == 0) {
            return;
        }
        File pom = dir.resolve("pom.xml").toFile();
        log.info("Adding {} as dependencies to {}", Arrays.toString(dependencies), pom);
        Model model;
        try (InputStream is = new FileInputStream(pom)) {
            model = new MavenXpp3Reader().read(is);
        } catch (IOException | XmlPullParserException e) {
            throw new RuntimeException("Unable to load POM " + pom.getAbsolutePath(), e);
        }

        for (String dependency : dependencies) {
            Dependency dep = new Dependency();
            dep.setGroupId("org.apache.camel");
            dep.setArtifactId("camel-" + dependency);
            model.getDependencies().add(dep);
        }

        try (OutputStream os = new FileOutputStream(pom)) {
            new MavenXpp3Writer().write(os, model);
        } catch (IOException e) {
            throw new RuntimeException("Unable to write POM " + pom.getAbsolutePath(), e);
        }
    }
}
