package org.jboss.fuse.tnb.product.ck.customizer;

import org.jboss.fuse.tnb.product.ck.integration.builder.CamelKIntegrationBuilder;

import org.apache.commons.lang3.StringUtils;

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
        final String commentSign = getIntegrationBuilder().getFileName().contains(".java") ? "//" : "#";

        final String modelineRegex = commentSign + "\\s*" + MODELINE_PREFIX + ".*";

        StringBuilder modeline = new StringBuilder(MODELINE_PREFIX);
        modeline.append(toPrepend).append(" ");

        // Collect previous modelines
        if (getIntegrationBuilder().getRouteBuilder().isPresent()) {
            modeline.append(getIntegrationBuilder().getRouteBuilder().get().getComment().stream().map(Comment::getContent)
                .filter(c -> c.matches(modelineRegex))
                .map(c -> c.replaceAll(MODELINE_PREFIX + "\\s*", ""))
                .collect(Collectors.joining(" ")));

            getIntegrationBuilder().getRouteBuilder().get().setLineComment(modeline.append("\n").toString());
        } else {
            // Parse the modeline from string if no route builder class is present
            CamelKIntegrationBuilder ckib = (CamelKIntegrationBuilder) getIntegrationBuilder();
            StringBuilder remaining = new StringBuilder();

            for (String line : ckib.getContent().split("\n")) {
                if (line.matches(modelineRegex)) {
                    modeline.append(StringUtils.substringAfter(line, MODELINE_PREFIX).trim()).append(" ");
                } else {
                    remaining.append(line).append("\n");
                }
            }

            ckib.fromString(commentSign + " " + modeline.toString().trim() + "\n" + remaining);
        }
    }
}
