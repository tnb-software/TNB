package org.jboss.fuse.tnb.processor;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import javax.lang.model.element.Modifier;

public final class Processor {
    private Processor() {
    }

    public static TypeSpec create(CodeBlock code) {
        return TypeSpec.anonymousClassBuilder("")
            .addSuperinterface(ClassName.get("org.apache.camel", "Processor"))
            .addMethod(MethodSpec.methodBuilder("process")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ClassName.get("org.apache.camel", "Exchange"), "exchange")
                .addCode(code)
                .returns(TypeName.VOID)
                .build())
            .build();
    }
}
