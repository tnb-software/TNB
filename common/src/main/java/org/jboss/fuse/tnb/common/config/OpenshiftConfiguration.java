package org.jboss.fuse.tnb.common.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

public class OpenshiftConfiguration extends Configuration {
    private static final Logger LOG = LoggerFactory.getLogger(OpenshiftConfiguration.class);
    public static final String USE_OPENSHIFT = "test.use.openshift";

    private static final String OPENSHIFT_URL = "openshift.url";
    private static final String OPENSHIFT_USERNAME = "openshift.username";
    private static final String OPENSHIFT_PASSWORD = "openshift.password";
    private static final String OPENSHIFT_NAMESPACE = "openshift.namespace";
    private static final String OPENSHIFT_KUBECONFIG = "openshift.kubeconfig";
    private static final String OPENSHIFT_HTTPS_PROXY = "openshift.https.proxy";

    private static final String OPENSHIFT_DEPLOYMENT_LABEL = "openshift.deployment.label";

    private static final String OPENSHIFT_DEPLOY_STRATEGY = "openshift.deploy.strategy";

    private static final String NAMESPACE_PREFIX = "tnb-test-";

    private static boolean isTemporaryNamespace = false;

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
        return getProperty(OPENSHIFT_HTTPS_PROXY, null);
    }

    public static String openshiftNamespace() {
        String namespace = getProperty(OPENSHIFT_NAMESPACE);
        if (namespace == null) {
            namespace = NAMESPACE_PREFIX + UUID.randomUUID().toString().replaceAll("-", "").substring(0, 8);
            isTemporaryNamespace = true;
            LOG.info("Using namespace {}", namespace);
            setProperty(OPENSHIFT_NAMESPACE, namespace);
        }
        return namespace;
    }

    public static Path openshiftKubeconfig() {
        return Paths.get(getProperty(OPENSHIFT_KUBECONFIG, System.getProperty("user.home") + "/.kube/config"));
    }

    public static String openshiftDeploymentLabel() {
        return getProperty(OPENSHIFT_DEPLOYMENT_LABEL, "app");
    }

    public static boolean isTemporaryNamespace() {
        return isTemporaryNamespace;
    }

    public static String getDeployStrategy() {
        return getProperty(OPENSHIFT_DEPLOY_STRATEGY, "jkube");
    }
}
