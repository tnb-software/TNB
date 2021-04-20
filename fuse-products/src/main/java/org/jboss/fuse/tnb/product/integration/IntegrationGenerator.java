package org.jboss.fuse.tnb.product.integration;

import org.jboss.fuse.tnb.common.config.TestConfiguration;
import org.jboss.fuse.tnb.common.product.ProductType;
import org.jboss.fuse.tnb.common.utils.MapUtils;
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

/**
 * Dumps the IntegrationBuilder class into a given object.
 */
public final class IntegrationGenerator {
    private static final Logger LOG = LoggerFactory.getLogger(IntegrationGenerator.class);

    /**
     * Dumps the codeblock into a integration data object.
     *
     * @param integrationBuilder integration builder instance
     * @return integration data instance
     */
    public static IntegrationData toIntegrationData(IntegrationBuilder integrationBuilder) {
        StringBuilder out = new StringBuilder();
        Properties properties = new Properties();
        create(integrationBuilder, properties, out);
        return new IntegrationData(out.toString(), properties);
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
     * Dumps the integrationbuilder into a given object.
     *
     * @param integrationBuilder integration builder instance
     * @param out where to dump
     */
    private static void create(IntegrationBuilder integrationBuilder, Object out) {
        create(integrationBuilder, null, out);
    }

    /**
     * Dumps the integrationbuilder into a given object with populating the properties file.
     *
     * @param integrationBuilder integration builder instance
     * @param out where to dump
     */
    private static void create(IntegrationBuilder integrationBuilder, Properties properties, Object out) {
        final MethodSpec.Builder configureMethodBuilder = MethodSpec.methodBuilder("configure")
            .addAnnotation(Override.class)
            .returns(TypeName.VOID)
            .addModifiers(Modifier.PUBLIC)
            .addException(Exception.class);

        final TypeSpec.Builder routeBuilderClassBuilder = TypeSpec.classBuilder("MyRouteBuilder")
            .addModifiers(Modifier.PUBLIC)
            .superclass(RouteBuilder.class);

        if (properties == null) {
            properties = new Properties();
        }

        for (Customizer customizer : integrationBuilder.getCustomizers()) {
            customizer.setConfigureMethodBuilder(configureMethodBuilder);
            customizer.setRouteBuilderClassBuilder(routeBuilderClassBuilder);
            customizer.setApplicationProperties(properties);
            customizer.customize();
        }

        final MethodSpec configureMethod = configureMethodBuilder.addCode(CodeBlock.builder()
            .add(integrationBuilder.getCodeBlock())
            .build())
            .build();

        routeBuilderClassBuilder.addMethod(configureMethod);

        // For camel quarkus we add @ApplicationScoped to RouteBuilder class
        // And dump properties into application.properties
        if (TestConfiguration.product() == ProductType.CAMEL_QUARKUS) {
            routeBuilderClassBuilder.addAnnotation(ApplicationScoped.class);

            Path applicationPropertiesPath = ((Path) out).resolve("src/main/resources/application.properties");
            String applicationPropertiesContent = MapUtils.propertiesToString(properties);
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
