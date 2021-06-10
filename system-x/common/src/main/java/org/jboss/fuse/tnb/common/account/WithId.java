package org.jboss.fuse.tnb.common.account;

/**
 * Represents an account with ID, with a default value that can be overriden via system properties.
 */
public interface WithId {
    /**
     * Get the id from system property if set, the system property is for example "awsaccount.id".
     *
     * @return value of system property or default id
     */
    default String getId() {
        return System.getProperty(this.getClass().getSimpleName().toLowerCase() + ".id", credentialsId());
    }

    /**
     * Default id.
     *
     * @return id
     */
    String credentialsId();
}
