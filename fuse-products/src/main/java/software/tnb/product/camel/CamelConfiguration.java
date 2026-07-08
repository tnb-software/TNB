package software.tnb.product.camel;

import software.tnb.common.config.Configuration;
import software.tnb.common.config.TestConfiguration;

import java.nio.file.Path;

public class CamelConfiguration extends Configuration {
    public static final String CAMEL_VERSION = "camel.version";

    public static final String CLI_GROUP_ID = "camel.cli.group.id";
    public static final String CLI_ARTIFACT_ID = "camel.cli.artifact.id";
    public static final String CLI_VERSION = "camel.cli.version";
    public static final String CLI_DOWNLOAD_DIR = "camel.cli.download.dir";

    public static String camelVersion() {
        return getProperty(CAMEL_VERSION);
    }

    public static String cliGroupId() {
        return getProperty(CLI_GROUP_ID, "org.apache.camel");
    }

    public static String cliArtifactId() {
        return getProperty(CLI_ARTIFACT_ID, "camel-launcher");
    }

    public static String cliVersion() {
        return getProperty(CLI_VERSION);
    }

    public static Path cliDirectory() {
        return Path.of(getProperty(CLI_DOWNLOAD_DIR, TestConfiguration.appLocation().toString()));
    }
}
