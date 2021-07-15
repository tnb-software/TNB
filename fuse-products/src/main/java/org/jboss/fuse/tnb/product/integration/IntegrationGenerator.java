package org.jboss.fuse.tnb.product.integration;

import org.jboss.fuse.tnb.common.config.TestConfiguration;
import org.jboss.fuse.tnb.common.product.ProductType;
import org.jboss.fuse.tnb.common.utils.IOUtils;
import org.jboss.fuse.tnb.common.utils.PropertiesUtils;
import org.jboss.fuse.tnb.product.ck.utils.ModelineCustomizer;
import org.jboss.fuse.tnb.product.cq.utils.ApplicationScopeCustomizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * Dumps the IntegrationBuilder class into a given object.
 */
public final class IntegrationGenerator {
    private static final Logger LOG = LoggerFactory.getLogger(IntegrationGenerator.class);

    private IntegrationGenerator() {
    }

    /**
     * Dumps the codeblock into a integration data object.
     *
     * @param integrationBuilder integration builder instance
     * @return integration data instance
     */
    public static IntegrationData toIntegrationData(IntegrationBuilder integrationBuilder) {
        //Inline classes to the routebuilder
        integrationBuilder.getAdditionalClasses().forEach(cu -> {
            //newClass is an empty class added to the route builder
            final TypeDeclaration<?> routeBuilder = integrationBuilder.getRouteBuilder().getClassByName(IntegrationBuilder.ROUTE_BUILDER_NAME).get();
            //sourceClass is modified to be inlinable
            final TypeDeclaration<?> sourceClass = cu.getPrimaryType().get();
            sourceClass.setPublic(true);
            sourceClass.addModifier(Modifier.Keyword.STATIC);

            routeBuilder.addMember(sourceClass);
            //merge imports from the source class
            for (ImportDeclaration imp : cu.getImports()) {
                integrationBuilder.getRouteBuilder().addImport(imp.getNameAsString());
            }
            //Remove the import to the class itself (won't be present in the generated app)
            integrationBuilder.getRouteBuilder().getImports().removeIf(
                imp -> imp.getNameAsString().equals(cu.getPackageDeclaration().get().getNameAsString() + "." + sourceClass.getNameAsString()));
        });

        return create(integrationBuilder);
    }

    /**
     * Dumps the codeblock into a file.
     *
     * @param integrationBuilder integration builder instance
     * @param location location of the root of the app
     */
    public static void toFile(IntegrationBuilder integrationBuilder, Path location) {
        final Path sources = location.resolve("src/main/java");

        //Add additional classes to the application
        integrationBuilder.getAdditionalClasses().forEach(cu -> {
            final String originalPackageName = cu.getPackageDeclaration().get().getNameAsString();
            cu.setPackageDeclaration(TestConfiguration.appGroupId());
            final String packageName = cu.getPackageDeclaration().get().getNameAsString();
            final String typeName = cu.getPrimaryTypeName().get();

            final Path packageFolder = sources.resolve(packageName.replace(".", "/"));
            packageFolder.toFile().mkdirs();
            final Path fileName = packageFolder.resolve(typeName + ".java");
            //fully qualified class name
            final String fqn = packageName + "." + typeName;
            LOG.info("Adding class '{}' to application as class '{}'", originalPackageName + "." + typeName, fqn);
            IOUtils.writeFile(fileName, cu.toString());
            //If the class is in the same package, it needs to be imported explicitly
            final ImportDeclaration importDeclaration =
                new ImportDeclaration(fqn, false, false);
            if (!integrationBuilder.getRouteBuilder().getImports().contains(importDeclaration)) {
                integrationBuilder.getRouteBuilder().addImport(importDeclaration);
            }
        });

        final IntegrationData integrationData = create(integrationBuilder);

        Path applicationPropertiesPath = location.resolve("src/main/resources/application.properties");
        String applicationPropertiesContent = PropertiesUtils.toString(integrationBuilder.getAppProperties());
        LOG.debug("Application properties:\n{}", applicationPropertiesContent);
        try {
            // Properties#store() escapes stuff by default, so construct the property file manually
            Files.write(applicationPropertiesPath, applicationPropertiesContent.getBytes(), StandardOpenOption.APPEND, StandardOpenOption.CREATE);
        } catch (IOException e) {
            throw new RuntimeException("Unable to write application.properties: ", e);
        }

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
        if (TestConfiguration.product() == ProductType.CAMEL_QUARKUS) {
            integrationBuilder.addCustomizer(new ApplicationScopeCustomizer());
        }

        if (TestConfiguration.product() == ProductType.CAMEL_K) {
            integrationBuilder.addCustomizer(new ModelineCustomizer());
        }

        for (Customizer customizer : integrationBuilder.getCustomizers()) {
            customizer.setIntegrationBuilder(integrationBuilder);
            customizer.customize();
        }

        LOG.debug("Integration class:\n{}", integrationBuilder.getRouteBuilder().toString());

        return new IntegrationData(integrationBuilder.getRouteBuilder().toString(), integrationBuilder.getAppProperties());
    }
}
