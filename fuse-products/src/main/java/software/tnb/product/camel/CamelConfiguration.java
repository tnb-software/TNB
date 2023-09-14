package software.tnb.product.camel;

import software.tnb.common.config.Configuration;

public class CamelConfiguration extends Configuration {
    public static final String CAMEL_VERSION = "camel.version";

    public static String camelVersion() {
        return getProperty(CAMEL_VERSION);
    }
}
