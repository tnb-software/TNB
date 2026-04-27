package software.tnb.product.quarkus.cxf.variant;

import software.tnb.product.quarkus.cxf.configuration.CxfQuarkusConfiguration;
import software.tnb.product.quarkus.vanilla.variant.QuarkusVariant;

import java.util.HashSet;
import java.util.Set;

/**
 * CXF Quarkus variant.
 */
public class CxfQuarkusVariant extends QuarkusVariant {
    @Override
    public String additionalBomGroupId() {
        return CxfQuarkusConfiguration.cxfQuarkusPlatformGroupId();
    }

    @Override
    public String additionalBomArtifactId() {
        return CxfQuarkusConfiguration.cxfQuarkusPlatformArtifactId();
    }

    @Override
    public String additionalBomVersion() {
        return CxfQuarkusConfiguration.cxfQuarkusPlatformVersion();
    }

    @Override
    public Set<String> getExtensions() {
        // CXF Quarkus needs smallrye-health for proper readiness probes
        Set<String> extensions = new HashSet<>(super.getExtensions());
        extensions.add("smallrye-health");
        return extensions;
    }

    @Override
    public String[] getAdditionalDependencies() {
        return new String[]{"io.quarkiverse.cxf:quarkus-cxf"};
    }
}
