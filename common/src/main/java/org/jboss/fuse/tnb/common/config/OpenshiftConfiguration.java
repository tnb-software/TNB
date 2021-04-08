package org.jboss.fuse.tnb.common.config;

import org.jboss.fuse.tnb.common.utils.StringUtils;

public class OpenshiftConfiguration extends Configuration {
    private static final String OPENSHIFT_URL = "openshift.url";
    private static final String OPENSHIFT_USERNAME = "openshift.username";
    private static final String OPENSHIFT_PASSWORD = "openshift.password";
    private static final String OPENSHIFT_NAMESPACE = "openshift.namespace";

    private static final String OPENSHIFT_DEPLOYMENT_LABEL = "openshift.deployment.label";

    private static boolean OPENSHIFT_IS_TEMPORARY_NAMESPACE = false;

    static {//TODO check if namespace exists (even though it is temporary - will be added creating ns also in services)
        if ((openshiftUrl() != null) && (openshiftNamespace() == null)) {
            OPENSHIFT_IS_TEMPORARY_NAMESPACE = true;
            //generate new name for temporary namespace
            setProperty(OPENSHIFT_NAMESPACE, StringUtils.generateTemporaryNamespaceName());
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
