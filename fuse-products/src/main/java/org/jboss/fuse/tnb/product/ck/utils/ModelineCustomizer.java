package org.jboss.fuse.tnb.product.ck.utils;

import org.jboss.fuse.tnb.product.ck.customizer.CamelKCustomizer;

import org.apache.maven.model.Dependency;

import com.github.javaparser.ast.comments.Comment;

import java.util.stream.Collectors;

public class ModelineCustomizer extends CamelKCustomizer {
    private static final String MODELINE_PREFIX = "camel-k:";
    private String append;

    public ModelineCustomizer() {
    }

    public ModelineCustomizer(String append) {
        this.append = append;
    }

    @Override
    public void customize() {
        StringBuilder modeline = new StringBuilder(MODELINE_PREFIX + " language=java");
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
        String prevModelines =
            getIntegrationBuilder().getRouteBuilder().getComment().stream().map(Comment::getContent)
                .filter(c -> c.startsWith(MODELINE_PREFIX))
                .map(c -> c.replaceAll(MODELINE_PREFIX + "\\s*", ""))
                .collect(Collectors.joining(" "));
        modeline.append(" ");
        if (append != null) {
            modeline.append(append).append(" ");
        }
        modeline.append(prevModelines);
        getIntegrationBuilder().getRouteBuilder().setLineComment(modeline.append("\n").toString());
    }
}
