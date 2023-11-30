package software.tnb.common.deployment;

import software.tnb.common.openshift.OpenshiftClient;
import software.tnb.common.util.ReflectionUtil;
import software.tnb.common.utils.WaitUtils;

import java.util.List;
import java.util.Optional;
import java.util.function.BooleanSupplier;

import io.fabric8.kubernetes.api.model.EnvVar;
import io.fabric8.openshift.api.model.operatorhub.v1alpha1.SubscriptionConfig;

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
        return "tnb-" + ReflectionUtil.getSuperClassName(this.getClass()).toLowerCase();
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
                targetNamespace(), clusterWide(), null, Optional.ofNullable(getOperatorEnvVariables())
                    .map(envVars -> {
                        SubscriptionConfig config = new SubscriptionConfig();
                        config.setEnv(envVars);
                        return config;
                    }).orElse(null));
        OpenshiftClient.get().waitForInstallPlanToComplete(subscriptionName(), targetNamespace());
    }

    default List<EnvVar> getOperatorEnvVariables() {
        return null;
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
        return Optional.ofNullable(System.getProperty("tnb." + ReflectionUtil.getSuperClassName(this.getClass()).toLowerCase() + "." + property))
            .orElse(defaultValue);
    }
}
