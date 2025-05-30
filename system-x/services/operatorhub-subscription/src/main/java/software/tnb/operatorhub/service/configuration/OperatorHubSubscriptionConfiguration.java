package software.tnb.operatorhub.service.configuration;

import software.tnb.common.service.configuration.ServiceConfiguration;

import java.util.Map;

public class OperatorHubSubscriptionConfiguration extends ServiceConfiguration {

    private static final String OH_OPERATOR_NAME = "oh.operator.name";
    private static final String OH_OPERATOR_CATALOG = "oh.operator.catalog";
    private static final String OH_OPERATOR_CATALOG_NAMESPACE = "oh.operator.catalog.namespace";
    private static final String OH_OPERATOR_CHANNEL = "oh.operator.channel";
    private static final String OH_SUBSCRIPTION_NAME = "oh.subscription.name";
    private static final String OH_SUBSCRIPTION_NAMESPACE = "oh.subscription.namespace";
    private static final String OH_SUBSCRIPTION_CLUSTER_WIDE = "oh.subscription.clusterwide";
    private static final String OH_OPERATOR_ENV_VARS = "oh.operator.env.vars";
    private static final String OH_OPERATOR_POD_SELECTOR = "oh.operator.pod.selector";

    public OperatorHubSubscriptionConfiguration operatorName(String operatorName) {
        set(OH_OPERATOR_NAME, operatorName);
        return this;
    }

    public String getOperatorName() {
        return get(OH_OPERATOR_NAME, String.class);
    }

    public OperatorHubSubscriptionConfiguration operatorCatalog(String operatorCatalog) {
        set(OH_OPERATOR_CATALOG, operatorCatalog);
        return this;
    }

    public String getOperatorCatalog() {
        return get(OH_OPERATOR_CATALOG, String.class);
    }

    public OperatorHubSubscriptionConfiguration operatorCatalogNamespace(String operatorCatalogNamespace) {
        set(OH_OPERATOR_CATALOG_NAMESPACE, operatorCatalogNamespace);
        return this;
    }

    public String getOperatorCatalogNamespace() {
        return get(OH_OPERATOR_CATALOG_NAMESPACE, String.class);
    }

    public OperatorHubSubscriptionConfiguration operatorChannel(String operatorChannel) {
        set(OH_OPERATOR_CHANNEL, operatorChannel);
        return this;
    }

    public String getOperatorChannel() {
        return get(OH_OPERATOR_CHANNEL, String.class);
    }

    public OperatorHubSubscriptionConfiguration subscriptionName(String subscriptionName) {
        set(OH_SUBSCRIPTION_NAME, subscriptionName);
        return this;
    }

    public String getSubscriptionName() {
        return get(OH_SUBSCRIPTION_NAME, String.class);
    }

    public OperatorHubSubscriptionConfiguration targetNamespace(String targetNamespace) {
        set(OH_SUBSCRIPTION_NAMESPACE, targetNamespace);
        return this;
    }

    public String getTargetNamespace() {
        return get(OH_SUBSCRIPTION_NAMESPACE, String.class);
    }

    public OperatorHubSubscriptionConfiguration clusterWide(boolean clusterWide) {
        set(OH_SUBSCRIPTION_CLUSTER_WIDE, clusterWide);
        return this;
    }

    public boolean isClusterWide() {
        return get(OH_SUBSCRIPTION_CLUSTER_WIDE, Boolean.class);
    }

    public OperatorHubSubscriptionConfiguration operatorEnvVariables(Map<String, String> env) {
        set(OH_OPERATOR_ENV_VARS, env);
        return this;
    }

    public Map<String, String> getOperatorEnvVariables() {
        return get(OH_OPERATOR_ENV_VARS, Map.class);
    }

    public OperatorHubSubscriptionConfiguration podSelector(Map<String, String> labels) {
        set(OH_OPERATOR_POD_SELECTOR, labels);
        return this;
    }

    public Map<String, String> getPodSelector() {
        return get(OH_OPERATOR_POD_SELECTOR, Map.class);
    }
}
