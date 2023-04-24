package software.tnb.common.service;

import software.tnb.common.account.Account;
import software.tnb.common.service.configuration.ServiceConfiguration;
import software.tnb.common.util.ReflectionUtil;
import software.tnb.common.validation.Validation;

import org.junit.platform.commons.util.ReflectionUtils;

/**
 * For services that can have more configurations for deployment
 * <p>
 * WARNING: It is not recommended to use ConfigurableService with ReusableOpenshiftDeployable!
 * because ConfigurableService service can have more deployment configuration in the same test run,
 * e.g. TestClass1 -> ServiceA.config1 , ... TestClass4 -> ServiceA.config2 , ... TestClass6 -> ServiceA.config1
 * and in that case, the ServiceA.config1 will not be undeployed before ServiceA.config2 which would cause that deploy ServiceA.config2 would fail.
 *
 * @param <C> Service configuration class which extends ServiceConfiguration
 */
public abstract class ConfigurableService<A extends Account, C, V extends Validation, S extends ServiceConfiguration> extends Service<A, C, V> {
    private final S configuration;

    public ConfigurableService() {
        Class<S> serviceConfigurationClass = (Class<S>) ReflectionUtil.getGenericTypesOf(ConfigurableService.class, this.getClass())[3];
        configuration = ReflectionUtils.newInstance(serviceConfigurationClass);
    }

    public S getConfiguration() {
        return configuration;
    }

    /**
     * The default configuration in case no customizations are made.
     */
    protected abstract void defaultConfiguration();
}
