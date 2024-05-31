package software.tnb.common.config;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;

public abstract class Configuration {
    private static final Logger LOG = LoggerFactory.getLogger(Configuration.class);

    private static final Config config;

    public static String getProperty(String name) {
        return getProperty(name, (String) null);
    }

    public static String getProperty(String name, String defaultValue) {
        return config.getOptionalValue(name, String.class).orElse(defaultValue);
    }

    public static String getProperty(String name, Supplier<String> defaultValue) {
        return config.getOptionalValue(name, String.class).orElseGet(defaultValue);
    }

    public static void setProperty(String name, String value) {
        System.setProperty(name, value);
    }

    public static boolean getBoolean(String name) {
        return getBoolean(name, false);
    }

    public static boolean getBoolean(String name, boolean defaultValue) {
        return config.getOptionalValue(name, Boolean.class).orElse(defaultValue);
    }

    public static int getInteger(String name) {
        return getInteger(name, 0);
    }

    public static int getInteger(String name, int defaultValue) {
        return config.getOptionalValue(name, Integer.class).orElse(defaultValue);
    }

    public static String[] getArray(String name) {
        return getArray(name, new String[0]);
    }

    public static String[] getArray(String name, String[] defaultValue) {
        return config.getOptionalValue(name, String[].class).orElse(defaultValue);
    }

    static {
        LOG.info("Loading properties");
        System.setProperty("org.apache.geronimo.config.configsource.SystemPropertyConfigSource.copy", "false");
        config = ConfigProvider.getConfig();
    }
}
