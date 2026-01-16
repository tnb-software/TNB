package software.tnb.common.deployment;

import software.tnb.common.config.TestConfiguration;
import software.tnb.common.util.ReflectionUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface RemoteService extends Deployable {
    Logger LOG = LoggerFactory.getLogger(RemoteService.class);

    @Override
    default boolean enabled() {
        return propertyValue("host") != null;
    }

    @Override
    default int priority() {
        return 2;
    }

    @Override
    default void deploy() {
    }

    @Override
    default void undeploy() {
    }

    default String propertyValue(String prop) {
        return propertyValue(prop, null);
    }

    default String propertyValue(String prop, String def) {
        return TestConfiguration.getProperty(String.format("tnb.%s.%s", ReflectionUtil.getSuperClassName(this.getClass()).toLowerCase(), prop), def);
    }

    default String host() {
        return propertyValue("host");
    }

    default String getLogs() {
        LOG.warn("getLogs method not supported in RemoteService, returning an empty string");
        return "";
    }
}
