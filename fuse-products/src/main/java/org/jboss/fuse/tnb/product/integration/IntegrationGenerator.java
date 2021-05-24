package org.jboss.fuse.tnb.product.integration;

import org.jboss.fuse.tnb.common.config.TestConfiguration;
import org.jboss.fuse.tnb.common.product.ProductType;
import org.jboss.fuse.tnb.common.utils.IOUtils;
import org.jboss.fuse.tnb.common.utils.MapUtils;
import org.jboss.fuse.tnb.customizer.Customizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.visitor.ModifierVisitor;
import com.github.javaparser.ast.visitor.Visitable;

import javax.enterprise.context.ApplicationScoped;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

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
        return create(integrationBuilder);
    }

    /**
     * Dumps the codeblock into a file.
     *
     * @param integrationBuilder integration builder instance
     * @param location location of the root of the app
     */
    public static void toFile(IntegrationBuilder integrationBuilder, Path location) {
        final IntegrationData integrationData = create(integrationBuilder);

        Path applicationPropertiesPath = location.resolve("src/main/resources/application.properties");
        String applicationPropertiesContent = MapUtils.propertiesToString(integrationBuilder.getAppProperties());
        LOG.debug("Application properties:\n{}", applicationPropertiesContent);
        try {
            // Properties#store() escapes stuff by default, so construct the property file manually
            Files.write(applicationPropertiesPath, applicationPropertiesContent.getBytes(), StandardOpenOption.APPEND, StandardOpenOption.CREATE);

        } catch (IOException e) {
            throw new RuntimeException("Unable to write application.properties: ", e);
        }

        final Path sources = location.resolve("src/main/java");
        final PackageDeclaration packageDeclaration = integrationBuilder.getRouteBuilder().getPackageDeclaration().get();
        final Path destination = sources.resolve(packageDeclaration.getName().asString().replace(".", "/"));
        destination.toFile().mkdirs();
        IOUtils.writeFile(destination.resolve("MyRouteBuilder.java"),
            integrationData.getIntegration());
    }

    /**
     * Dumps the integrationbuilder into a given object with populating the properties file.
     *
     * @param integrationBuilder integration builder instance
     */
    private static IntegrationData create(IntegrationBuilder integrationBuilder) {
        // For camel quarkus we add @ApplicationScoped to RouteBuilder class
        // And dump properties into application.properties
        if (TestConfiguration.product() == ProductType.CAMEL_QUARKUS) {
            integrationBuilder.getRouteBuilder().accept(new ModifierVisitor<>() {
                @Override
                public Visitable visit(ClassOrInterfaceDeclaration n, Object arg) {
                    super.visit(n, arg);
                    if (n.getExtendedTypes().stream().anyMatch(x -> x.getNameAsString().equals("RouteBuilder"))) {
                        n.addAnnotation(ApplicationScoped.class);
                    }
                    return n;
                }
            }, null);
        }

        for (Customizer customizer : integrationBuilder.getCustomizers()) {
            customizer.setRouteBuilder(integrationBuilder.getRouteBuilder());
            customizer.setApplicationProperties(integrationBuilder.getAppProperties());
            customizer.customize();
        }

        LOG.debug("Integration class:\n{}", integrationBuilder.getRouteBuilder().toString());

        return new IntegrationData(integrationBuilder.getRouteBuilder().toString(), integrationBuilder.getAppProperties(),
            integrationBuilder.getCamelDependencies());
    }
}
