package org.jboss.fuse.tnb.product.integration;

import org.jboss.fuse.tnb.common.config.TestConfiguration;
import org.jboss.fuse.tnb.common.product.ProductType;

import org.apache.camel.builder.RouteBuilder;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.javaparser.ParseProblemException;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.visitor.ModifierVisitor;
import com.github.javaparser.utils.CodeGenerationUtils;
import com.github.javaparser.utils.ParserCollectionStrategy;
import com.github.javaparser.utils.ProjectRoot;
import com.github.javaparser.utils.SourceRoot;
import com.github.javaparser.utils.StringEscapeUtils;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.fabric8.kubernetes.api.model.Secret;

/**
 * Wrapper around creating integrations.
 */
public class IntegrationBuilder {
    public static final String ROUTE_BUILDER_NAME = "MyRouteBuilder";
    private static final Logger LOG = LoggerFactory.getLogger(IntegrationBuilder.class);
    private static final Set<String> IGNORED_PACKAGES = Set.of("org.jboss.fuse.tnb", "org.junit");
    private static final String BASE_PACKAGE = TestConfiguration.appGroupId();

    private final List<String> dependencies = new ArrayList<>();
    private final List<Customizer> customizers = new ArrayList<>();
    private final List<CompilationUnit> classesToAdd = new ArrayList<>();
    private final List<Resource> resources = new ArrayList<>();
    private final Properties properties = new Properties();

    private CompilationUnit routeBuilder;
    private String integrationName;
    private String sourceName = "MyRouteBuilder.java";
    private String sourceContent;
    private String secret;

    public IntegrationBuilder(String name) {
        this.integrationName = name;
    }

    public IntegrationBuilder fromRouteBuilder(RouteBuilder routeBuilder) {
        Class<?> clazz = routeBuilder.getClass();
        //If class is nested we need to find the top-most parent
        while (clazz.getEnclosingClass() != null) {
            clazz = clazz.getEnclosingClass();
        }
        CompilationUnit cu = getCompilationUnit(clazz);
        String className = getClassName(clazz);

        if (routeBuilder.getClass().getEnclosingClass() != null) {
            //Find the nested class, should really be only one class since they all should have unique names
            final List<ClassOrInterfaceDeclaration> classList =
                cu.getLocalDeclarationFromClassname(getClassName(routeBuilder.getClass()));
            if (!classList.isEmpty()) {
                final ClassOrInterfaceDeclaration decl = classList.get(0);
                if (decl.isStatic()) {
                    decl.setStatic(false);
                }

                String code = decl.toString();

                final CompilationUnit nestedCu = StaticJavaParser.parse(decl.toString());

                //If parent's CompilationUnits imports are used in RouteBuilder add them to the new Compilation unit
                cu.getImports().stream().filter(imp -> code.contains(getClassName(imp.getNameAsString()))).forEach(nestedCu::addImport);

                //Use the new compilation unit as the routebuilder
                cu = nestedCu;
                className = getClassName(routeBuilder.getClass());
            }
        }
        processRouteBuilder(routeBuilder, className, cu);
        cu.setPackageDeclaration(BASE_PACKAGE);
        LOG.debug("Adding RouteBuilder class: {} to the application", className);
        this.routeBuilder = cu;
        return this;
    }

    /**
     * Add classpath resource (represented by string path) into integration and the resource type
     *
     * @param resource path to the resource
     * @param type resource type
     * @return this
     */
    public IntegrationBuilder addResource(ResourceType type, String resource) {
        String resourceData;
        try (InputStream is = this.getClass().getClassLoader().getResourceAsStream(resource)) {
            resourceData = IOUtils.toString(is, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Unable to read resource: ", e);
        }
        addResource(new Resource(type, resource, resourceData));
        return this;
    }

    public IntegrationBuilder addResource(Resource resource) {
        resources.add(resource);
        return this;
    }

    public IntegrationBuilder addResource(String resource) {
        addResource(ResourceType.DATA, resource);
        return this;
    }

    public IntegrationBuilder addToProperties(String key, String value) {
        properties.setProperty(key, value);
        return this;
    }

    public IntegrationBuilder addToProperties(Properties properties) {
        properties.forEach((key, value) -> this.properties.setProperty(key.toString(), value.toString()));
        return this;
    }

    public IntegrationBuilder addToProperties(ProductType forType, String key, String value) {
        if (forType == TestConfiguration.product()) {
            addToProperties(key, value);
        }
        return this;
    }

    private Stream<SourceRoot> getSourceRoots(Class<?> clazz) {
        ProjectRoot projectRoot = new ParserCollectionStrategy().collect(CodeGenerationUtils.mavenModuleRoot(clazz));
        return projectRoot.getSourceRoots().stream().filter(sr -> !sr.getRoot().toString().contains("target"));
    }

    private CompilationUnit getCompilationUnit(Class<?> clazz) {
        return getSourceRoots(clazz).map(sr -> {
                try {
                    return sr.parse(clazz.getPackageName(), getClassName(clazz) + ".java");
                } catch (ParseProblemException ex) {
                    return null;
                }
            }).filter(Objects::nonNull).findFirst()
            .orElseThrow(() -> new RuntimeException(String
                .format("Couldn't parse class %s in source roots [%s]. Make sure the sources are available.", clazz.getName(),
                    getSourceRoots(clazz).collect(
                        Collectors.toList()))));
    }

    ///Get TypeName of the class ie. tnb.tests.SlackTestIt$RouteBuilder -> RouteBuilder
    private static String getClassName(String className) {
        final String typeName = className.substring(className.lastIndexOf(".") + 1);
        if (typeName.contains("$")) {
            return typeName.substring(typeName.lastIndexOf("$") + 1);
        }
        return typeName;
    }

    ///Get TypeName of the class ie. tnb.tests.SlackTestIt$RouteBuilder -> RouteBuilder
    private static String getClassName(Class<?> clazz) {
        return getClassName(clazz.getName());
    }

    private void processRouteBuilder(RouteBuilder routeBuilder, String className, CompilationUnit cu) {
        cu.getClassByName(className).ifPresent(decl -> {
            //Remove all constructors with parameters
            decl.getConstructors().forEach(cdecl -> {
                if (cdecl.getParameters().isNonEmpty()) {
                    decl.remove(cdecl);
                }
            });
            //Preprocess all final fields
            decl.getFields().forEach(fieldDecl -> {
                if (fieldDecl.isFinal()) {
                    fieldDecl.getVariables().forEach(varDecl -> {
                        try {
                            String fieldName = fieldDecl.getVariable(0).getNameAsString();
                            final Field field = routeBuilder.getClass().getDeclaredField(fieldName);
                            field.setAccessible(true);
                            final Object value = field.get(routeBuilder);
                            String expression = getExpressionCode(value);
                            varDecl.setInitializer(expression);
                        } catch (Exception e) {
                            throw new RuntimeException("Failed to find/process route builder class: " + e.getMessage());
                        }
                    });
                }
            });
            if (!decl.isPublic()) {
                //Camel needs the class to be public
                decl.setPublic(true);
            }
            //Rewrite the original MyRouteBuilder class from the archetypes
            decl.setName(ROUTE_BUILDER_NAME);
            processImports(cu);
        });
    }

    /**
     * Returns code that should replicate the expression.
     *
     * @param value the runtime value from the routebuilder
     * @return code that will create the same values in the app code
     */
    private String getExpressionCode(Object value) {
        if (value == null) {
            return "null";
        } else if (value instanceof String) {
            //Escape escaped characters so javaparser can compile and unescape them 
            return "\"" + StringEscapeUtils.escapeJava((String) value) + "\"";
        } else if (value.getClass().isArray()) {
            StringBuilder expressionBuilder = new StringBuilder();
            expressionBuilder.append("new ").append(value.getClass().getTypeName()).append("{");
            for (int i = 0; i < Array.getLength(value); i++) {
                //get string representation for each value
                expressionBuilder.append(getExpressionCode(Array.get(value, i))).append(",");
            }
            expressionBuilder.append("}");
            return expressionBuilder.toString();
        } else if (ClassUtils.isPrimitiveOrWrapper(value.getClass())) {
            //is primitive, or boxed Primitive
            return value.toString();
        } else if (value instanceof Class) {
            return ((Class<?>) value).getName() + ".class";
        } else {
            throw new RuntimeException("Can't process final field with type " + value.getClass().getName()
                + " please consider not making this field final if you don't want it processed");
        }
    }

    /**
     * Processes imports - right now any tnb related import has to be removed as the classes are not present in the generated application.
     */
    private static void processImports(CompilationUnit cu) {
        cu.accept(new ModifierVisitor<>() {
            @Override
            public Node visit(ImportDeclaration importDecl, Object arg) {
                super.visit(importDecl, arg);
                final String importClass = importDecl.getName().asString();
                //Remove internal classes
                if (IGNORED_PACKAGES.stream().anyMatch(importClass::startsWith)) {
                    return null;
                }
                return importDecl;
            }
        }, null);
    }

    /**
     * Adds dependencies to resulting projects / classes. If a dependency contains a colon, it's assumed that it is a form of GA[V] and it is used
     * as is, otherwise it is assumed that it is a camel dependency and it is prepended with "camel-" or "camel-quarkus-"
     *
     * @param dependencies dependencies to add
     * @return this
     */
    public IntegrationBuilder dependencies(String... dependencies) {
        this.dependencies.addAll(Arrays.asList(dependencies));
        return this;
    }

    public IntegrationBuilder name(String name) {
        this.integrationName = name;
        return this;
    }

    public IntegrationBuilder addCustomizer(Customizer c) {
        customizers.add(c);
        return this;
    }

    /**
     * Adds class source to the application code. The sources must be available, so this will work only for classes inside tnb-tests.
     *
     * @param clazz class to be added to the application
     * @return this
     */
    public IntegrationBuilder addClass(Class<?> clazz) {
        return addClass(getCompilationUnit(clazz));
    }

    /**
     * Adds class source to the application code. This can be used for classes outside of the maven project.
     *
     * @param cu compilation unit that represents the class to be added
     * @return this
     */
    public IntegrationBuilder addClass(CompilationUnit cu) {
        classesToAdd.add(cu);
        return this;
    }

    public IntegrationBuilder sourceName(String sourceName) {
        this.sourceName = sourceName;
        return this;
    }

    public IntegrationBuilder fromString(String source) {
        this.sourceContent = source;
        return this;
    }

    public IntegrationBuilder secret(String secret) {
        this.secret = secret;
        return this;
    }

    public IntegrationBuilder secrets(Secret secret) {
        this.secret = secret.getMetadata().getName();
        return this;
    }

    public String getSecret() {
        return secret;
    }

    public String getSourceName() {
        return sourceName;
    }

    public String getSourceContent() {
        return sourceContent;
    }

    public List<CompilationUnit> getAdditionalClasses() {
        return this.classesToAdd;
    }

    public String getIntegrationName() {
        return integrationName;
    }

    public List<String> getDependencies() {
        return dependencies;
    }

    public CompilationUnit getRouteBuilder() {
        return routeBuilder;
    }

    public Properties getProperties() {
        return properties;
    }

    public List<Resource> getResources() {
        return resources;
    }

    public List<Customizer> getCustomizers() {
        return customizers;
    }
}
