package software.tnb.common.service.configuration;

import java.util.HashMap;
import java.util.Map;

public class ServiceConfiguration {
    private final Map<String, Object> configuration = new HashMap<>();

    protected void set(String key, Object value) {
        configuration.put(key, value);
    }

    protected <T> T get(String key, Class<T> clazz) {
        final Object value = configuration.get(key);
        return value == null ? null : clazz.cast(value);
    }
}
