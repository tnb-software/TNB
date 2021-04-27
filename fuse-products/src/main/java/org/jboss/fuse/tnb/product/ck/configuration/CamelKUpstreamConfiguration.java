package org.jboss.fuse.tnb.product.ck.configuration;

public class CamelKUpstreamConfiguration extends CamelKConfiguration {

    @Override
    public String subscriptionChannel() {
        return getProperty(SUBSCRIPTION_CHANNEL, "stable");
    }

    @Override
    public String subscriptionOperatorName() {
        return getProperty(SUBSCRIPTION_OPERATOR_NAME, "camel-k");
    }

    @Override
    public String subscriptionSource() {
        return getProperty(SUBSCRIPTION_SOURCE, "community-operators");
    }

    @Override
    public String subscriptionSourceNamespace() {
        return getProperty(SUBSCRIPTION_SOURCE_NAMESPACE, "openshift-marketplace");
    }
}
