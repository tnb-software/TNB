package org.jboss.fuse.tnb.product.integration;

import org.jboss.fuse.tnb.customizer.Customizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.squareup.javapoet.CodeBlock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Wrapper around javapoet's codeblock.
 */
public class IntegrationBuilder {
    private static final Logger LOG = LoggerFactory.getLogger(IntegrationBuilder.class);
    private final String integrationName;
    private final CodeBlock.Builder codeBlockBuilder = CodeBlock.builder();
    private String from;
    private final List<String> to = new ArrayList<>();
    private final List<Customizer> customizers = new ArrayList<>();
    private final List<String> camelDependencies = new ArrayList<>();

    public IntegrationBuilder(String integrationName) {
        this.integrationName = integrationName;
    }

    public IntegrationBuilder from(String from) {
        if (this.from != null) {
            LOG.warn("Multiple routes are not supported yet!");
        }
        this.from = from;
        return this;
    }

    public IntegrationBuilder to(String to) {
        this.to.add(to);
        return this;
    }

    public IntegrationBuilder customize(Customizer... customizer) {
        customizers.addAll(Arrays.asList(customizer));
        return this;
    }

    public IntegrationBuilder camelDependencies(String... dependencies) {
        this.camelDependencies.addAll(Arrays.asList(dependencies));
        return this;
    }

    public String getIntegrationName() {
        return integrationName;
    }

    public List<Customizer> getCustomizers() {
        return customizers;
    }

    public List<String> getCamelDependencies() {
        return camelDependencies;
    }

    public CodeBlock getCodeBlock() {
        StringBuilder sb = new StringBuilder("from($S)");
        to.forEach(to -> sb.append(".to($S)"));
        List<Object> args = new ArrayList<>();
        args.add(from);
        args.addAll(to);
        return this.codeBlockBuilder.addStatement(sb.toString(), args.toArray()).build();
    }
}
