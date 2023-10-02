package software.tnb.product.integration.generator;

import software.tnb.common.config.TestConfiguration;
import software.tnb.common.utils.IOUtils;
import software.tnb.common.utils.PropertiesUtils;
import software.tnb.product.ck.customizer.DependenciesToModelineCustomizer;
import software.tnb.product.ck.customizer.TraitCustomizer;
import software.tnb.product.ck.integration.builder.CamelKIntegrationBuilder;
import software.tnb.product.cq.utils.ApplicationScopeCustomizer;
import software.tnb.product.csb.customizer.CamelMainCustomizer;
import software.tnb.product.csb.customizer.ComponentCustomizer;
import software.tnb.product.customizer.Customizer;
import software.tnb.product.customizer.Customizers;
import software.tnb.product.integration.Resource;
import software.tnb.product.integration.builder.AbstractIntegrationBuilder;
import software.tnb.product.util.InlineCustomizer;
import software.tnb.product.util.RemoveQuarkusAnnotationsCustomizer;

import org.apache.camel.v1.integrationspec.traits.Container;
import org.apache.camel.v1.integrationspec.traits.Logging;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.stream.Collectors;

/**
 * Dumps the IntegrationBuilder class into a given object.
 */
public final class IntegrationGenerator {
    private static final Logger LOG = LoggerFactory.getLogger(IntegrationGenerator.class);

    private IntegrationGenerator() {
    }

    private static void doWriteResource(Path resourcesPath, Resource resource) {
        if (resource.getIsContentPath()) {
            IOUtils.copyFile(Paths.get(resource.getContent()), resourcesPath.resolve(resource.getName()));
        } else {
            IOUtils.writeFile(resourcesPath.resolve(resource.getName()), resource.getContent());
        }
    }

    /**
     * Dumps the integration class into a file.
     *
     * @param integrationBuilder integration builder instance
     * @param location location of the root of the app
     */
    public static void toFile(AbstractIntegrationBuilder<?> integrationBuilder, Path location) {
        final Path sources = location.resolve("src/main/java");

        if (!integrationBuilder.getResources().isEmpty()) {
            integrationBuilder.addCustomizer(Customizers.QUARKUS.customize(i ->
                    i.addToProperties("quarkus.native.resources.includes",
                        integrationBuilder.getResources().stream().map(Resource::getName).collect(Collectors.joining(","))
                    )
                )
            );
        }

        processCustomizers(integrationBuilder);
        // Add additional resources to the application
        final Path resourcesPath = location.resolve("src/main/resources");
        integrationBuilder.getResources().forEach(resource -> doWriteResource(resourcesPath, resource));

        //Add additional classes to the application
        integrationBuilder.getAdditionalClasses().forEach(cu -> {
            final String packageName = cu.getPackageDeclaration().get().getNameAsString();
            final String typeName = cu.getPrimaryTypeName().orElse(cu.getType(0).getNameAsString());

            final Path packageFolder = sources.resolve(packageName.replace(".", "/"));
            packageFolder.toFile().mkdirs();
            final Path fileName = packageFolder.resolve(typeName + ".java");
            //fully qualified class name
            final String fqn = packageName + "." + typeName;
            LOG.info("Adding class '{}' to application as class '{}'", packageName + "." + typeName, fqn);
            IOUtils.writeFile(fileName, cu.toString());

            integrationBuilder.getRouteBuilder().ifPresent(rb -> {
                //If the class isn't in the same package, it needs to be imported explicitly
                rb.addImport(new ImportDeclaration(fqn, false, false));
            });
        });

        Path applicationPropertiesPath = location.resolve("src/main/resources/application.properties");
        String applicationPropertiesContent = PropertiesUtils.toString(integrationBuilder.getProperties());
        if (!applicationPropertiesContent.trim().isEmpty()) {
            LOG.debug("Application properties:\n{}", applicationPropertiesContent);
            try {
                // Properties#store() escapes stuff by default, so construct the property file manually
                Files.write(applicationPropertiesPath, applicationPropertiesContent.getBytes(), StandardOpenOption.APPEND, StandardOpenOption.CREATE);
            } catch (IOException e) {
                throw new RuntimeException("Unable to write application.properties: ", e);
            }
        }

        integrationBuilder.getRouteBuilder().ifPresent(rb -> {
            final PackageDeclaration packageDeclaration = rb.getPackageDeclaration().get();
            final Path destination = sources.resolve(packageDeclaration.getName().asString().replace(".", "/"));
            destination.toFile().mkdirs();
            String integrationClass = rb.toString();
            LOG.debug("Integration class:\n{}", integrationClass);
            IOUtils.writeFile(destination.resolve(integrationBuilder.getFileName()), integrationClass);
        });
    }

    /**
     * Processes the customizers and returns the resulting class as a String.
     *
     * @param integrationBuilder integration builder instance
     * @return integration class as a String
     */
    public static String toString(AbstractIntegrationBuilder<?> integrationBuilder) {
        String result;

        processCustomizers(integrationBuilder);

        if (integrationBuilder.getRouteBuilder().isPresent()) {
            //Inline classes to the routebuilder
            integrationBuilder.getAdditionalClasses().forEach(cu -> {
                //newClass is an empty class added to the route builder
                final TypeDeclaration<?> routeBuilder = integrationBuilder.getRouteBuilder().get()
                    .getClassByName(AbstractIntegrationBuilder.ROUTE_BUILDER_NAME).get();
                //sourceClass is modified to be inlinable
                final TypeDeclaration<?> sourceClass = cu.getPrimaryType().orElseGet(() -> cu.getType(0));
                sourceClass.setPublic(true);
                sourceClass.addModifier(Modifier.Keyword.STATIC);

                routeBuilder.addMember(sourceClass);
                //merge imports from the source class
                for (ImportDeclaration imp : cu.getImports()) {
                    integrationBuilder.getRouteBuilder().get().addImport(imp.getNameAsString());
                }
                //Remove the import to the class itself (won't be present in the generated app)
                integrationBuilder.getRouteBuilder().get().getImports().removeIf(
                    imp -> imp.getNameAsString().equals(cu.getPackageDeclaration().get().getNameAsString() + "." + sourceClass.getNameAsString()));
            });

            String applicationPropertiesContent = PropertiesUtils.toString(integrationBuilder.getProperties());
            if (!applicationPropertiesContent.trim().isEmpty()) {
                LOG.debug("Application properties:\n{}", applicationPropertiesContent);
            }
            result = integrationBuilder.getRouteBuilder().get().toString();
        } else if (integrationBuilder instanceof CamelKIntegrationBuilder) {
            // RouteBuilder is null, so we should have integration directly in string (quickstarts case)
            CamelKIntegrationBuilder ckib = (CamelKIntegrationBuilder) integrationBuilder;
            if (ckib.getContent() == null) {
                throw new IllegalArgumentException("Missing either RouteBuilder class or String source content in IntegrationBuilder class");
            }
            result = ckib.getContent();
        } else {
            throw new IllegalArgumentException("You need to specify either integration content or RouteBuilder");
        }

        LOG.debug("Integration class:\n{}", result);
        return result;
    }

    private static void processCustomizers(AbstractIntegrationBuilder<?> integrationBuilder) {
        integrationBuilder.addCustomizer(
            new ApplicationScopeCustomizer(),
            new RemoveQuarkusAnnotationsCustomizer(),
            new DependenciesToModelineCustomizer(),
            new InlineCustomizer(),
            new ComponentCustomizer(),
            new CamelMainCustomizer(),
            // for camel-k expose the configured port using trait, so that app.getEndpoint() can be used
            new TraitCustomizer(traits -> {
                Container container = traits.getContainer() == null ? new Container() : traits.getContainer();
                container.setServicePort((long) integrationBuilder.getPort());
                traits.setContainer(container);
            })
        );

        if (TestConfiguration.streamLogs()) {
            // Disable color for camel-k integration, as it breaks the log4j color configuration
            integrationBuilder.addCustomizer(new TraitCustomizer(traits -> {
                Logging logging = traits.getLogging() == null ? new Logging() : traits.getLogging();
                logging.setColor(false);
                traits.setLogging(logging);
            }));
        }

        for (Customizer customizer : integrationBuilder.getCustomizers()) {
            customizer.setIntegrationBuilder(integrationBuilder);
            customizer.doCustomize();
        }
    }
}
