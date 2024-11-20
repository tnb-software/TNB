package software.tnb.product.config;

import software.tnb.common.config.Configuration;

public class BytemanConfiguration extends Configuration {
    public static final String BYTEMAN_VERSION = "byteman.version";
    public static final String BYTEMAN_MAVEN_COORDINATES = "byteman.maven.coordinates";

    public static String version() {
        return getProperty(BYTEMAN_VERSION, "4.0.23");
    }

    public static String mavenCoordinates() {
        return getProperty(BYTEMAN_MAVEN_COORDINATES, "org.jboss.byteman:byteman");
    }
}
