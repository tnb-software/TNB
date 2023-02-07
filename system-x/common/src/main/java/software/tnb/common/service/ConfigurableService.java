package software.tnb.common.service;

import software.tnb.common.service.configuration.ServiceConfiguration;

import org.junit.platform.commons.util.ReflectionUtils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class ConfigurableService<C extends ServiceConfiguration> implements Service {
    private final C configuration;

    public ConfigurableService() {
        Type type = this.getClass().getGenericSuperclass();
        while (true) {
            if (type instanceof ParameterizedType) {
                if (((ParameterizedType) type).getRawType().equals(ConfigurableService.class)) {
                    break;
                } else {
                    throw new RuntimeException("Expected to find " + ConfigurableService.class.getSimpleName() + ", but found "
                        + ((ParameterizedType) type).getRawType().getTypeName());
                }
            }
            type = ((Class) type).getGenericSuperclass();
        }
        configuration = ReflectionUtils.newInstance((Class<C>) ((ParameterizedType) type).getActualTypeArguments()[0]);
    }

    public C getConfiguration() {
        return configuration;
    }
}
