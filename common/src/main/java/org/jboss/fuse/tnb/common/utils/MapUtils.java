package org.jboss.fuse.tnb.common.utils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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

    /**
     * Converts properties object into a map.
     *
     * @param properties of service, obtained from credentials.yaml
     * @param prefix e.g. "camel.kamelet.aws-s3-source."
     * @return map with keys in camelCase
     */
    public static Map<String, Object> propertiesToMap(Properties properties, String prefix) {
        Map<String, Object> map = new LinkedHashMap<>();
        if (properties != null) {
            properties.entrySet().stream()
                .forEach(entry -> map
                    .put(Optional.ofNullable(prefix).orElse("") + StringUtils.replaceUnderscoreWithCamelCase(entry.getKey().toString()),
                        entry.getValue()));
        }
        return map;
    }

    public static List<EnvVar> mapToEnvVars(Map<String, String> map) {
        return map.entrySet().stream().map(e -> new EnvVar(e.getKey(), e.getValue(), null)).collect(Collectors.toList());
    }
}
