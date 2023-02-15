package software.tnb.common.service.configuration;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ServiceConfiguration {
    private final Map<String, Object> configuration = new HashMap<>();

    protected void set(String key, Object value) {
        configuration.put(key, value);
    }

    protected <T> T get(String key, Class<T> clazz) {
        final Object value = configuration.get(key);
        return value == null ? null : clazz.cast(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ServiceConfiguration that = (ServiceConfiguration) o;
        return Objects.equals(configuration, that.configuration);
    }

    @Override
    public int hashCode() {
        return Objects.hash(configuration);
    }
}
