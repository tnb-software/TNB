package software.tnb.product.util.maven;

import software.tnb.common.config.TestConfiguration;
import software.tnb.common.exception.FailureCauseException;
import software.tnb.common.product.ProductType;
import software.tnb.common.utils.IOUtils;
import software.tnb.product.log.stream.FileLogStream;
import software.tnb.product.log.stream.LogStream;
import software.tnb.product.util.maven.handler.MavenFileOutputHandler;

import org.apache.commons.lang3.StringUtils;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Exclusion;
import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.PluginExecution;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.apache.maven.settings.Mirror;
import org.apache.maven.settings.RepositoryPolicy;
import org.apache.maven.settings.Settings;
import org.apache.maven.settings.io.xpp3.SettingsXpp3Writer;
import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.DefaultInvoker;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.InvocationResult;
import org.apache.maven.shared.invoker.Invoker;
import org.apache.maven.shared.invoker.MavenInvocationException;
import org.codehaus.plexus.util.xml.Xpp3Dom;
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
import java.util.stream.Collectors;

/**
 * Class for invoking maven.
 */
public class Maven {
    private static final Logger LOG = LoggerFactory.getLogger(Maven.class);
    protected static Invoker invoker;
    protected static boolean initialized = false;

    protected Maven() {
    }

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
        if (initialized) {
            return;
        }

        LOG.info("Setting up maven");

        if (TestConfiguration.mavenSettings() == null) {
            if (TestConfiguration.mavenRepository() != null) {
                createSettingsXmlFile();
            } else {
                LOG.info("No maven settings or repository specified, using default maven system settings");
            }
        }

        if (System.getProperty("maven.home") != null) {
            // Do nothing as the maven.home is what we need
            return;
        }

        LOG.debug("M2_HOME env property is {}", System.getenv("M2_HOME"));
        if (System.getenv("M2_HOME") != null) {
            System.setProperty("maven.home", System.getenv("M2_HOME"));
            return;
        }

        LOG.debug("M2_HOME system property is {}", System.getProperty("M2_HOME"));
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

        initialized = true;
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
        File mavenSettings = null;

        InvocationRequest request = newRequest()
            .setBaseDirectory(dir)
            .setBatchMode(true)
            .addArgs(goals)
            .setProperties(properties)
            .setNoTransferProgress(TestConfiguration.mavenTransferProgress())
            .setOutputHandler(buildRequest.getOutputHandler())
            .setErrorHandler(buildRequest.getOutputHandler());

        //add extra args
        final String mavenExtraArgs = TestConfiguration.mavenExtraArgs();
        Arrays.stream(mavenExtraArgs.split(" ")).filter((arg) -> !arg.isBlank()).forEach(request::addArg);

        // If you didn't specify custom maven settings, use settings.xml file created in createSettingsXmlFile method as the global settings
        if (TestConfiguration.mavenSettings() == null) {
            if (TestConfiguration.mavenRepository() != null) {
                mavenSettings = TestConfiguration.appLocation().resolve(TestConfiguration.mavenSettingsFileName()).toFile();
                if (!TestConfiguration.isMavenMirror()) {
                    LOG.debug("Adding {} profile to build profiles", TestConfiguration.mavenRepositoryId());
                    profiles.add(TestConfiguration.mavenRepositoryId());
                }
            }
        } else {
            // For custom settings, we want to override also the user settings, so that it is the only file used
            LOG.debug("Using maven settings file {}", TestConfiguration.mavenSettings());
            mavenSettings = new File(TestConfiguration.mavenSettings());
            request.setUserSettingsFile(mavenSettings);
        }
        request.setProfiles(profiles);

        if (mavenSettings != null) {
            request.setGlobalSettingsFile(mavenSettings);
        }

        StringBuilder propertiesLog = new StringBuilder("Invoking maven with:" + "\n"
            + "  Base dir: " + dir.getAbsolutePath() + "\n"
            + "  Goals: " + goals.toString() + "\n"
            + "  Profiles: " + profiles + "\n"
            + (request.getUserSettingsFile() == null ? "" : "  User settings: " + request.getUserSettingsFile().getAbsolutePath() + "\n")
            + (request.getGlobalSettingsFile() == null ? "" : "  Global settings: " + request.getGlobalSettingsFile().getAbsolutePath() + "\n")
        );

        if (StringUtils.isNotBlank(mavenExtraArgs)) {
            propertiesLog.append("  Extra args: ")
                .append(mavenExtraArgs)
                .append("\n");
        }

        if (!properties.isEmpty()) {
            propertiesLog.append("  Properties:").append("\n");
            properties.forEach((key, value) -> propertiesLog.append("    ").append(key).append(": ").append(value).append("\n"));
        }
        LOG.debug(propertiesLog.substring(0, propertiesLog.length() - 1));

        Path file = ((MavenFileOutputHandler) buildRequest.getOutputHandler()).getFile();
        String marker = buildRequest.getLogMarker() != null ? buildRequest.getLogMarker() : "[MARKER-MISSING]";

        LogStream logStream = new FileLogStream(file, marker);
        try {
            result = invoker.execute(request);
        } catch (MavenInvocationException e) {
            throw new RuntimeException("Error while executing maven: ", e);
        } finally {
            logStream.stop();
            if (buildRequest.getOutputHandler() instanceof Closeable) {
                try {
                    ((Closeable) buildRequest.getOutputHandler()).close();
                } catch (IOException e) {
                    LOG.warn("Can't close log file stream", e);
                }
            }
        }

        // Don't throw exception in case the exit code is less than zero (happens when killing the process through executorservice)
        if (result.getExitCode() > 0) {
            String exceptionMessage = "Maven invocation failed with exit code " + result.getExitCode();
            if (buildRequest.getLogFile() != null) {
                if (TestConfiguration.streamLogs()) {
                    throw new RuntimeException(exceptionMessage + ", check " + buildRequest.getLogFile().toAbsolutePath() + " for more details");
                } else {
                    throw new RuntimeException(exceptionMessage, new FailureCauseException(buildRequest.getLogFile()));
                }
            } else {
                throw new RuntimeException(exceptionMessage);
            }
        }
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
    public static String createSettingsXmlFile() {
        // Create settings.xml file with the user defined repository
        Settings settings = new Settings();

        if (TestConfiguration.isMavenMirror()) {
            // if the maven repository is a mirror of something, define it as a mirror
            Mirror m = new Mirror();
            m.setId(TestConfiguration.mavenRepositoryId());
            m.setUrl(StringUtils.substringBefore(TestConfiguration.mavenRepository(), "@"));
            m.setMirrorOf(StringUtils.substringAfter(TestConfiguration.mavenRepository(), "mirrorOf="));
            settings.addMirror(m);
        } else {
            // otherwise create a new profile with that repository
            org.apache.maven.settings.Repository r = new org.apache.maven.settings.Repository();
            r.setId(TestConfiguration.mavenRepositoryId());
            r.setUrl(TestConfiguration.mavenRepository());
            RepositoryPolicy enabled = new RepositoryPolicy();
            enabled.setEnabled(true);
            r.setReleases(enabled);
            r.setSnapshots(enabled);

            org.apache.maven.settings.Profile p = new org.apache.maven.settings.Profile();
            p.setId(TestConfiguration.mavenRepositoryId());
            p.setRepositories(Collections.singletonList(r));
            p.setPluginRepositories(Collections.singletonList(r));

            settings.setProfiles(Collections.singletonList(p));
        }
        File out = TestConfiguration.appLocation().resolve(TestConfiguration.mavenSettingsFileName()).toFile();
        try (OutputStream os = new FileOutputStream(out)) {
            new SettingsXpp3Writer().write(os, settings);
        } catch (IOException e) {
            throw new RuntimeException("Unable to write settings file ", e);
        }

        return IOUtils.readFile(out.toPath());
    }

    public static Dependency createDependency(String dep, String... exclusions) {
        Dependency dependency = createDependency(dep);

        if (exclusions != null) {
            dependency.setExclusions(Arrays.stream(exclusions).map(e -> {
                Exclusion exclusion = new Exclusion();
                Dependency depExclusion = createDependency(e);

                exclusion.setArtifactId(depExclusion.getArtifactId());
                exclusion.setGroupId(depExclusion.getGroupId());

                return exclusion;
            }).collect(Collectors.toList()));
        }

        return dependency;
    }

    /**
     * Returns the dependency object from given string. If the string contains ":" it is assumed that it is a GA[V] string.
     *
     * @param s dependency string
     * @return dependency object
     */
    public static Dependency createDependency(String s) {
        Dependency dependency = new Dependency();
        if (s.contains(":")) {
            String[] parts = s.split(":");
            int index = 0;
            if ("github".equals(parts[0])) {
                index++;
                dependency.setType("github");
            }
            dependency.setGroupId(parts[index]);
            dependency.setArtifactId(parts[index + 1]);
            if (parts.length > index + 2) {
                dependency.setVersion(parts[index + 2]);
            }
        } else {
            if (TestConfiguration.product() == ProductType.CAMEL_SPRINGBOOT) {
                dependency.setGroupId("org.apache.camel.springboot");
                dependency.setArtifactId("camel-" + s + "-starter");
            } else {
                dependency.setGroupId("org.apache.camel.quarkus");
                dependency.setArtifactId("camel-quarkus-" + s);
            }
        }
        String log = "Created dependency: " + dependency.getGroupId() + ":" + dependency.getArtifactId();
        if (dependency.getVersion() != null) {
            log += ":" + dependency.getVersion();
        }
        LOG.info(log);
        return dependency;
    }

    /**
     * Set plugin identifier from given string. It is assumed that it is a GA[V] string.
     *
     * @param identifier alias artifactId:groupId:version plugin string
     */
    public static Plugin createPlugin(String identifier) {
        Plugin plugin = new Plugin();
        if (identifier.contains(":")) {
            String[] parts = identifier.split(":");
            plugin.setGroupId(parts[0]);
            plugin.setArtifactId(parts[1]);
            if (parts.length > 2) {
                plugin.setVersion(parts[2]);
            }
        }

        return plugin;
    }

    /**
     * Set plugin identifier from given string. It is assumed that it is a GA[V] string.
     * Also set plugin execution part
     *
     * @param identifier alias artifactId:groupId:version string
     * @param executions plugin executions part
     */
    public static Plugin createPlugin(String identifier, List<PluginExecution> executions) {
        Plugin plugin = createPlugin(identifier);
        plugin.setExecutions(executions);

        return plugin;
    }

    /**
     * Set plugin identifier from given string. It is assumed that it is a GA[V] string.
     * Also set plugin execution part
     *
     * @param identifier alias artifactId:groupId:version string
     * @param executions plugin executions part
     * @param configuration sets plugin configuration with Xpp3Dom class
     * @param extensions whether to load Maven extensions (such as packaging and type handlers) from this plugin
     */
    public static Plugin createPlugin(String identifier, List<PluginExecution> executions, Object configuration, boolean extensions) {
        Plugin plugin = createPlugin(identifier, executions);
        plugin.setConfiguration(configuration);
        plugin.setExtensions(extensions);

        return plugin;
    }

    /**
     * Converts the map to the hierarchy of Xpp3Dom objects.
     *
     * @param name element name
     * @param map configuration
     * @return Xpp3Dom object
     */
    public static Xpp3Dom mapToXpp3Dom(String name, Map<String, Object> map) {
        Xpp3Dom xpp3Dom = new Xpp3Dom(name);

        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            Xpp3Dom child = new Xpp3Dom(key);

            if (value instanceof Map) {
                child = mapToXpp3Dom(key, (Map<String, Object>) value);
            } else {
                child.setValue(String.valueOf(value));
            }

            xpp3Dom.addChild(child);
        }

        return xpp3Dom;
    }
}
