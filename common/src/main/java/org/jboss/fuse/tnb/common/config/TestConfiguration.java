package org.jboss.fuse.tnb.common.config;

import org.jboss.fuse.tnb.common.product.ProductType;
import org.jboss.fuse.tnb.common.utils.IOUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;

public class TestConfiguration extends Configuration {
    public static final String CAMEL_VERSION = "camel.version";
    public static final String CAMEL_ARCHETYPE_VERSION = "camel.archetype.version";

    public static final String PRODUCT = "test.product";
    public static final String CREDENTIALS_FILE = "test.credentials.file";
    public static final String APP_GROUP_ID = "test.app.group.id";
    public static final String APP_LOCATION = "app.location";
    public static final String APP_TEMPLATE_NAME = "app.template.name";
    public static final String TEST_WAIT_TIME = "test.wait.time";
    public static final String TEST_WAIT_KILL_TIMEOUT = "test.wait.kill.timeout";
    public static final String TEST_SKIP_TEARDOWN = "test.skip.teardown";
    public static final String TEST_SKIP_TEARDOWN_OPENSHIFT_AMQSTREAMS = "test.skip.teardown.openshift.amqstreams";
    public static final String MAVEN_REPOSITORY = "test.maven.repository";
    public static final String MAVEN_SETTINGS = "test.maven.settings";
    public static final String MAVEN_SETTINGS_FILE_NAME = "test.maven.settings.file.name";
    public static final String MAVEN_REPOSITORY_ID = "test.maven.repository.id";
    public static final String REPORT_PORTAL = "test.report.portal.enabled";
    public static final String ODO_PATH = "test.odo.path";

    public static final String VARIABLE_PLACEHOLDER_START = "\\$\\{";
    public static final String VARIABLE_PLACEHOLDER_END = "\\}";

    public static final String USER = "tnb.user";

    public static ProductType product() {
        return Arrays.stream(ProductType.values()).filter(p -> p.getValue().equals(getProperty(PRODUCT))).findFirst()
            .orElseThrow(
                () -> new IllegalArgumentException(String.format("Unable to find enum for system property %s = %s", PRODUCT, getProperty(PRODUCT))));
    }

    public static String credentialsFile() {
        final String credentials = getProperty(CREDENTIALS_FILE);
        if (credentials == null) {
            throw new IllegalArgumentException("No credentials file specified!");
        }
        return credentials;
    }

    public static String appGroupId() {
        return getProperty(APP_GROUP_ID, "com.test");
    }

    public static Path appLocation() {
        return Paths.get(getProperty(APP_LOCATION, "target"));
    }

    public static String appTemplateName() {
        return getProperty(APP_TEMPLATE_NAME, "tnb-app");
    }

    public static Duration testWaitTime() {
        return Duration.of(getInteger(TEST_WAIT_TIME, 60), ChronoUnit.SECONDS);
    }

    public static Duration testWaitKillTimeout() {
        return Duration.of(getInteger(TEST_WAIT_KILL_TIMEOUT, 2), ChronoUnit.HOURS);
    }

    public static String mavenRepository() {
        return getProperty(MAVEN_REPOSITORY);
    }

    public static String mavenSettings() {
        return getProperty(MAVEN_SETTINGS);
    }

    public static String mavenSettingsFileName() {
        return getProperty(MAVEN_SETTINGS_FILE_NAME, "tnb-maven-settings.xml");
    }

    public static String mavenRepositoryId() {
        return getProperty(MAVEN_REPOSITORY_ID, "tnb-maven-repo");
    }

    public static boolean isMavenMirror() {
        return getProperty(MAVEN_REPOSITORY, "").contains("@mirrorOf=");
    }

    public static boolean skipTearDown() {
        return getBoolean(TEST_SKIP_TEARDOWN, false);
    }

    public static boolean skipTearDownOpenshiftAMQStreams() {
        return getBoolean(TEST_SKIP_TEARDOWN_OPENSHIFT_AMQSTREAMS, false);
    }

    public static boolean reportPortalEnabled() {
        return getBoolean(REPORT_PORTAL, false);
    }

    public static String user() {
        if (!"hudson".equals(System.getProperty("user.name"))) {
            return System.getProperty("user.name");
        }
        return System.getProperty(USER);
    }

    public static String odoPath() throws IOException {
        return getProperty(ODO_PATH, IOUtils.getExecInPath("odo"));
    }
}
