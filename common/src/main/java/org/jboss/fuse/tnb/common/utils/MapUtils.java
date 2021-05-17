package org.jboss.fuse.tnb.common.utils;

import java.util.LinkedHashMap;
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
        return propertiesToString(properties, "");
    }

    public static String propertiesToString(Properties properties, String prefix) {
        return properties.entrySet().stream().map(entry -> prefix + entry.getKey() + "=" + entry.getValue())
            .collect(Collectors.joining(System.lineSeparator()));
    }

    public static Map<String, Object> propertiesToMap(Properties properties, String prefix) {
        Map<String, Object> map = new LinkedHashMap<>();
        properties.entrySet().stream()
            .forEach(entry -> map.put(prefix + StringUtils.replaceUnderscoreWithCamelCase(entry.getKey().toString()), entry.getValue()));
        return map;
    }
}
