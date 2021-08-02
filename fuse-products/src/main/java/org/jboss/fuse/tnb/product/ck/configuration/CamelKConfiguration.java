package org.jboss.fuse.tnb.product.ck.configuration;

import org.jboss.fuse.tnb.common.config.Configuration;

public abstract class CamelKConfiguration extends Configuration {
    private static final String FORCE_UPSTREAM = "force.upstream";

    protected static final String SUBSCRIPTION_CHANNEL = "camelk.subscription.channel";
    protected static final String SUBSCRIPTION_OPERATOR_NAME = "camelk.subscription.operatorName";
    public static final String SUBSCRIPTION_SOURCE = "camelk.subscription.source";
    protected static final String SUBSCRIPTION_SOURCE_NAMESPACE = "camelk.subscription.sourceNamespace";
    protected static final String QUICKSTART_BRANCH = "camelk.quickstart.branch";

    public static final String SUBSCRIPTION_NAME = "tnb-camel-k";

    public abstract String subscriptionChannel();

    public abstract String subscriptionOperatorName();

    public abstract String subscriptionSource();

    public abstract String subscriptionSourceNamespace();

    public String quickstartBranch() {
        return getProperty(QUICKSTART_BRANCH, "main");
    }

    public static boolean forceUpstream() {
        return getBoolean(FORCE_UPSTREAM, false);
    }

    public static CamelKConfiguration getConfiguration() {
        if (forceUpstream()) {
            return new CamelKUpstreamConfiguration();
        } else {
            return new CamelKProdConfiguration();
        }
    }
}
