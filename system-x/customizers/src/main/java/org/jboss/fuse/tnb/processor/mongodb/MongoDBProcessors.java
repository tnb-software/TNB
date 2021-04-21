package org.jboss.fuse.tnb.processor.mongodb;

import org.jboss.fuse.tnb.processor.Processor;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.TypeSpec;

public final class MongoDBProcessors {
    private MongoDBProcessors() {
    }

    public static TypeSpec extractField(String field) {
        return Processor.create(
            CodeBlock.builder()
                .addStatement("exchange.getOut().setBody(exchange.getIn().getBody($T.class).get($S))",
                    ClassName.get("org.bson", "Document"), field)
                .build()
        );
    }
}
