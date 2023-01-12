package software.tnb.common.deployment;

import software.tnb.common.openshift.OpenshiftClient;
import software.tnb.common.utils.WaitUtils;

import java.util.Optional;
import java.util.function.BooleanSupplier;

public interface WithOperatorHub {
    String operatorName();

    default String operatorCatalog() {
        return "redhat-operators";
    }

    default String operatorCatalogNamespace() {
        return "openshift-marketplace";
    }

    default String operatorChannel() {
        return "stable";
    }

    default String subscriptionName() {
        return "tnb-" + getSuperClassName();
    }

    default String targetNamespace() {
        return OpenshiftClient.get().getNamespace();
    }

    default String getOperatorCatalog() {
        return getValue("operator.catalog", operatorCatalog());
    }

    default String getOperatorChannel() {
        return getValue("operator.channel", operatorChannel());
    }

    default String getOperatorName() {
        return getValue("operator.name", operatorName());
    }

    default String getOperatorCatalogNamespace() {
        return getValue("catalog.namespace", operatorCatalogNamespace());
    }

    default boolean clusterWide() {
        return false;
    }

    default void createSubscription() {
        OpenshiftClient.get()
            .createSubscription(getOperatorChannel(), getOperatorName(), getOperatorCatalog(), subscriptionName(), getOperatorCatalogNamespace(),
                targetNamespace(), clusterWide());
        OpenshiftClient.get().waitForInstallPlanToComplete(subscriptionName(), targetNamespace());
    }

    default void deleteSubscription(BooleanSupplier waitCondition) {
        OpenshiftClient.get().deleteSubscription(subscriptionName(), targetNamespace());
        WaitUtils.waitFor(waitCondition, "Waiting until the operator is undeployed");
    }

    /**
     * this.getClass() should always be an openshift variant of a system-x service, for example OpenshiftMySQL.class
     * and this class should always extend the abstract system-x service class, e.g. OpenshiftMySQL extends MySQL
     * All properties are prefixed with the name of the parent class - for example to override the operator catalog, the resulting property name is
     * mysql.operator.catalog
     *
     * @return value from system property or default value
     */
    private String getValue(final String property, final String defaultValue) {
        return Optional.ofNullable(System.getProperty(getSuperClassName() + "." + property)).orElse(defaultValue);
    }

    private String getSuperClassName() {
        final Class<?> superclass = this.getClass().getSuperclass();
        if (Object.class.equals(superclass)) {
            throw new IllegalStateException("Current class " + this.getClass().getSimpleName() + " does not extend any other class"
                + " and default method from WithOperatorHub was called, either override this method or "
                + "check what's wrong as this shouldn't happen");
        }
        return superclass.getSimpleName().toLowerCase();
    }
}
