package org.jboss.fuse.tnb.product.ck.customizer;

import org.apache.maven.model.Dependency;

public class DependenciesToModelineCustomizer extends ModelineCustomizer {
    @Override
    public void customize() {
        if (getIntegrationBuilder().getDependencies().isEmpty()) {
            return;
        }

        StringBuilder modeline = new StringBuilder();
        for (Dependency dependency : getIntegrationBuilder().getDependencies()) {
            modeline.append("dependency=").append("github".equals(dependency.getType()) ? "github:" : "mvn:");
            modeline.append(dependency.getGroupId()).append(":").append(dependency.getArtifactId());
            if (dependency.getVersion() != null) {
                modeline.append(":").append(dependency.getVersion());
            }
            modeline.append(" ");
        }

        super.toPrepend = modeline.toString().trim();
        super.customize();
    }
}
