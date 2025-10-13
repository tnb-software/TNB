package software.tnb.product.csb.application;

import software.tnb.common.config.OpenshiftConfiguration;
import software.tnb.common.config.TestConfiguration;
import software.tnb.common.utils.IOUtils;
import software.tnb.product.application.App;
import software.tnb.product.application.Phase;
import software.tnb.product.csb.configuration.SpringBootConfiguration;
import software.tnb.product.csb.integration.builder.SpringBootIntegrationBuilder;
import software.tnb.product.git.MavenGitRepository;
import software.tnb.product.integration.builder.AbstractGitIntegrationBuilder;
import software.tnb.product.integration.builder.AbstractIntegrationBuilder;
import software.tnb.product.integration.builder.AbstractMavenGitIntegrationBuilder;
import software.tnb.product.integration.generator.IntegrationGenerator;
import software.tnb.product.log.stream.LogStream;
import software.tnb.product.util.maven.BuildRequest;
import software.tnb.product.util.maven.Maven;

import org.apache.commons.io.FileUtils;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public abstract class SpringBootApp extends App {
    private static final Logger LOG = LoggerFactory.getLogger(SpringBootApp.class);
    protected MavenGitRepository mavenGitApp = null;

    private boolean shouldRun = true;

    @Override
    public boolean shouldRun() {
        return shouldRun;
    }

    public SpringBootApp(AbstractIntegrationBuilder<?> integrationBuilder) {
        super(integrationBuilder);

        // If there is an existing jar defined, don't create a new app
        if (getExistingJar() == null) {
            if (integrationBuilder instanceof AbstractGitIntegrationBuilder<?>
                && ((AbstractGitIntegrationBuilder<?>) integrationBuilder).getRepositoryUrl() != null) {
                mavenGitApp = new MavenGitRepository((AbstractMavenGitIntegrationBuilder<?>) integrationBuilder
                    , getName(), getLogPath(Phase.BUILD)
                    , ((AbstractMavenGitIntegrationBuilder<?>) integrationBuilder).buildProject());
                shouldRun = ((AbstractGitIntegrationBuilder<?>) integrationBuilder).runApplication();
            } else {
                if (integrationBuilder.isJBang()) {
                    createUsingJBang();
                } else {
                    createUsingMaven();
                }

                final Path basePath = TestConfiguration.appLocation().resolve(getName());

                removeExistingTests(basePath);

                keepWebLayerOnlyForOcp(OpenshiftConfiguration.isOpenshift(), basePath);

                IntegrationGenerator.createFiles(integrationBuilder, basePath);

                customizeMain(basePath);

                customizeDependencies(integrationBuilder.getDependencies());

                customizePlugins(integrationBuilder.getPlugins());

                BuildRequest.Builder requestBuilder = new BuildRequest.Builder()
                    .withBaseDirectory(basePath)
                    .withArgs("clean", "package")
                    .withProperties(Map.of(
                        "skipTests", "true"
                    ))
                    .withLogFile(getLogPath(Phase.BUILD))
                    .withLogMarker(LogStream.marker(getName(), Phase.BUILD));

                LOG.info("Building {} application project", getName());
                Maven.invoke(requestBuilder.build());
            }
        }
    }

    private void createUsingJBang() {
        List<String> arguments = new ArrayList<>(List.of(
            "--runtime", "spring-boot",
            "--build-property", "openshift-maven-plugin-version=" + SpringBootConfiguration.openshiftMavenPluginVersion(),
            // Align the generated CamelApplication class to correct package
            "--package-name", TestConfiguration.appGroupId(),
            "--camel-spring-boot-version", SpringBootConfiguration.camelSpringBootVersion(),
            "--spring-boot-version", SpringBootConfiguration.springBootVersion()
            ));
        super.createUsingJBang(arguments);
    }

    private void createUsingMaven() {
        LOG.info("Creating Camel SpringBoot application project for integration {}", getName());
        Map<String, String> properties = Map.of(
            "archetypeGroupId", SpringBootConfiguration.camelSpringBootArchetypeGroupId(),
            "archetypeArtifactId", SpringBootConfiguration.camelSpringBootArchetypeArtifactId(),
            "archetypeVersion", SpringBootConfiguration.camelSpringBootArchetypeVersion(),
            "groupId", TestConfiguration.appGroupId(),
            "artifactId", getName(),
            "version", TestConfiguration.appVersion(),
            "package", TestConfiguration.appGroupId(),
            "archetypeCatalog", "internal"
        );

        Maven.invoke(new BuildRequest.Builder()
            .withBaseDirectory(TestConfiguration.appLocation())
            .withArgs("archetype:generate")
            .withProperties(properties)
            .withLogFile(getLogPath(Phase.GENERATE))
            .withLogMarker(LogStream.marker(getName(), Phase.GENERATE))
            .build());
    }

    private void keepWebLayerOnlyForOcp(boolean isOpenShift, Path location) {
        final List<String> artifactsToRemove = new ArrayList<>(List.of("camel-stream-starter"));

        if (!isOpenShift) {
            artifactsToRemove.addAll(List.of("spring-boot-starter-web", "spring-boot-starter-undertow"
                , "spring-boot-starter-actuator"));
        }
        File pom = location.resolve("pom.xml").toFile();
        Model model = Maven.loadPom(pom);

        model.getDependencies().removeIf(d -> artifactsToRemove.contains(d.getArtifactId()));

        Maven.writePom(pom, model);
    }

    private void removeExistingTests(final Path location) {
        FileUtils.deleteQuietly(location.resolve(Path.of("src", "test")).toFile());
        final String[] packages = TestConfiguration.appGroupId().split("\\.");
        Path basePackagePath = location.resolve(Path.of("src", "main", "java"));
        for (String aPackage : packages) {
            basePackagePath = basePackagePath.resolve(aPackage);
        }
        final Path finalBasePackagePath = basePackagePath;
        Stream.of("MySpringBean.java", "MySpringBootRouter.java")
            .map(f -> finalBasePackagePath.resolve(f).toFile())
            .filter(File::exists)
            .forEach(File::delete);
    }

    protected Path getExistingJar() {
        return integrationBuilder instanceof SpringBootIntegrationBuilder
            ? ((SpringBootIntegrationBuilder) integrationBuilder).getExistingJar() : null;
    }

    private void customizeDependencies(List<Dependency> mavenDependencies) {
        File pom = TestConfiguration.appLocation().resolve(getName()).resolve("pom.xml").toFile();
        Model model = Maven.loadPom(pom);

        mavenDependencies.forEach(model.getDependencies()::add);

        Maven.writePom(pom, model);
    }

    private void customizeMain(Path location) {
        if (integrationBuilder instanceof SpringBootIntegrationBuilder) {
            addXmlResourceImport((SpringBootIntegrationBuilder) integrationBuilder, location);
        }
    }

    private void addXmlResourceImport(SpringBootIntegrationBuilder integrationBuilder, Path location) {
        final Path sources = location.resolve("src/main/java");
        final Path resourcesPath = location.resolve("src/main/resources");

        List<String> xmlImportFiles = new ArrayList<>();
        integrationBuilder.getXmlCamelContext().forEach(resource -> {
            IOUtils.writeFile(resourcesPath.resolve(resource.getName()), resource.getContent());
            xmlImportFiles.add("\"classpath:" + resource.getName() + "\"");
        });

        if (!xmlImportFiles.isEmpty()) {
            integrationBuilder.dependencies("org.apache.camel.springboot:camel-spring-boot-xml-starter");

            String springBootMainType = "MySpringBootApplication";
            String springBootMainClass = springBootMainType + ".java";
            try {
                CompilationUnit springBootMain = StaticJavaParser.parse(TestConfiguration.appLocation().resolve(
                    Path.of(integrationBuilder.getIntegrationName(), "src", "main", "java"
                        , TestConfiguration.appGroupId().replace(".", File.separator), springBootMainClass)));

                springBootMain.addImport("org.springframework.context.annotation.ImportResource");

                NormalAnnotationExpr importResourceAnnotation =
                    springBootMain.getClassByName(springBootMainType).get().addAndGetAnnotation("ImportResource");

                StringBuilder sb = new StringBuilder();
                for (String classPathResource : xmlImportFiles) {
                    sb.append(classPathResource + ",");
                }
                importResourceAnnotation.addPair("value", "{" + sb.substring(0, sb.length() - 1) + "}");

                final Path packageFolder = sources.resolve(TestConfiguration.appGroupId().replace(".", File.separator));
                final Path fileName = packageFolder.resolve(springBootMainClass);
                IOUtils.writeFile(fileName, springBootMain.toString());
            } catch (IOException e) {
                throw new RuntimeException("Cannot parse "
                    + TestConfiguration.appLocation().resolve(springBootMainClass).toAbsolutePath(), e);
            }
        }
    }
}
