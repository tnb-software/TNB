package software.tnb.product.config;

import software.tnb.common.config.Configuration;
import software.tnb.common.config.TestConfiguration;

import java.nio.file.Path;

public class BytemanConfiguration extends Configuration {
    public static final String BYTEMAN_GROUP_ID = "byteman.group.id";
    public static final String BYTEMAN_ARTIFACT_ID = "byteman.artifact.id";
    public static final String BYTEMAN_VERSION = "byteman.version";
    public static final String BYTEMAN_DOWNLOAD_DIR =  "byteman.download.dir";

    public static String version() {
        return getProperty(BYTEMAN_VERSION, "4.0.23");
    }

    public static String groupId() {
        return getProperty(BYTEMAN_GROUP_ID, "org.jboss.byteman");
    }

    public static String artifactId() {
        return getProperty(BYTEMAN_ARTIFACT_ID, "byteman");
    }

    public static Path dir() {
        return Path.of(getProperty(BYTEMAN_DOWNLOAD_DIR, TestConfiguration.appLocation().toString()));
    }
}
