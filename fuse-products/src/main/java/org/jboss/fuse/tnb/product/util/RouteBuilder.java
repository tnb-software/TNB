package org.jboss.fuse.tnb.product.util;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;

public class RouteBuilder {
    private final CodeBlock.Builder codeBlockBuilder;
    private String from;
    private String to;

    public RouteBuilder() {
        codeBlockBuilder = CodeBlock.builder();
    }

    public RouteBuilder from(String from) {
        this.from = from;
        return this;
    }

    public RouteBuilder to(String to) {
        this.to = to;
        return this;
    }

    public RouteBuilder addToRegistry(String name, String code, ClassName className) {
        this.codeBlockBuilder.addStatement("bindToRegistry($S, " + code.replace(className.simpleName(), "$T") + ")", name, className);
        return this;
    }

    public CodeBlock build() {
        return this.codeBlockBuilder.addStatement("from($S).to($S)", from, to).build();
    }
}
