package software.tnb.product.quarkus.vanilla.configuration;

import software.tnb.product.camel.CamelConfiguration;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Configuration for vanilla Quarkus (without Camel or CXF).
 */
public class QuarkusConfiguration extends CamelConfiguration {
    // All properties starting with "quarkus." are propagated to the maven builds (to allow customizing quarkus build "globally")
    // To include only properties passed as system properties, this list contains properties that are defined in this class
    // and are filtered out and not passed to the maven build (or are passed as different properties)
    // if you're adding a new property and it starts with "quarkus.", consider adding it to this set
    private static final Set<String> ignoredQuarkusProperties =
        Set.of("quarkus.version", "quarkus.native", "quarkus.platform.group-id", "quarkus.platform.artifact-id", "quarkus.platform.version");

    public static final String QUARKUS_VERSION = "quarkus.version";

    public static final String DEFAULT_QUARKUS_VERSION = "3.26.0";
    public static final String QUARKUS_NATIVE_BUILD = "quarkus.native";

    public static final String QUARKUS_PLATFORM_GROUP_ID = "quarkus.platform.group-id";
    public static final String QUARKUS_PLATFORM_ARTIFACT_ID = "quarkus.platform.artifact-id";

    public static final String QUARKUS_PLATFORM_VERSION = "quarkus.platform.version";

    public static final String DEFAULT_QUARKUS_PLATFORM_GROUP_ID = "io.quarkus";
    public static final String DEFAULT_QUARKUS_PLATFORM_ARTIFACT_ID = "quarkus-bom";

    public static String quarkusVersion() {
        return getProperty(QUARKUS_VERSION);
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

    /**
     * Collect all "quarkus." properties defined as system properties and filter out properties from QuarkusConfiguration class.
     *
     * @return map of quarkus properties
     */
    public static Map<String, String> fromSystemProperties() {
        return System.getProperties()
            .entrySet()
            .stream()
            .filter(e -> e.getKey().toString().startsWith("quarkus."))
            .filter(e -> !ignoredQuarkusProperties.contains(e.getKey().toString()))
            .collect(Collectors.toMap(e -> e.getKey().toString(), e -> e.getValue().toString()));
    }
}
