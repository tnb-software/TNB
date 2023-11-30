package software.tnb.common.deployment;

import software.tnb.common.util.ReflectionUtil;

public interface RemoteService extends Deployable {
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
        return System.getProperty(String.format("tnb.%s.%s", ReflectionUtil.getSuperClassName(this.getClass()).toLowerCase(), prop), def);
    }

    default String host() {
        return propertyValue("host");
    }
}
