package org.jboss.fuse.tnb.product.integration;

import org.jboss.fuse.tnb.common.config.TestConfiguration;
import org.jboss.fuse.tnb.customizer.Customizer;

import org.apache.camel.builder.RouteBuilder;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * Dumps the javapoet's codeblock.
 */
public final class IntegrationGenerator {
    private static final Logger LOG = LoggerFactory.getLogger(IntegrationGenerator.class);

    /**
     * Dumps the codeblock into a string.
     *
     * @param integrationBuilder integration builder instance
     * @return string
     */
    public static String asString(IntegrationBuilder integrationBuilder) {
        StringBuilder out = new StringBuilder();
        create(integrationBuilder, out);
        return out.toString();
    }

    /**
     * Dumps the codeblock into a file.
     *
     * @param integrationBuilder integration builder instance
     * @param location location of the root of the app
     */
    public static void toFile(IntegrationBuilder integrationBuilder, Path location) {
        create(integrationBuilder, location);
    }

    /**
     * Dumps the codeblock into a given object.
     *
     * @param integrationBuilder integration builder instance
     * @param out where to dump
     */
    private static void create(IntegrationBuilder integrationBuilder, Object out) {
        final MethodSpec.Builder configureMethodBuilder = MethodSpec.methodBuilder("configure")
            .addAnnotation(Override.class)
            .returns(TypeName.VOID)
            .addModifiers(Modifier.PUBLIC)
            .addException(Exception.class);

        final TypeSpec.Builder routeBuilderClassBuilder = TypeSpec.classBuilder("MyRouteBuilder")
            .addModifiers(Modifier.PUBLIC)
            .superclass(RouteBuilder.class);

        final Properties applicationProperties = new Properties();

        for (Customizer customizer : integrationBuilder.getCustomizers()) {
            customizer.setConfigureMethodBuilder(configureMethodBuilder);
            customizer.setRouteBuilderClassBuilder(routeBuilderClassBuilder);
            customizer.setApplicationProperties(applicationProperties);
            customizer.customize();
        }

        final MethodSpec configureMethod = configureMethodBuilder.addCode(CodeBlock.builder()
            .add(integrationBuilder.build())
            .build())
            .build();

        routeBuilderClassBuilder.addMethod(configureMethod);

        // For camel quarkus we add @ApplicationScoped to RouteBuilder class
        // And dump properties into application.properties
        if ("camelquarkus".equals(TestConfiguration.product())) {
            routeBuilderClassBuilder.addAnnotation(ApplicationScoped.class);

            Path applicationPropertiesPath = ((Path) out).resolve("src/main/resources/application.properties");
            String applicationPropertiesContent = applicationProperties.entrySet().stream().map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining("\n"));
            LOG.debug("Application properties:\n{}", applicationPropertiesContent);
            try {
                // Properties#store() escapes stuff by default, so construct the property file manually
                Files.write(applicationPropertiesPath, applicationPropertiesContent.getBytes());
            } catch (IOException e) {
                throw new RuntimeException("Unable to write application.properties: ", e);
            }
        }

        JavaFile javaFile = JavaFile.builder(TestConfiguration.appGroupId(), routeBuilderClassBuilder.build()).build();
        LOG.debug("Generated class: \n{}", javaFile.toString());

        try {
            if (out instanceof Appendable) {
                javaFile.writeTo((Appendable) out);
            } else {
                javaFile.writeTo(((Path) out).resolve("src/main/java"));
            }
        } catch (IOException e) {
            throw new RuntimeException("Unable to write: ", e);
        }
    }
}
