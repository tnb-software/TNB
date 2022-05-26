package org.jboss.fuse.tnb.common.deployment;

import org.jboss.fuse.tnb.common.openshift.OpenshiftClient;

import java.util.function.Function;

/**
 * Represents a hostname that is used internally in the cluster, so in pod -> pod communication.
 *
 */
public interface WithInClusterHostname {
    /**
     * Generally this method is used to connect other deployed services to this service.
     * @return in cluster hostname name.namespace.svc.cluster.local
     */
    default String inClusterHostname() {
        if (this instanceof WithName) {
            Function<WithName, String> getId = WithName::name;
            try {
                return OpenshiftClient.get().getClusterHostname(getId.apply((WithName) this));
            } catch (Exception e) {
                throw new RuntimeException("Unable to cast " + this.getClass().getSimpleName() + " to WithName");
            }
        } else {
            throw new IllegalArgumentException("Class " + this.getClass().getSimpleName() + " does not implement WithName,"
                + " you need to override the default implementation of inClusterHostname");
        }
    }
}
