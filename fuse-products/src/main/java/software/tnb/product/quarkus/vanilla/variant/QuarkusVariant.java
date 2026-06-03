package software.tnb.product.quarkus.vanilla.variant;

import software.tnb.common.config.OpenshiftConfiguration;
import software.tnb.product.customizer.Customizer;
import software.tnb.product.quarkus.vanilla.customizer.QuarkusBomCustomizer;

import java.util.List;
import java.util.Set;

/**
 * Represents different variants of Quarkus applications (vanilla Quarkus, Camel Quarkus, etc.).
 * This abstract class allows customization of BOM coordinates and other variant-specific configuration.
 */
public abstract class QuarkusVariant {
    /**
     * Returns the group ID for the additional BOM.
     *
     * @return BOM group ID
     */
    public abstract String additionalBomGroupId();

    /**
     * Returns the artifact ID for the additional BOM.
     *
     * @return BOM artifact ID
     */
    public abstract String additionalBomArtifactId();

    /**
     * Returns the version for the additional BOM.
     *
     * @return BOM version
     */
    public abstract String additionalBomVersion();

    /**
     * Returns set of Quarkus extensions to include in the project.
     * By default returns "openshift" extension when running on OpenShift, empty set otherwise.
     * Variants can override this to add additional extensions.
     *
     * @return set of extensions (e.g., Set.of("openshift", "smallrye-health"))
     */
    public Set<String> getExtensions() {
        return OpenshiftConfiguration.isOpenshift() ? Set.of("openshift") : Set.of();
    }

    /**
     * Returns the startup regex pattern for this variant.
     * By default returns the Quarkus startup pattern.
     * Variants can override this to use a different pattern (e.g., Camel uses a different one).
     *
     * @return startup regex pattern
     */
    public String getStartupRegex() {
        return "(?m)^.*Quarkus.*started in.*$";
    }

    /**
     * Returns list of customizers for this variant.
     * By default returns the BOM customizer.
     * Variants can override this to add additional customizers.
     *
     * @return list of customizers
     */
    public List<Customizer> getCustomizers() {
        return List.of(new QuarkusBomCustomizer(
            additionalBomGroupId(),
            additionalBomArtifactId(),
            additionalBomVersion(),
            getExtensions()
        ));
    }

    /**
     * Returns array of additional dependencies for this variant.
     * By default returns an empty array.
     * Variants can override this to add variant-specific dependencies.
     *
     * @return array of dependencies in GA[V] format (e.g., "io.quarkiverse.cxf:quarkus-cxf")
     */
    public String[] getAdditionalDependencies() {
        return new String[0];
    }
}
