package software.tnb.common.utils;

import java.util.ArrayList;
import java.util.HashMap;
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

    /**
     * Merges two maps together and returns a new map.
     * @param m1 map 1
     * @param m2 map 2
     * @return new map, where m2 is merged into m1
     */
    public static Map<String, Object> merge(Map<String, Object> m1, Map<String, Object> m2) {
        Map<String, Object> result = new HashMap<>(m1);
        for (Map.Entry<String, Object> e : m2.entrySet()) {
            if (e.getValue() instanceof Map) {
                result.put(e.getKey(), merge(castToMap(result.computeIfAbsent(e.getKey(), v -> new HashMap<>())), castToMap(e.getValue())));
            } else if (e.getValue() instanceof List) {
                List<Object> list = new ArrayList<>(castToList(result.computeIfAbsent(e.getKey(), v -> new ArrayList<>())));
                list.addAll(castToList(e.getValue()));
                result.put(e.getKey(), list);
            } else {
                result.put(e.getKey(), e.getValue());
            }
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> castToMap(Object o) {
        return (Map<String, Object>) o;
    }

    @SuppressWarnings("unchecked")
    private static List<Object> castToList(Object o) {
        return (List<Object>) o;
    }
}
