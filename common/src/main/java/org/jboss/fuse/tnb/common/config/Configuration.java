package org.jboss.fuse.tnb.common.config;

public abstract class Configuration {
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
}
