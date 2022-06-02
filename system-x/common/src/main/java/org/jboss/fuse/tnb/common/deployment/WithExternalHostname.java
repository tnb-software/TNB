package org.jboss.fuse.tnb.common.deployment;

/**
 * Represents a hostname that is used in a communication "from outside", so from remote node to a pod in openshift.
 */
public interface WithExternalHostname {
    /**
     * This hostname represents the hostname that is used in validation to connect the client to.
     *
     * In many services this can be "localhost" when the communication goes through a port-forward.
     * @return hostname to connect the client to
     */
    String externalHostname();
}
