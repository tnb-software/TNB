package org.jboss.fuse.tnb.product.util;

import org.apache.camel.builder.RouteBuilder;
import org.jboss.fuse.tnb.common.config.TestConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import javax.enterprise.context.ApplicationScoped;
import javax.lang.model.element.Modifier;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Dumps the javapoet's codeblock.
 */
public final class RouteBuilderGenerator {
    private static final Logger LOG = LoggerFactory.getLogger(RouteBuilderGenerator.class);

    /**
     * Dumps the codeblock into a string.
     * @param definition codeblock
     * @return string
     */
    public static String asString(CodeBlock definition) {
        StringBuilder out = new StringBuilder();
        create(definition, out);
        return out.toString();
    }

    /**
     * Dumps the codeblock into a file.
     * @param definition codeblock
     * @param location location
     */
    public static void toFile(CodeBlock definition, Path location) {
        create(definition, location);
    }

    /**
     * Dumps the codeblock into a given object.
     * @param definition codeblock
     * @param out where to dump
     */
    private static void create(CodeBlock definition, Object out) {
        MethodSpec configure = MethodSpec.methodBuilder("configure")
            .addAnnotation(Override.class)
            .returns(TypeName.VOID)
            .addModifiers(Modifier.PUBLIC)
            .addException(Exception.class)
            .addCode(CodeBlock.builder()
                .add(definition)
                .build())
            .build();

        final TypeSpec.Builder typeSpecBuilder = TypeSpec.classBuilder("MyRouteBuilder")
            .addModifiers(Modifier.PUBLIC)
            .superclass(RouteBuilder.class)
            .addMethod(configure);
        if ("camelquarkus".equals(TestConfiguration.product())) {
            typeSpecBuilder.addAnnotation(ApplicationScoped.class);
        }

        JavaFile javaFile = JavaFile.builder(TestConfiguration.appGroupId(), typeSpecBuilder.build()).build();
        LOG.debug("Generated class: \n{}", javaFile.toString());

        try {
            if (out instanceof Appendable) {
                javaFile.writeTo((Appendable) out);
            } else {
                javaFile.writeTo((Path) out);
            }
        } catch (IOException e) {
            throw new RuntimeException("Unable to write: ", e);
        }
    }
}
