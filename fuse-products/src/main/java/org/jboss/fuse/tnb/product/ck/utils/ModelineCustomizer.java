package org.jboss.fuse.tnb.product.ck.utils;

import org.jboss.fuse.tnb.product.integration.Customizer;
import org.jboss.fuse.tnb.product.util.maven.Maven;

import org.apache.maven.model.Dependency;

import java.util.stream.Collectors;

public class ModelineCustomizer extends Customizer {
    private static final String MODELINE_PREFIX = "camel-k:";

    @Override
    public void customize() {
        StringBuilder modeline = new StringBuilder(MODELINE_PREFIX + " language=java");
        for (String dependency : getIntegrationBuilder().getDependencies()) {
            if (dependency.startsWith("github")) {
                modeline.append(" dependency=").append(dependency);
            } else {
                final Dependency dep = Maven.toDependency(dependency);

                modeline.append(" dependency=mvn:").append(dep.getGroupId()).append(":").append(dep.getArtifactId());
                if (dep.getVersion() != null) {
                    modeline.append(":").append(dep.getVersion());
                }
            }
        }
        String prevModelines =
            getIntegrationBuilder().getRouteBuilder().getComment().stream().map(c -> c.getContent())
                .filter(c -> c.startsWith(MODELINE_PREFIX))
                .map(c -> c.replaceAll(MODELINE_PREFIX + "\\s*", ""))
                .collect(Collectors.joining(" "));
        modeline.append(" " + prevModelines);
        getIntegrationBuilder().getRouteBuilder().setLineComment(modeline.append("\n").toString());
    }
}
