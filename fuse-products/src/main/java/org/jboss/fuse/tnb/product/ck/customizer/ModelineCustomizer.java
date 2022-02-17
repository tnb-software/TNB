package org.jboss.fuse.tnb.product.ck.customizer;

import com.github.javaparser.ast.comments.Comment;

import java.util.stream.Collectors;

public class ModelineCustomizer extends CamelKCustomizer {
    private static final String MODELINE_PREFIX = "camel-k:";
    protected String toPrepend;

    public ModelineCustomizer() { }

    public ModelineCustomizer(String toPrepend) {
        this.toPrepend = toPrepend;
    }

    @Override
    public void customize() {
        StringBuilder modeline = new StringBuilder(MODELINE_PREFIX + " language=java ");
        modeline.append(toPrepend).append(" ");
        modeline.append(getIntegrationBuilder().getRouteBuilder().getComment().stream().map(Comment::getContent)
            .filter(c -> c.startsWith(MODELINE_PREFIX))
            .map(c -> c.replaceAll(MODELINE_PREFIX + "\\s*", ""))
            .collect(Collectors.joining(" ")));
        getIntegrationBuilder().getRouteBuilder().setLineComment(modeline.append("\n").toString());
    }
}
