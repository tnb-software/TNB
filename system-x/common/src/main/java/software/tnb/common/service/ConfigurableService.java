package software.tnb.common.service;

import software.tnb.common.service.configuration.ServiceConfiguration;

import org.junit.platform.commons.util.ReflectionUtils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * For services that can have more configurations for deployment
 * <p>
 * WARNING: It is not recommended to use ConfigurableService with ReusableOpenshiftDeployable!
 * because ConfigurableService service can have more deployment configuration in the same test run,
 * e.g. TestClass1 -> ServiceA.config1 , ... TestClass4 -> ServiceA.config2 , ... TestClass6 -> ServiceA.config1
 * and in that case, the ServiceA.config1 will not be undeployed before ServiceA.config2 which would cause that deploy ServiceA.config2 would fail.
 * @param <C> Service configuration class which extends ServiceConfiguration
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
