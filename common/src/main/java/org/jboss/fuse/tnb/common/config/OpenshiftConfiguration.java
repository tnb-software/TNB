package org.jboss.fuse.tnb.common.config;

import java.util.UUID;

public class OpenshiftConfiguration extends Configuration {
    private static final String OPENSHIFT_URL = "openshift.url";
    private static final String OPENSHIFT_USERNAME = "openshift.username";
    private static final String OPENSHIFT_PASSWORD = "openshift.password";
    private static final String OPENSHIFT_NAMESPACE = "openshift.namespace";

    private static final String OPENSHIFT_DEPLOYMENT_LABEL = "openshift.deployment.label";

    private static boolean OPENSHIFT_IS_TEMPORARY_NAMESPACE = false;
    private static final String NAMESPACE_PREFIX = "tnb-test-";

    static {
        if ((openshiftUrl() != null) && (openshiftNamespace() == null)) {
            OPENSHIFT_IS_TEMPORARY_NAMESPACE = true;
            //generate new name for temporary namespace
            String random = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 8);
            setProperty(OPENSHIFT_NAMESPACE, NAMESPACE_PREFIX + random);
        }
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

    public static String openshiftNamespace() {
        return getProperty(OPENSHIFT_NAMESPACE);
    }

    public static String openshiftDeploymentLabel() {
        return getProperty(OPENSHIFT_DEPLOYMENT_LABEL, "app");
    }

    public static boolean isOpenshift() {
        return openshiftUrl() != null;
    }

    public static boolean isTemporaryNamespace() {
        return OPENSHIFT_IS_TEMPORARY_NAMESPACE;
    }
}
