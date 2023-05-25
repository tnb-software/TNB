package software.tnb.common.config;

import software.tnb.common.product.ProductType;
import software.tnb.common.utils.IOUtils;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class TestConfiguration extends Configuration {

    public static final String PRODUCT = "test.product";
    public static final String CREDENTIALS_FILE = "test.credentials.file";
    public static final String CREDENTIALS = "test.credentials";
    public static final String USE_VAULT = "test.credentials.use.vault";
    public static final String VAULT_TOKEN = "test.credentials.vault.token";
    private static final String VAULT_SECRET_ID = "test.credentials.vault.secret.id";
    private static final String VAULT_ROLE_ID = "test.credentials.vault.role.id";
    public static final String VAULT_ADDRESS = "test.credentials.vault.address";
    public static final String VAULT_PATH_PATTERN = "test.credentials.vault.path.pattern";
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
    public static final String MAVEN_EXTRA_ARGS = "test.maven.extra.args";
    public static final String REPORT_PORTAL = "test.report.portal.enabled";
    public static final String ODO_PATH = "odo.path";
    public static final String STREAM_LOGS = "stream.logs";
    public static final String JIRA_ALLOWED_RESOLUTIONS = "jira.allowed.resolutions";
    public static final String PARALLEL = "test.parallel";

    public static final String VARIABLE_PLACEHOLDER_START = "\\$\\{";
    public static final String VARIABLE_PLACEHOLDER_END = "\\}";

    public static final String USER = "tnb.user";

    public static ProductType product() {
        return Arrays.stream(ProductType.values()).filter(p -> p.getValue().equals(getProperty(PRODUCT))).findFirst()
            .orElseThrow(
                () -> new IllegalArgumentException(String.format("Unable to find enum for system property %s = %s", PRODUCT, getProperty(PRODUCT))));
    }

    public static boolean useVault() {
        return getBoolean(USE_VAULT, false);
    }

    public static String vaultToken() {
        return getProperty(VAULT_TOKEN);
    }

    public static String vaultRoleId() {
        return getProperty(VAULT_ROLE_ID);
    }

    public static String vaultSecretId() {
        return getProperty(VAULT_SECRET_ID);
    }

    public static String vaultPathPattern() {
        return getProperty(VAULT_PATH_PATTERN, "/");
    }

    public static String vaultAddress() {
        return getProperty(VAULT_ADDRESS, "https://vault.devshift.net");
    }

    public static String credentialsFile() {
        final String credentials = getProperty(CREDENTIALS_FILE);
        if (credentials == null) {
            throw new IllegalArgumentException("No credentials file specified!");
        }
        return credentials;
    }

    public static String credentials() {
        return getProperty(CREDENTIALS);
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
        return Duration.of(getInteger(TEST_WAIT_KILL_TIMEOUT, 120), ChronoUnit.MINUTES);
    }

    public static String mavenRepository() {
        return getProperty(MAVEN_REPOSITORY, "https://repository.jboss.org/nexus/content/groups/public/");
    }

    public static String mavenSettings() {
        return getProperty(MAVEN_SETTINGS);
    }

    public static String mavenExtraArgs() {
        return getProperty(MAVEN_EXTRA_ARGS, "");
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

    public static String odoPath() {
        return Optional.of(getProperty(ODO_PATH, () -> IOUtils.getExecInPath("odo")))
            .orElseThrow(() -> new RuntimeException("Unable to find odo command: please provide '" + ODO_PATH
                + "' property or add odo binary in system path"));
    }

    public static boolean streamLogs() {
        return getBoolean(STREAM_LOGS, false);
    }

    public static Set<String> jiraAllowedResolutions() {
        return Arrays.stream(
            getProperty(JIRA_ALLOWED_RESOLUTIONS, "Resolved, Closed, Done, Validation Backlog, In Validation").split(",")
        ).map(String::trim).map(String::toLowerCase).collect(Collectors.toSet());
    }

    public static boolean parallel() {
        return getBoolean(PARALLEL, false);
    }
}
