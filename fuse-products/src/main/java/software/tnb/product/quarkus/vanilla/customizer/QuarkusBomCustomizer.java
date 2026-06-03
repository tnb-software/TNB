package software.tnb.product.quarkus.vanilla.customizer;

import software.tnb.product.customizer.Customizer;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;

import java.util.Set;

/**
 * Customizer that holds Quarkus variant-specific configuration (BOM coordinates and extensions)
 * and adds the additional BOM to the project's pom.xml.
 */
public class QuarkusBomCustomizer extends Customizer {
    private final String bomGroupId;
    private final String bomArtifactId;
    private final String bomVersion;
    private final Set<String> extensions;

    public QuarkusBomCustomizer(String bomGroupId, String bomArtifactId, String bomVersion, Set<String> extensions) {
        this.bomGroupId = bomGroupId;
        this.bomArtifactId = bomArtifactId;
        this.bomVersion = bomVersion;
        this.extensions = extensions;
    }

    @Override
    public void customize() {
        // Add the additional platform BOM to pom.xml
        Model model = getModel();

        if (model == null) {
            // pom.xml doesn't exist yet (e.g., JBang mode), skip
            return;
        }

        // Check if the additional BOM is already present in the dependencies management, if it is, then it is overridden
        model.getDependencyManagement().getDependencies().stream()
            .filter(d -> d.getArtifactId().equals(bomArtifactId)).findFirst()
            .ifPresent(d -> model.getDependencyManagement().getDependencies().remove(d));

        Dependency additionalBom = new Dependency();
        additionalBom.setGroupId(bomGroupId);
        additionalBom.setArtifactId(bomArtifactId);
        additionalBom.setVersion(bomVersion);
        additionalBom.setType("pom");
        additionalBom.setScope("import");
        model.getDependencyManagement().getDependencies().add(additionalBom);
    }

    public String getBomGroupId() {
        return bomGroupId;
    }

    public String getBomArtifactId() {
        return bomArtifactId;
    }

    public String getBomVersion() {
        return bomVersion;
    }

    public Set<String> getExtensions() {
        return extensions;
    }
}
