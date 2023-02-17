package software.tnb.common.account;

/**
 * Represents an account with ID, with a default value that can be overriden via system properties.
 */
public interface WithId {
    String SYSTEM_PROPERTY_FORMAT = "tnb.%s.id";

    /**
     * Get the id from system property if set, the system property is for example "tnb.awsaccount.id".
     *
     * @return value of system property or default id
     */
    default String getId() {
        return System.getProperty(String.format(SYSTEM_PROPERTY_FORMAT, this.getClass().getSimpleName().toLowerCase()), credentialsId());
    }

    /**
     * Default id.
     *
     * @return id
     */
    String credentialsId();
}
