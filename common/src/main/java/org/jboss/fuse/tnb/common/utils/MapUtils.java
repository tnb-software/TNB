package org.jboss.fuse.tnb.common.utils;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

import io.fabric8.kubernetes.api.model.EnvVar;

public final class MapUtils {
    private MapUtils() {
    }

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

    public static List<EnvVar> toEnvVars(Map<String, String> map) {
        return map.entrySet().stream().map(e -> new EnvVar(e.getKey(), e.getValue(), null)).collect(Collectors.toList());
    }
}
