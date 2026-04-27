package software.tnb.product.quarkus.vanilla.variant;

import software.tnb.common.config.OpenshiftConfiguration;
import software.tnb.product.integration.builder.AbstractIntegrationBuilder;

/**
 * Represents different variants of Quarkus applications (vanilla Quarkus, Camel Quarkus, etc.).
 * This interface allows customization of BOM coordinates and other variant-specific configuration.
 */
public interface QuarkusVariant {
    /**
     * Returns the group ID for the additional BOM.
     *
     * @return BOM group ID
     */
    String additionalBomGroupId();

    /**
     * Returns the artifact ID for the additional BOM.
     *
     * @return BOM artifact ID
     */
    String additionalBomArtifactId();

    /**
     * Returns the version for the additional BOM.
     *
     * @return BOM version
     */
    String additionalBomVersion();

    /**
     * Returns comma-separated list of Quarkus extensions to include in the project.
     * By default returns "openshift" when running on OpenShift, empty string otherwise.
     * Variants can override this to add additional extensions.
     *
     * @return comma-separated extensions (e.g., "openshift,smallrye-health")
     */
    default String getExtensions() {
        return OpenshiftConfiguration.isOpenshift() ? "openshift" : "";
    }

    /**
     * Customizes integration builder.
     */
    default void customizeIntegrationBuilder(AbstractIntegrationBuilder<?> integrationBuilder) {
        integrationBuilder.startupRegex("(?m)^.*Quarkus.*started in.*$");
    }
}
