package org.jboss.fuse.tnb.common.config;

public class TestConfiguration extends Configuration {
    public static final String CAMEL_VERSION = "camel.version";

    public static final String PRODUCT = "test.product";
    public static final String CREDENTIALS_FILE = "test.credentials.file";
    public static final String APP_GROUP_ID = "test.app.group.id";

    public static String camelVersion() {
        return getProperty(CAMEL_VERSION);
    }

    public static String product() {
        return getProperty(PRODUCT);
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
}
