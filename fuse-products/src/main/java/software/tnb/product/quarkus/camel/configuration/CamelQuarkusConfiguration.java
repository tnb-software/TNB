package software.tnb.product.quarkus.camel.configuration;

import software.tnb.product.quarkus.vanilla.configuration.QuarkusConfiguration;

/**
 * Configuration for Camel Quarkus.
 */
public class CamelQuarkusConfiguration extends QuarkusConfiguration {
    public static final String CAMEL_QUARKUS_VERSION = "camel-quarkus.version";

    public static final String DEFAULT_CAMEL_QUARKUS_VERSION = "3.26.0";

    public static final String CAMEL_QUARKUS_PLATFORM_GROUP_ID = "camel-quarkus.platform.group-id";

    public static final String CAMEL_QUARKUS_PLATFORM_ARTIFACT_ID = "camel-quarkus.platform.artifact-id";

    public static final String CAMEL_QUARKUS_PLATFORM_VERSION = "camel-quarkus.platform.version";

    public static final String DEFAULT_CAMEL_QUARKUS_PLATFORM_GROUP_ID = "org.apache.camel.quarkus";

    public static final String DEFAULT_CAMEL_QUARKUS_PLATFORM_ARTIFACT_ID = "camel-quarkus-bom";

    public static final String CAMEL_QUARKUS_EXAMPLES_REPO = "camel.quarkus.examples.repo";

    public static final String CAMEL_QUARKUS_EXAMPLES_BRANCH = "camel.quarkus.examples.branch";

    public static String camelQuarkusVersion() {
        return getProperty(CAMEL_QUARKUS_VERSION);
    }

    public static String camelQuarkusPlatformGroupId() {
        return getProperty(CAMEL_QUARKUS_PLATFORM_GROUP_ID);
    }

    public static String camelQuarkusPlatformArtifactId() {
        return getProperty(CAMEL_QUARKUS_PLATFORM_ARTIFACT_ID);
    }

    public static String camelQuarkusPlatformVersion() {
        return getProperty(CAMEL_QUARKUS_PLATFORM_VERSION);
    }

    public static String getCamelQuarkusExamplesRepo() {
        return getProperty(CAMEL_QUARKUS_EXAMPLES_REPO, "https://github.com/apache/camel-quarkus-examples");
    }

    public static String getCamelQuarkusExamplesBranch() {
        return getProperty(CAMEL_QUARKUS_EXAMPLES_BRANCH, "main");
    }
}
