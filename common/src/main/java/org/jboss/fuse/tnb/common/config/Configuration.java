package org.jboss.fuse.tnb.common.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public abstract class Configuration {
    private static final Logger LOG = LoggerFactory.getLogger(Configuration.class);
    private static final String TEST_PROPERTIES = "test.properties";

    protected static String getProperty(String name) {
        return System.getProperty(name);
    }

    protected static String getProperty(String name, String defaultValue) {
        return System.getProperty(name, defaultValue);
    }

    protected static void setProperty(String name, String value) {
        System.setProperty(name, value);
    }

    protected static boolean getBoolean(String name) {
        return Boolean.parseBoolean(getProperty(name));
    }

    protected static boolean getBoolean(String name, boolean defaultValue) {
        return Boolean.parseBoolean(getProperty(name, String.valueOf(defaultValue)));
    }

    protected static int getInteger(String name) {
        return Integer.parseInt(getProperty(name));
    }

    protected static int getInteger(String name, int defaultValue) {
        return Integer.parseInt(getProperty(name, String.valueOf(defaultValue)));
    }

    static {
        Path testProperties = Paths.get(System.getProperty(TEST_PROPERTIES, "test.properties"));
        if (testProperties.toFile().exists()) {
            LOG.info("Loading properties from {}", testProperties.toAbsolutePath());
            Properties props = new Properties();
            try (InputStream is = Files.newInputStream(testProperties)) {
                props.load(is);
            } catch (IOException e) {
                LOG.error("Unable to load properties from " + testProperties.toAbsolutePath(), e);
                throw new RuntimeException(e);
            }
            props.forEach((key, value) -> {
                // Do not override properties set on command line
                if (System.getProperty(key.toString()) == null) {
                    LOG.debug("Setting system property {} to {}", key, value.toString());
                    System.setProperty(key.toString(), value.toString());
                }
            });
        } else {
            LOG.debug("Test.properties file not found");
        }
    }
}
