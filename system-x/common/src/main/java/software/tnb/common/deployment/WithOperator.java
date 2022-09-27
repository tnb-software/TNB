package software.tnb.common.deployment;

import java.util.Optional;
import java.util.function.Supplier;

public interface WithOperator {
    /**
     * This method should always be called from OpenshiftX, so it should always extend some X class and the property to use
     * to override the image would resolve to x.operator.[property]
     *
     * @return catalog source to use to install operator
     */
    default String operatorCatalog() {
        return getValue(this.getClass(), ".operator.catalog", () -> defaultOperatorCatalog());
    }

    default String operatorChannel() {
        return getValue(this.getClass(), ".operator.channel", () -> defaultOperatorChannel());
    }

    /**
     * The default CatalogSource CRD to use.
     *
     * @return default CatalogSource to use
     */
    String defaultOperatorCatalog();

    /**
     * The default subscription channel.
     *
     * @return default channel to use
     */
    String defaultOperatorChannel();

    private static String getValue(final Class<?> thisClass, final String property, final Supplier<String> defaultMethod) {
        final Class<?> superclass = thisClass.getSuperclass();
        if (Object.class.equals(superclass)) {
            throw new IllegalStateException("Current class " + thisClass.getSimpleName() + " does not extend any other class"
                + " and default method from " + thisClass.getSimpleName() + " was called, either override this method or "
                + "check what's wrong as this shouldn't happen");
        }
        return Optional.ofNullable(System.getProperty(superclass.getSimpleName().toLowerCase() + property)).orElseGet(defaultMethod);
    }
}
