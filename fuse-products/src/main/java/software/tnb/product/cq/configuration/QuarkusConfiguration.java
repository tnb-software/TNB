package software.tnb.product.cq.configuration;

import software.tnb.product.camel.CamelConfiguration;

public class QuarkusConfiguration extends CamelConfiguration {
    public static final String CAMEL_QUARKUS_VERSION = "camel.quarkus.version";
    public static final String QUARKUS_VERSION = "quarkus.version";
    public static final String QUARKUS_NATIVE_BUILD = "quarkus.native";

    public static final String QUARKUS_PLATFORM_GROUP_ID = "quarkus.platform.group.id";
    public static final String QUARKUS_PLATFORM_ARTIFACT_ID = "quarkus.platform.artifact.id";

    public static final String CAMEL_PLATFORM_GROUP_ID = "camel.quarkus.platform.group.id";
    public static final String CAMEL_PLATFORM_ARTIFACT_ID = "camel.quarkus.platform.artifact.id";

    public static final String MAVEN_PLUGIN_GROUP_ID = "quarkus.maven.plugin.group.id";
    public static final String MAVEN_PLUGIN_ARTIFACT_ID = "quarkus.maven.plugin.artifact.id";
    public static final String MAVEN_PLUGIN_VERSION = "quarkus.maven.plugin.version";

    public static String quarkusVersion() {
        return getProperty(QUARKUS_VERSION, "2.13.7.Final");
    }

    public static String camelQuarkusVersion() {
        return getProperty(CAMEL_QUARKUS_VERSION, "2.13.3");
    }

    public static boolean isQuarkusNative() {
        return getBoolean(QUARKUS_NATIVE_BUILD);
    }

    public static String quarkusPlatformGroupId() {
        return getProperty(QUARKUS_PLATFORM_GROUP_ID, "io.quarkus");
    }

    public static String quarkusPlatformArtifactId() {
        return getProperty(QUARKUS_PLATFORM_ARTIFACT_ID, "quarkus-bom");
    }

    public static String camelPlatformGroupId() {
        return getProperty(CAMEL_PLATFORM_GROUP_ID, "org.apache.camel.quarkus");
    }

    public static String camelPlatformArtifactId() {
        return getProperty(CAMEL_PLATFORM_ARTIFACT_ID, "camel-quarkus-bom");
    }

    public static String mavenPluginGroupId() {
        return getProperty(MAVEN_PLUGIN_GROUP_ID, "io.quarkus");
    }

    public static String mavenPluginArtifactId() {
        return getProperty(MAVEN_PLUGIN_ARTIFACT_ID, "quarkus-maven-plugin");
    }

    public static String mavenPluginVersion() {
        return getProperty(MAVEN_PLUGIN_VERSION, quarkusVersion());
    }
}
