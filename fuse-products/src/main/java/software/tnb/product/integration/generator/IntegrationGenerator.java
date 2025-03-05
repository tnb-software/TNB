package software.tnb.product.integration.generator;

import software.tnb.common.config.TestConfiguration;
import software.tnb.common.utils.IOUtils;
import software.tnb.common.utils.PropertiesUtils;
import software.tnb.product.cq.utils.ApplicationScopeCustomizer;
import software.tnb.product.csb.customizer.CamelMainCustomizer;
import software.tnb.product.csb.customizer.ComponentCustomizer;
import software.tnb.product.customizer.Customizer;
import software.tnb.product.customizer.Customizers;
import software.tnb.product.customizer.app.HTTPServerPortCustomizer;
import software.tnb.product.customizer.byteman.BytemanCustomizer;
import software.tnb.product.integration.Resource;
import software.tnb.product.integration.builder.AbstractIntegrationBuilder;
import software.tnb.product.util.RemoveQuarkusAnnotationsCustomizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.PackageDeclaration;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.stream.Collectors;

/**
 * Dumps the IntegrationBuilder class into a given object.
 */
public final class IntegrationGenerator {
    private static final Logger LOG = LoggerFactory.getLogger(IntegrationGenerator.class);

    private IntegrationGenerator() {
    }

    /**
     * Creates the given resource inside the given path.
     * @param resourcesPath resources directory path
     * @param resource resource to create
     */
    private static void createResource(Path resourcesPath, Resource resource) {
        if (resource.isContentPath()) {
            IOUtils.copyFile(Paths.get(resource.getContent()), resourcesPath.resolve(resource.getName()));
        } else {
            IOUtils.writeFile(resourcesPath.resolve(resource.getName()), resource.getContent());
        }
    }

    /**
     * Creates the integration app directory.
     * @param integrationBuilder integration builder instance
     * @return app directory
     */
    public static Path createApplicationDirectory(AbstractIntegrationBuilder<?> integrationBuilder) {
        TestConfiguration.appLocation().resolve(integrationBuilder.getIntegrationName()).toFile().mkdirs();
        return TestConfiguration.appLocation().resolve(integrationBuilder.getIntegrationName());
    }

    /**
     * Creates all necessary files based on the integration builder setup.
     *
     * @param integrationBuilder integration builder instance
     * @param appDir location of the root of the app
     */
    public static void createFiles(AbstractIntegrationBuilder<?> integrationBuilder, Path appDir) {
        processCustomizers(integrationBuilder);

        createResourceFiles(integrationBuilder, appDir);

        createAdditionalClasses(integrationBuilder, appDir);

        createApplicationProperties(integrationBuilder, appDir);

        createIntegrationClass(integrationBuilder, appDir);
    }

    /**
     * Processes the customizers defined in the integration builder.
     * @param integrationBuilder integration builder
     */
    public static void processCustomizers(AbstractIntegrationBuilder<?> integrationBuilder) {
        if (!integrationBuilder.getResources().isEmpty()) {
            integrationBuilder.addCustomizer(Customizers.QUARKUS.customize(i ->
                    i.addToApplicationProperties("quarkus.native.resources.includes",
                        integrationBuilder.getResources().stream().map(Resource::getName).collect(Collectors.joining(","))
                    )
                )
            );
        }

        integrationBuilder.addCustomizer(
            new ApplicationScopeCustomizer(),
            new RemoveQuarkusAnnotationsCustomizer(),
            new ComponentCustomizer(),
            new CamelMainCustomizer(),
            new BytemanCustomizer()
        );

        // the http server port customizer should be added by default, however, if there is any other instance of that class already added
        // (by using .port(port, boolean) method), don't add another one
        if (integrationBuilder.getCustomizers().stream().noneMatch(c -> c instanceof HTTPServerPortCustomizer)) {
            integrationBuilder.addCustomizer(new HTTPServerPortCustomizer(true));
        }

        // Reverse the order of customizers, as the user-defined should take precedence over the default ones
        Collections.reverse(integrationBuilder.getCustomizers());

        for (Customizer customizer : integrationBuilder.getCustomizers()) {
            customizer.setIntegrationBuilder(integrationBuilder);
            customizer.doCustomize();
        }
    }

    /**
     * Creates the resource files defined in the integration builder.
     * @param integrationBuilder integration builder
     * @param appDir app directory
     */
    public static void createResourceFiles(AbstractIntegrationBuilder<?> integrationBuilder, Path appDir) {
        final Path resourcesPath = appDir.resolve("src/main/resources");
        integrationBuilder.getResources().forEach(resource -> createResource(resourcesPath, resource));
    }

    /**
     * Creates the additional classes defined in the integration builder.
     * @param integrationBuilder integration builder
     * @param appDir app directory
     */
    public static void createAdditionalClasses(AbstractIntegrationBuilder<?> integrationBuilder, Path appDir) {
        // For Camel JBang, place all additional files in the app root dir alongside the routebuilder class
        final Path sources = integrationBuilder.isJBang() ? appDir : appDir.resolve("src/main/java");

        // Add additional classes to the application
        integrationBuilder.getAdditionalClasses().forEach(cu -> {
            final String typeName = cu.getPrimaryTypeName().orElse(cu.getType(0).getNameAsString());
            final Path fileName;
            final String packageName = cu.getPackageDeclaration().get().getNameAsString();
            final String fqn = packageName + "." + typeName;

            if (integrationBuilder.isJBang()) {
                fileName = sources.resolve(typeName + ".java");
            } else {
                final Path packageFolder = sources.resolve(packageName.replace(".", "/"));
                packageFolder.toFile().mkdirs();
                fileName = packageFolder.resolve(typeName + ".java");
            }
            integrationBuilder.getRouteBuilder().ifPresent(rb -> {
                //If the class isn't in the same package, it needs to be imported explicitly
                rb.addImport(new ImportDeclaration(fqn, false, false));
            });
            LOG.info("Adding class '{}' to application as class '{}'", packageName + "." + typeName, fqn);
            IOUtils.writeFile(fileName, cu.toString());
        });
    }

    /**
     * Creates the application.properties file.
     * @param integrationBuilder integration builder
     * @param appDir app directory
     */
    public static void createApplicationProperties(AbstractIntegrationBuilder<?> integrationBuilder, Path appDir) {
        appDir = integrationBuilder.isJBang() ? appDir : appDir.resolve("src/main/resources");
        Path propertiesFile = appDir.resolve("application.properties");

        if (propertiesFile.toFile().exists()) {
            // If the file already exists, append newline first in case the file does not end with a newline
            IOUtils.appendFile(propertiesFile, "\n");
        }

        String content = PropertiesUtils.toString(integrationBuilder.getApplicationProperties());
        if (!content.trim().isEmpty()) {
            LOG.debug("Application properties:\n{}", content);
            // Properties#store() escapes stuff by default, so construct the property file manually
            IOUtils.appendFile(propertiesFile, content);
        }
    }

    /**
     * Creates the integration class file.
     * @param integrationBuilder integration builder
     * @param appDir app directory
     */
    public static void createIntegrationClass(AbstractIntegrationBuilder<?> integrationBuilder, Path appDir) {
        final Path sources = integrationBuilder.isJBang() ? appDir : appDir.resolve("src/main/java");

        integrationBuilder.getRouteBuilder().ifPresent(rb -> {
            final Path destination;
            if (integrationBuilder.isJBang()) {
                destination = sources;
            } else {
                final PackageDeclaration packageDeclaration = rb.getPackageDeclaration().get();
                destination = sources.resolve(packageDeclaration.getName().asString().replace(".", "/"));
                destination.toFile().mkdirs();
            }
            String integrationClass = rb.toString();
            LOG.debug("Integration class:\n{}", integrationClass);
            IOUtils.writeFile(destination.resolve(integrationBuilder.getFileName()), integrationClass);
        });
    }
}
