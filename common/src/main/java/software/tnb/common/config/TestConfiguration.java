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
    public static final String VAULT_TOKEN = "test.credentials.vault.token";
    public static final String VAULT_SECRET_ID = "test.credentials.vault.secret.id";
    public static final String VAULT_ROLE_ID = "test.credentials.vault.role.id";
    public static final String VAULT_ADDRESS = "test.credentials.vault.address";
    public static final String VAULT_PATH_PATTERN = "test.credentials.vault.path.pattern";
    public static final String APP_GROUP_ID = "test.app.group.id";
    public static final String APP_VERSION = "test.app.version";
    public static final String APP_LOCATION = "test.app.location";
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
    public static final String MAVEN_TRANSFER_PROGRESS = "test.maven.transfer.progress";
    public static final String REPORT_PORTAL = "test.report.portal.enabled";
    public static final String ODO_PATH = "odo.path";
    public static final String STREAM_LOGS = "stream.logs";
    public static final String JIRA_ALLOWED_RESOLUTIONS = "jira.allowed.resolutions";
    public static final String JIRA_ACCESS_TOKEN = "jira.token";
    public static final String PARALLEL = "test.parallel";
    public static final String TEST_USE_GLOBAL_OPENSHIFT_KAFKA = "test.use.global.openshift.kafka";

    public static final String VARIABLE_PLACEHOLDER_START = "\\$\\{";
    public static final String VARIABLE_PLACEHOLDER_END = "\\}";

    public static final String USER = "tnb.user";

    public static final String APP_DEBUG = "tnb.app.debug";

    public static final String APP_DEBUG_PORT = "tnb.app.debug.port";

    public static final String KAMELETS_VERSION = "kamelets.version";

    public static ProductType product() {
        return Arrays.stream(ProductType.values()).filter(p -> p.getValue().equals(getProperty(PRODUCT))).findFirst()
            .orElseThrow(
                () -> new IllegalArgumentException(String.format("Unable to find enum for system property %s = %s", PRODUCT, getProperty(PRODUCT))));
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
        return getProperty(CREDENTIALS_FILE);
    }

    public static String credentials() {
        return getProperty(CREDENTIALS);
    }

    public static String appGroupId() {
        return getProperty(APP_GROUP_ID, "com.test");
    }

    public static String appVersion() {
        return getProperty(APP_VERSION, "1.0.0-SNAPSHOT");
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
        return getProperty(MAVEN_REPOSITORY);
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

    public static boolean mavenTransferProgress() {
        return getBoolean(MAVEN_TRANSFER_PROGRESS, false);
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
        return getProperty(USER);
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

    public static String jiraAccessToken() {
        return getProperty(JIRA_ACCESS_TOKEN, "");
    }

    public static boolean parallel() {
        return getBoolean(PARALLEL, false);
    }

    public static boolean useGlobalOpenshiftKafka() {
        return getBoolean(TEST_USE_GLOBAL_OPENSHIFT_KAFKA, false);
    }

    public static boolean appDebug() {
        return getBoolean(APP_DEBUG, false);
    }

    public static Integer appDebugPort() {
        return getInteger(APP_DEBUG_PORT, 5005);
    }

    public static String kameletsVersion() {
        return getProperty(KAMELETS_VERSION);
    }
}
