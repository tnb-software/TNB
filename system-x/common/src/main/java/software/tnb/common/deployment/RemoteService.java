package software.tnb.common.deployment;

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
        final Class<?> superclass = this.getClass().getSuperclass();
        if (Object.class.equals(superclass)) {
            throw new IllegalStateException("Current class " + this.getClass().getSimpleName() + " does not extend any other class"
                + " and default method from RemoteDeployable was called, either override this method or check what's wrong as this shouldn't happen");
        }

        return System.getProperty(String.format("tnb.%s.%s", superclass.getSimpleName().toLowerCase(), prop), def);
    }

    default String host() {
        return propertyValue("host");
    }
}
