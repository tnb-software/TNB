package org.jboss.fuse.tnb.product.ck.configuration;

public class CamelKProdConfiguration extends CamelKConfiguration {

    @Override
    public String subscriptionChannel() {
        return getProperty(SUBSCRIPTION_CHANNEL, "techpreview");
    }

    @Override
    public String subscriptionOperatorName() {
        return getProperty(SUBSCRIPTION_OPERATOR_NAME, "red-hat-camel-k");
    }

    @Override
    public String subscriptionSource() {
        return getProperty(SUBSCRIPTION_SOURCE, "redhat-operators");
    }

    @Override
    public String subscriptionSourceNamespace() {
        return getProperty(SUBSCRIPTION_SOURCE_NAMESPACE, "openshift-marketplace");
    }
}
