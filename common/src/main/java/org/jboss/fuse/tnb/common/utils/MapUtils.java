package org.jboss.fuse.tnb.common.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public final class MapUtils {
    private MapUtils() {
    }

    public static Map<String, String> map(String... values) {
        final Map<String, String> result = new HashMap<>();
        for (int i = 0; i + 1 < values.length; i += 2) {
            result.put(values[i], values[i + 1]);
        }
        return result;
    }

    public static Properties toProperties(Map<String, String> map) {
        Properties properties = new Properties();
        properties.putAll(map);
        return properties;
    }
}
