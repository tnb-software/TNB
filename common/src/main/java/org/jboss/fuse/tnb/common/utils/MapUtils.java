package org.jboss.fuse.tnb.common.utils;

import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

public final class MapUtils {
    /**
     * Creates a Properties file from a given map.
     *
     * @param map map
     * @return properties instance
     */
    public static Properties toProperties(Map<String, String> map) {
        Properties properties = new Properties();
        properties.putAll(map);
        return properties;
    }

    /**
     * Dumps the properties as new-line separated string of key=value.
     *
     * @param properties properties
     * @return string
     */
    public static String propertiesToString(Properties properties) {
        return properties.entrySet().stream().map(entry -> entry.getKey() + "=" + entry.getValue())
            .collect(Collectors.joining("\n"));
    }
}
