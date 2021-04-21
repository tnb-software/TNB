package org.jboss.fuse.tnb.product.integration;

import org.jboss.fuse.tnb.customizer.Customizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.TypeSpec;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Wrapper around javapoet's codeblock.
 */
public class IntegrationBuilder {
    private static final Logger LOG = LoggerFactory.getLogger(IntegrationBuilder.class);
    private final CodeBlock.Builder codeBlockBuilder = CodeBlock.builder();
    private final List<Customizer> customizers = new ArrayList<>();
    private final List<String> camelDependencies = new ArrayList<>();
    private String integrationName;

    public IntegrationBuilder(String integrationName) {
        this.integrationName = integrationName;
    }

    public IntegrationBuilder from(String from) {
        if (!codeBlockBuilder.isEmpty()) {
            LOG.warn("Multiple routes are not supported yet!");
            // Technically, the only thing needed here should be to add a missing semicolon to end the previous route
        }
        codeBlockBuilder.add("from($S)", from);
        return this;
    }

    public IntegrationBuilder to(String to) {
        codeBlockBuilder.add(".to($S)", to);
        return this;
    }

    public IntegrationBuilder log(String log) {
        codeBlockBuilder.add(".log($S)", log);
        return this;
    }

    public IntegrationBuilder process(TypeSpec processor) {
        codeBlockBuilder.add(".process($L)", processor);
        return this;
    }

    public IntegrationBuilder customize(Customizer... customizer) {
        customizers.addAll(Arrays.asList(customizer));
        return this;
    }

    public IntegrationBuilder camelDependencies(String... dependencies) {
        camelDependencies.addAll(Arrays.asList(dependencies));
        return this;
    }

    public IntegrationBuilder name(String name) {
        this.integrationName = name;
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
        if (!codeBlockBuilder.toString().endsWith(";")) {
            codeBlockBuilder.add(";");
        }
        return codeBlockBuilder.build();
    }
}
