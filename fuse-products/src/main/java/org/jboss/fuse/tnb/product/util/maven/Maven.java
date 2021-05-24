package org.jboss.fuse.tnb.product.util.maven;

import org.jboss.fuse.tnb.common.config.TestConfiguration;

import org.apache.commons.lang3.StringUtils;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.apache.maven.settings.RepositoryPolicy;
import org.apache.maven.settings.Settings;
import org.apache.maven.settings.io.xpp3.SettingsXpp3Writer;
import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.DefaultInvoker;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.InvocationResult;
import org.apache.maven.shared.invoker.Invoker;
import org.apache.maven.shared.invoker.MavenInvocationException;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

/**
 * Class for invoking maven.
 */
public final class Maven {
    private static final Logger LOG = LoggerFactory.getLogger(Maven.class);
    private static Invoker invoker;

    private static InvocationRequest newRequest() {
        if (invoker == null) {
            invoker = new DefaultInvoker();
        }
        return new DefaultInvocationRequest();
    }

    /**
     * Maven invoker needs to have maven.home property set, so try to find it in multiple places.
     */
    public static void setupMaven() {
        LOG.info("Setting up maven");
        if (System.getProperty("maven.home") != null) {
            // Do nothing as the maven.home is what we need
            return;
        }

        LOG.debug("M2_HOME env property is " + System.getenv("M2_HOME"));
        if (System.getenv("M2_HOME") != null) {
            System.setProperty("maven.home", System.getenv("M2_HOME"));
            return;
        }

        LOG.debug("M2_HOME system property is " + System.getProperty("M2_HOME"));
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
                LOG.debug("Using maven from {}", mvnLocation);
                System.setProperty("maven.home", mvnLocation);
                break;
            }
        }

        if (mvnLocation == null) {
            throw new RuntimeException("No maven found in system/environment properties nor in PATH");
        }

        createSettingsXmlFile();
    }

    /**
     * Creates a project from archetype.
     *
     * @param archetypeGroupId archetype group id
     * @param archetypeArtifactId archetype artifact id
     * @param archetypeVersion archetype version
     * @param appGroupId application group id
     * @param appArtifactId application artifact id
     * @param location path where the project will be generated
     */
    public static void createFromArchetype(String archetypeGroupId, String archetypeArtifactId, String archetypeVersion, String appGroupId,
        String appArtifactId, File location) {
        if (!location.exists()) {
            if (!location.mkdirs()) {
                throw new RuntimeException("Unable to create directory: " + location.getAbsolutePath());
            }
        }
        invoke(new BuildRequest.Builder()
            .withBaseDirectory(location)
            .withGoals("archetype:generate")
            .withProperties(Map.of(
                "archetypeGroupId", archetypeGroupId,
                "archetypeArtifactId", archetypeArtifactId,
                "archetypeVersion", archetypeVersion,
                "groupId", appGroupId,
                "artifactId", appArtifactId))
            .build()
        );
    }

    /**
     * Invokes maven.
     *
     * @param buildRequest MavenRequest class instance
     */
    public static void invoke(BuildRequest buildRequest) {
        InvocationResult result;

        File dir = buildRequest.getBaseDirectory();
        Properties properties = buildRequest.getProperties();
        List<String> goals = buildRequest.getGoals();
        List<String> profiles = new ArrayList<>(buildRequest.getProfiles());
        File globalMavenSettings = null;

        if (TestConfiguration.mavenRepository() != null) {
            LOG.debug("Adding {} profile to build profiles", TestConfiguration.mavenProfileId());
            profiles.add(TestConfiguration.mavenProfileId());
            globalMavenSettings = TestConfiguration.appLocation().resolve(TestConfiguration.mavenSettingsFileName()).toFile();
        }

        StringBuilder propertiesLog = new StringBuilder("Invoking maven with:" + "\n" +
            "  Base dir: " + dir.getAbsolutePath() + "\n" +
            "  Goals: " + goals.toString() + "\n" +
            "  Profiles: " + profiles + "\n"
        );
        if (properties != null && !properties.isEmpty()) {
            propertiesLog.append("  Properties").append("\n");
            properties.forEach((key, value) -> propertiesLog.append("    ").append(key).append(": ").append(value).append("\n"));
        }
        LOG.debug(propertiesLog.substring(0, propertiesLog.length() - 1));

        try {
            InvocationRequest request = newRequest()
                .setGlobalSettingsFile(globalMavenSettings)
                .setBaseDirectory(dir)
                .setBatchMode(true)
                .setGoals(goals)
                .setProfiles(profiles)
                .setProperties(properties)
                .setOutputHandler(buildRequest.getOutputHandler())
                .setErrorHandler(buildRequest.getOutputHandler());
            result = invoker.execute(request);
        } catch (MavenInvocationException e) {
            throw new RuntimeException("Error while executing maven: ", e);
        } finally {
            if (buildRequest.getOutputHandler() instanceof Closeable) {
                try {
                    ((Closeable) buildRequest.getOutputHandler()).close();
                } catch (IOException e) {
                    throw new RuntimeException("Can't close log file stream", e);
                }
            }
        }

        if (result.getExitCode() != 0) {
            if (buildRequest.getLogFile() != null) {
                throw new RuntimeException("Maven invocation failed with exit code " + result.getExitCode() + ", check "
                    + buildRequest.getLogFile() + " for more details");
            } else {
                throw new RuntimeException("Maven invocation failed with exit code " + result.getExitCode());
            }
        }
    }

    /**
     * Adds camel component dependencies to pom.xml file in a given dir.
     *
     * @param dir base directory
     * @param dependencies array of camel dependencies, e.g. "slack"
     */
    public static void addCamelComponentDependencies(Path dir, List<String> dependencies) {
        if (dependencies == null || dependencies.isEmpty()) {
            return;
        }
        File pom = dir.resolve("pom.xml").toFile();
        LOG.info("Adding {} as dependencies to {}", dependencies, pom);

        Model model = loadPom(pom);
        for (String dependency : dependencies) {
            Dependency dep = new Dependency();
            dep.setGroupId("org.apache.camel");
            dep.setArtifactId("camel-" + dependency);
            LOG.debug("Adding dependency {}:{}", dep.getGroupId(), dep.getArtifactId());
            model.getDependencies().add(dep);
        }

        writePom(pom, model);
    }

    /**
     * Loads the given pom file.
     *
     * @param pom pom file
     * @return model
     */
    public static Model loadPom(File pom) {
        Model model;
        try (InputStream is = new FileInputStream(pom)) {
            model = new MavenXpp3Reader().read(is);
        } catch (IOException | XmlPullParserException e) {
            throw new RuntimeException("Unable to load POM " + pom.getAbsolutePath(), e);
        }
        return model;
    }

    /**
     * Writes the maven model to a given pom file.
     *
     * @param pom pom file
     * @param model model
     */
    public static void writePom(File pom, Model model) {
        try (OutputStream os = new FileOutputStream(pom)) {
            new MavenXpp3Writer().write(os, model);
        } catch (IOException e) {
            throw new RuntimeException("Unable to write POM " + pom.getAbsolutePath(), e);
        }
    }

    /**
     * Creates a settings xml file with 1 profile with specified maven repository. This file is later used as maven global settings and will be
     * merged by user's settings by default by maven.
     */
    private static void createSettingsXmlFile() {
        if (TestConfiguration.mavenRepository() == null) {
            return;
        }

        // Create settings.xml file with the user defined repository
        Settings settings = new Settings();

        org.apache.maven.settings.Repository r = new org.apache.maven.settings.Repository();
        r.setId(TestConfiguration.mavenProfileId());
        r.setUrl(TestConfiguration.mavenRepository());
        RepositoryPolicy enabled = new RepositoryPolicy();
        enabled.setEnabled(true);
        r.setReleases(enabled);
        r.setSnapshots(enabled);

        org.apache.maven.settings.Profile p = new org.apache.maven.settings.Profile();
        p.setId("tnb-maven-repo");
        p.setRepositories(Collections.singletonList(r));
        p.setPluginRepositories(Collections.singletonList(r));

        settings.setProfiles(Collections.singletonList(p));
        File out = TestConfiguration.appLocation().resolve(TestConfiguration.mavenSettingsFileName()).toFile();
        try (OutputStream os = new FileOutputStream(out)) {
            new SettingsXpp3Writer().write(os, settings);
        } catch (IOException e) {
            throw new RuntimeException("Unable to write settings file ", e);
        }
    }
}
