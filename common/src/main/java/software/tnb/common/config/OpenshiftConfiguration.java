package software.tnb.common.config;

import software.tnb.common.utils.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.nio.file.Paths;

public class OpenshiftConfiguration extends Configuration {
    private static final Logger LOG = LoggerFactory.getLogger(OpenshiftConfiguration.class);

    public static final String USE_OPENSHIFT = "test.use.openshift";
    public static final String OPENSHIFT_URL = "openshift.url";
    public static final String OPENSHIFT_USERNAME = "openshift.username";
    public static final String OPENSHIFT_PASSWORD = "openshift.password";
    public static final String OPENSHIFT_NAMESPACE = "openshift.namespace";
    public static final String OPENSHIFT_NAMESPACE_DELETE = "openshift.namespace.delete";
    public static final String OPENSHIFT_KUBECONFIG = "openshift.kubeconfig";
    public static final String KUBECONFIG = "kubeconfig";
    public static final String OPENSHIFT_HTTPS_PROXY = "openshift.https.proxy";
    public static final String OPENSHIFT_DEPLOYMENT_LABEL = "openshift.deployment.label";

    private static final String OPENSHIFT_DEPLOY_STRATEGY = "openshift.deploy.strategy";

    private static final String NAMESPACE_PREFIX = "tnb-test-";
    public static final String USE_MICROSHIFT = "test.use.microshift";

    public static boolean isOpenshift() {
        return getBoolean(USE_OPENSHIFT, false);
    }

    public static String openshiftUrl() {
        return getProperty(OPENSHIFT_URL);
    }

    public static String openshiftUsername() {
        return getProperty(OPENSHIFT_USERNAME, "admin");
    }

    public static String openshiftPassword() {
        return getProperty(OPENSHIFT_PASSWORD, "admin");
    }

    public static String openshiftHttpsProxy() {
        return getProperty(OPENSHIFT_HTTPS_PROXY, (String) null);
    }

    /**
     * Returns the openshift namespace.
     *
     * @return namespace
     * @deprecated should be used only in initializing the openshift client. If you want to get the current namespace, use OpenshiftClient.get()
     * .getNamespace()
     */
    @Deprecated
    public static String openshiftNamespace() {
        String namespace = getProperty(OPENSHIFT_NAMESPACE);
        if (namespace == null) {
            namespace = NAMESPACE_PREFIX + StringUtils.getRandomAlphanumStringOfLength(8);
        } else if (TestConfiguration.parallel()) {
            namespace += "-" + StringUtils.getRandomAlphanumStringOfLength(8);
        }
        return namespace;
    }

    public static boolean openshiftNamespaceDelete() {
        return getBoolean(OPENSHIFT_NAMESPACE_DELETE, false) || TestConfiguration.parallel() || getProperty(OPENSHIFT_NAMESPACE) == null;
    }

    public static Path openshiftKubeconfig() {
        String kubeconfig = getProperty(OPENSHIFT_KUBECONFIG, getProperty(KUBECONFIG));
        return kubeconfig == null ? null : Paths.get(kubeconfig);
    }

    public static String openshiftDeploymentLabel() {
        return getProperty(OPENSHIFT_DEPLOYMENT_LABEL, "app");
    }

    public static String getDeployStrategy() {
        return getProperty(OPENSHIFT_DEPLOY_STRATEGY, "jkube");
    }

    public static boolean isMicroshift() {
        return getBoolean(USE_MICROSHIFT, false);
    }

}
