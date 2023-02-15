package software.tnb.common.service;

import software.tnb.common.service.configuration.ServiceConfiguration;

import org.junit.platform.commons.util.ReflectionUtils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * For services which have can have more configurations for deployment.
 * <p>
 * WARNING: It is not recommended to use ConfigurableService with ReusableOpenshiftDeployable!
 * When some service can have more deployment configuration in the one test run, it should not been long-running (ReusableOpenshiftDeployable)
 * because mostly it cannot be installed two same service in the one namespace.
 * @param <C> Service configuration class
 */
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
