package software.tnb.product.cq.configuration;

import software.tnb.product.camel.CamelConfiguration;

public class QuarkusConfiguration extends CamelConfiguration {
    public static final String CAMEL_QUARKUS_VERSION = "camel-quarkus.version";

    public static final String DEFAULT_CAMEL_QUARKUS_VERSION = "2.13.3";

    public static final String QUARKUS_VERSION = "quarkus.version";

    public static final String DEFAULT_QUARKUS_VERSION = "2.13.7.Final";
    public static final String QUARKUS_NATIVE_BUILD = "quarkus.native";

    public static final String QUARKUS_PLATFORM_GROUP_ID = "quarkus.platform.group-id";
    public static final String QUARKUS_PLATFORM_ARTIFACT_ID = "quarkus.platform.artifact-id";

    public static final String QUARKUS_PLATFORM_VERSION = "quarkus.platform.version";

    public static final String DEFAULT_QUARKUS_PLATFORM_GROUP_ID = "io.quarkus";
    public static final String DEFAULT_QUARKUS_PLATFORM_ARTIFACT_ID = "quarkus-bom";

    public static final String CAMEL_QUARKUS_PLATFORM_GROUP_ID = "camel-quarkus.platform.group-id";
    public static final String CAMEL_QUARKUS_PLATFORM_ARTIFACT_ID = "camel-quarkus.platform.artifact-id";

    public static final String CAMEL_QUARKUS_PLATFORM_VERSION = "camel-quarkus.platform.version";

    public static final String DEFAULT_CAMEL_QUARKUS_PLATFORM_GROUP_ID = "org.apache.camel.quarkus";

    public static final String DEFAULT_CAMEL_QUARKUS_PLATFORM_ARTIFACT_ID = "camel-quarkus-bom";

    public static String quarkusVersion() {
        return getProperty(QUARKUS_VERSION);
    }

    public static String camelQuarkusVersion() {
        return getProperty(CAMEL_QUARKUS_VERSION);
    }

    public static boolean isQuarkusNative() {
        return getBoolean(QUARKUS_NATIVE_BUILD);
    }

    public static String quarkusPlatformGroupId() {
        return getProperty(QUARKUS_PLATFORM_GROUP_ID);
    }

    public static String quarkusPlatformArtifactId() {
        return getProperty(QUARKUS_PLATFORM_ARTIFACT_ID);
    }

    public static String quarkusPlatformVersion() {
        return getProperty(QUARKUS_PLATFORM_VERSION);
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

}
