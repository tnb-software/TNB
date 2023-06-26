package software.tnb.product.integration.builder;

import software.tnb.common.config.TestConfiguration;
import software.tnb.common.utils.MapUtils;
import software.tnb.product.customizer.Customizer;
import software.tnb.product.deploystrategy.impl.custom.OpenshiftCustomDeployer;
import software.tnb.product.integration.Resource;
import software.tnb.product.util.maven.Maven;

import org.apache.camel.builder.RouteBuilder;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Plugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.javaparser.JavaParser;
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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Wrapper around creating integrations.
 */
public abstract class AbstractIntegrationBuilder<SELF extends AbstractIntegrationBuilder<SELF>> {
    public static final String ROUTE_BUILDER_NAME = "MyRouteBuilder";
    public static final String ROUTE_BUILDER_METHOD_NAME = "configure";

    private static final Logger LOG = LoggerFactory.getLogger(AbstractIntegrationBuilder.class);
    private static final Set<String> IGNORED_PACKAGES = Set.of("software.tnb", "org.junit", "io.fabric8");
    private static final String BASE_PACKAGE = TestConfiguration.appGroupId();

    private final List<Dependency> dependencies = new ArrayList<>();
    private final List<Plugin> plugins = new ArrayList<>();
    private final List<Customizer> customizers = new ArrayList<>();
    private final List<CompilationUnit> classesToAdd = new ArrayList<>();
    private final List<Resource> resources = new ArrayList<>();
    private final Properties properties = new Properties();

    private CompilationUnit routeBuilder;
    private String integrationName;
    private String fileName = "MyRouteBuilder.java";

    private int port = 8080;

    private String jvmAgentPath;

    private OpenshiftCustomDeployer customStrategy;

    public AbstractIntegrationBuilder(String name) {
        this.integrationName = name;
    }

    protected abstract SELF self();

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

    public SELF fromRouteBuilder(RouteBuilder routeBuilder) {
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
        return self();
    }

    /**
     * Add classpath resource (represented by string path) into integration and the resource type
     *
     * @param resource path to the resource
     * @return this
     */
    public SELF addClasspathResource(String resource) {
        String resourceData;
        try (InputStream is = this.getClass().getClassLoader().getResourceAsStream(resource)) {
            resourceData = IOUtils.toString(is, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Unable to read resource: ", e);
        }
        addResource(new Resource(resource, resourceData));
        return self();
    }

    public SELF addResource(Resource resource) {
        resources.add(resource);
        return self();
    }

    public SELF addToProperties(String key, String value) {
        return addToProperties(MapUtils.toProperties(Map.of(key, value)));
    }

    public SELF addToProperties(Map<String, String> properties) {
        return addToProperties(MapUtils.toProperties(properties));
    }

    public SELF addToProperties(Properties properties) {
        properties.forEach((key, value) -> this.properties.setProperty(key.toString(), value.toString()));
        return self();
    }

    public SELF addToProperties(InputStream inputStream) {
        try (inputStream) {
            final Properties p = new Properties();
            p.load(inputStream);
            addToProperties(p);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return self();
    }

    public SELF addToProperties(File pFile) {
        try (InputStream is = new FileInputStream(pFile)) {
            addToProperties(is);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return self();
    }

    private Stream<SourceRoot> getSourceRoots(Class<?> clazz) {
        ProjectRoot projectRoot = new ParserCollectionStrategy().collect(CodeGenerationUtils.mavenModuleRoot(clazz));
        return projectRoot.getSourceRoots().stream().filter(sr -> !sr.getRoot().toString().contains("target"));
    }

    public CompilationUnit getCompilationUnit(Class<?> clazz) {
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
     * Adds dependencies to resulting projects / classes. If a dependency contains a colon, it's assumed that it is a form of GA[V] and it is used
     * as is, otherwise it is assumed that it is a camel dependency and it is prepended with "camel-" or "camel-quarkus-"
     *
     * @param dependencies dependencies to add
     * @return this
     */
    public SELF dependencies(String... dependencies) {
        this.dependencies.addAll(Arrays.stream(dependencies).map(Maven::createDependency).collect(Collectors.toList()));
        return self();
    }

    public SELF dependencies(Dependency... dependencies) {
        this.dependencies.addAll(Arrays.asList(dependencies));
        return self();
    }

    public SELF addPlugin(Plugin p, Plugin... others) {
        plugins.add(p);
        if (others != null) {
            plugins.addAll(Arrays.asList(others));
        }
        return self();
    }

    public SELF name(String name) {
        this.integrationName = name;
        return self();
    }

    public SELF addCustomizer(Customizer c, Customizer... others) {
        customizers.add(c);
        if (others != null) {
            customizers.addAll(Arrays.asList(others));
        }
        return self();
    }

    public SELF port(int port) {
        this.port = port;
        return self();
    }

    /**
     * Adds class source to the application code. The sources must be available, so this will work only for classes inside tnb-tests.
     *
     * @param clazz class to be added to the application
     * @return this
     */
    public SELF addClass(Class<?> clazz) {
        return addClass(getCompilationUnit(clazz));
    }

    /**
     * Adds class source to the application code. This can be used for classes outside of the maven project.
     *
     * @param cu compilation unit that represents the class to be added
     * @return this
     */
    public SELF addClass(CompilationUnit cu) {
        classesToAdd.add(cu);
        return self();
    }

    /**
     * Loads the class from given path into a CompilationUnit.
     * <p>
     * If the path is a directory, all java files in the directory are added recursively.
     *
     * @param file path to the file
     * @return this
     */
    public SELF addClass(Path file) {
        if (file.toFile().isDirectory()) {
            try (Stream<Path> files = Files.walk(file)) {
                files.filter(f -> f.toString().toLowerCase().endsWith(".java")).forEach(this::addFile);
            } catch (IOException e) {
                throw new RuntimeException("Unable to walk files in " + file.toAbsolutePath(), e);
            }
        } else {
            addFile(file);
        }
        return self();
    }

    private void addFile(Path file) {
        try {
            classesToAdd.add(new JavaParser().parse(file).getResult().get());
        } catch (IOException e) {
            throw new RuntimeException("Unable to parse file " + file, e);
        }
    }

    public SELF fileName(String fileName) {
        this.fileName = fileName;
        return self();
    }

    public String getFileName() {
        return fileName;
    }

    public List<CompilationUnit> getAdditionalClasses() {
        return this.classesToAdd;
    }

    public String getIntegrationName() {
        return integrationName;
    }

    public List<Dependency> getDependencies() {
        return dependencies;
    }

    public List<Plugin> getPlugins() {
        return plugins;
    }

    /**
     * Gets the route builder compilation unit.
     * <p>
     * The RB may be null in case the integration is loaded from string (Camel-K), or from XML file (CSB)
     *
     * @return optional
     */
    public Optional<CompilationUnit> getRouteBuilder() {
        return Optional.ofNullable(routeBuilder);
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

    public int getPort() {
        return port;
    }

    /**
     * Add command parameter -javaagent:path/to/jvmagent.jar
     * @param jvmAgentPath String, the path of the agent
     * @return SELF
     */
    public SELF jvmAgentPath(String jvmAgentPath) {
        this.jvmAgentPath = jvmAgentPath;
        return self();
    }

    public String getJvmAgentPath() {
        return jvmAgentPath;
    }

    public SELF useOcpCustomStrategy(OpenshiftCustomDeployer customStrategy) {
        this.customStrategy = customStrategy;
        return self();
    }

    public OpenshiftCustomDeployer getOCPCustomStrategy() {
        return customStrategy;
    }
}
