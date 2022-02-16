package org.jboss.fuse.tnb.product.ck.customizer;

import org.apache.maven.model.Dependency;

public class DependenciesToModelineCustomizer extends ModelineCustomizer {
    @Override
    public void customize() {
        StringBuilder modeline = new StringBuilder();
        for (Dependency dependency : getIntegrationBuilder().getDependencies()) {
            modeline.append(" dependency=");
            if (!dependency.getGroupId().equals("github")) {
                modeline.append("mvn:");
            }
            modeline.append(dependency.getGroupId()).append(":").append(dependency.getArtifactId());
            if (dependency.getVersion() != null) {
                modeline.append(":").append(dependency.getVersion());
            }
        }

        super.toPrepend = modeline.toString();
        super.customize();
    }
}
