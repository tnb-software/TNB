package software.tnb.product.ck.configuration;

import software.tnb.common.config.Configuration;

import java.time.temporal.ChronoUnit;

import io.fabric8.kubernetes.api.model.Duration;

public abstract class CamelKConfiguration extends Configuration {
    public static final String FORCE_UPSTREAM = "force.upstream";

    public static final String SUBSCRIPTION_CHANNEL = "camelk.subscription.channel";
    public static final String SUBSCRIPTION_OPERATOR_NAME = "camelk.subscription.operatorName";
    public static final String SUBSCRIPTION_SOURCE = "camelk.subscription.source";
    public static final String SUBSCRIPTION_SOURCE_NAMESPACE = "camelk.subscription.sourceNamespace";
    public static final String QUICKSTART_BRANCH = "camelk.quickstart.branch";
    public static final String MAVEN_SETTINGS_CONFIG_MAP_NAME = "camelk.maven.settings.configmap.name";
    public static final String MAVEN_BUILD_TIMEOUT = "camelk.build.timeout";
    public static final String INTEGRATION_PLATFORM_NAME = "camelk.integration.platform.name";

    public String subscriptionName() {
        return "tnb-camel-k";
    }

    public String mavenSettingsConfigMapName() {
        return getProperty(MAVEN_SETTINGS_CONFIG_MAP_NAME, "tnb-maven-settings");
    }

    public Duration mavenBuildTimeout() {
        return new Duration(java.time.Duration.of(getInteger(MAVEN_BUILD_TIMEOUT, 30), ChronoUnit.MINUTES));
    }

    public String integrationPlatformName() {
        return getProperty(INTEGRATION_PLATFORM_NAME, "camel-k");
    }

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
