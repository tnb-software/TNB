package software.tnb.product.quarkus.cxf.configuration;

import software.tnb.product.quarkus.vanilla.configuration.QuarkusConfiguration;

/**
 * Configuration for CXF Quarkus.
 */
public class CxfQuarkusConfiguration extends QuarkusConfiguration {
    public static final String CXF_QUARKUS_PLATFORM_GROUP_ID = "cxf-quarkus.platform.group-id";

    public static final String CXF_QUARKUS_PLATFORM_ARTIFACT_ID = "cxf-quarkus.platform.artifact-id";

    public static final String CXF_QUARKUS_PLATFORM_VERSION = "cxf-quarkus.platform.version";

    public static String cxfQuarkusPlatformGroupId() {
        return getProperty(CXF_QUARKUS_PLATFORM_GROUP_ID);
    }

    public static String cxfQuarkusPlatformArtifactId() {
        return getProperty(CXF_QUARKUS_PLATFORM_ARTIFACT_ID);
    }

    public static String cxfQuarkusPlatformVersion() {
        return getProperty(CXF_QUARKUS_PLATFORM_VERSION);
    }
}
