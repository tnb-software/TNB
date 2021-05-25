package org.jboss.fuse.tnb.common.config;

import org.jboss.fuse.tnb.common.product.ProductType;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;

public class TestConfiguration extends Configuration {
    public static final String CAMEL_VERSION = "camel.version";
    public static final String CAMEL_QUARKUS_VERSION = "camel.quarkus.version";
    // Version for com.redhat.quarkus:quarkus-product-bom needed for running productized camel-quarkus
    public static final String QUARKUS_PRODUCT_BOM_VERSION = "redhat.quarkus.version";

    public static final String PRODUCT = "test.product";
    public static final String CREDENTIALS_FILE = "test.credentials.file";
    public static final String APP_GROUP_ID = "test.app.group.id";
    public static final String APP_LOCATION = "app.location";
    public static final String APP_TEMPLATE_NAME = "app.template.name";
    public static final String TEST_WAIT_TIME = "test.wait.time";
    public static final String MAVEN_REPOSITORY = "test.maven.repository";
    public static final String MAVEN_SETTINGS_FILE_NAME = "test.maven.settings.file.name";
    public static final String MAVEN_REPOSITORY_ID = "test.maven.repository.id";

    public static final String QUARKUS_VERSION = "quarkus.version";
    public static final String QUARKUS_NATIVE_BUILD = "quarkus.native";

    public static final String VARIABLE_PLACEHOLDER_START = "\\$\\{";
    public static final String VARIABLE_PLACEHOLDER_END = "\\}";

    public static String camelVersion() {
        return getProperty(CAMEL_VERSION);
    }

    public static String camelQuarkusVersion() {
        return getProperty(CAMEL_QUARKUS_VERSION, "1.6.0");
    }

    public static String quarkusProductBomVersion() {
        return getProperty(QUARKUS_PRODUCT_BOM_VERSION, "1.11.6.Final-redhat-00001");
    }

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
        return getProperty(CREDENTIALS_FILE);
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

    public static String mavenRepository() {
        return getProperty(MAVEN_REPOSITORY);
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

    public static String quarkusVersion() {
        return getProperty(QUARKUS_VERSION, "1.11.5.Final");
    }

    public static boolean isQuarkusNative() {
        return getBoolean(QUARKUS_NATIVE_BUILD);
    }
}
