package org.jboss.fuse.tnb.product.ck.utils;

import org.jboss.fuse.tnb.product.integration.Customizer;
import org.jboss.fuse.tnb.product.util.maven.Maven;

import org.apache.maven.model.Dependency;

public class ModelineCustomizer extends Customizer {
    @Override
    public void customize() {
        StringBuilder modeline = new StringBuilder("camel-k: language=java");
        for (String dependency : getIntegrationBuilder().getDependencies()) {
            final Dependency dep = Maven.toDependency(dependency);
            modeline.append(" dependency=mvn:").append(dep.getGroupId()).append(":").append(dep.getArtifactId());
            if (dep.getVersion() != null) {
                modeline.append(":").append(dep.getVersion());
            }
        }
        getIntegrationBuilder().getRouteBuilder().setLineComment(modeline.append("\n").toString());
    }
}
