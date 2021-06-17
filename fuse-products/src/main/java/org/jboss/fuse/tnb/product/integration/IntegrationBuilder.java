package org.jboss.fuse.tnb.product.integration;

import org.jboss.fuse.tnb.common.config.TestConfiguration;
import org.jboss.fuse.tnb.common.product.ProductType;
import org.jboss.fuse.tnb.customizer.Customizer;
import org.jboss.fuse.tnb.product.util.InlineCustomizer;

import org.apache.camel.builder.RouteBuilder;
import org.apache.commons.lang3.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * Wrapper around creating integrations.
 */
public class IntegrationBuilder {
    private String integrationName;

    private static final Logger LOG = LoggerFactory.getLogger(IntegrationBuilder.class);

    private static final Set<String> IGNORED_PACKAGES = Set.of("org.jboss.fuse.tnb", "org.junit");

    private final List<String> dependencies = new ArrayList<>();
    private final List<Customizer> customizers = new ArrayList<>();
    private CompilationUnit routeBuilder;
    private Properties appProperties = new Properties();

    final String basePackage = TestConfiguration.appGroupId();

    public IntegrationBuilder(String name) {
        this.integrationName = name;
        //replicate user behavior by specifying route endpoints as hardcoded strings
        //camel-k needs this to register components automagically
        addCustomizer(new InlineCustomizer());
    }

    public IntegrationBuilder fromRouteBuilder(RouteBuilder routeBuilder) {
        Class<?> clazz = routeBuilder.getClass();
        //If class is nested we need to find the top-most parent
        while (clazz.getEnclosingClass() != null) {
            clazz = clazz.getEnclosingClass();
        }
        SourceRoot sourceRoot = getSourceRoot(clazz);
        String className = getClassName(clazz);
        CompilationUnit cu = sourceRoot.parse(clazz.getPackageName(), className + ".java");

        if (routeBuilder.getClass().getEnclosingClass() != null) {
            //Find the nested class, should really be only one class since they all should have unique names
            final List<ClassOrInterfaceDeclaration> classList =
                cu.getLocalDeclarationFromClassname(getClassName(routeBuilder.getClass()));
            if (!classList.isEmpty()) {
                final ClassOrInterfaceDeclaration decl = classList.get(0);
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
        cu.setPackageDeclaration(basePackage);
        LOG.debug("Adding RouteBuilder class: {} to the application", className);
        this.routeBuilder = cu;
        return this;
    }

    public IntegrationBuilder addToApplicationProperties(String key, String value) {
        appProperties.setProperty(key, value);
        return this;
    }

    public IntegrationBuilder addToApplicationProperties(ProductType forType, String key, String value) {
        if (forType == TestConfiguration.product()) {
            addToApplicationProperties(key, value);
        }
        return this;
    }

    private SourceRoot getSourceRoot(Class<?> clazz) {
        ProjectRoot projectRoot = new ParserCollectionStrategy().collect(CodeGenerationUtils.mavenModuleRoot(clazz));
        return projectRoot.getSourceRoots().stream().filter(sr -> !sr.getRoot().toString().contains("target")).findFirst().get();
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
            decl.setName("MyRouteBuilder");
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
        if (value instanceof String) {
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

    public String getIntegrationName() {
        return integrationName;
    }

    public List<String> getDependencies() {
        return dependencies;
    }

    public CompilationUnit getRouteBuilder() {
        return routeBuilder;
    }

    public Properties getAppProperties() {
        return appProperties;
    }

    public List<Customizer> getCustomizers() {
        return customizers;
    }
}
