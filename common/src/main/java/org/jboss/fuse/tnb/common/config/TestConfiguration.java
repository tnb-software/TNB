package org.jboss.fuse.tnb.common.config;

public class TestConfiguration extends Configuration {
    public static final String PRODUCT = "test.product";

    public static final String CREDENTIALS_FILE = "test.credentials.file";

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
}
