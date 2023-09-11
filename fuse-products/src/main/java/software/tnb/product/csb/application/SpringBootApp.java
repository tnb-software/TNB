package software.tnb.product.csb.application;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class SpringBootApp extends App {
    private static final Logger LOG = LoggerFactory.getLogger(SpringBootApp.class);
    protected MavenGitRepository mavenGitApp = null;

    private boolean shouldRun = true;

    @Override
    public boolean shouldRun() {
        return shouldRun;
    }

    public SpringBootApp(AbstractIntegrationBuilder<?> integrationBuilder) {
        super(integrationBuilder.getIntegrationName());

        if (integrationBuilder instanceof AbstractGitIntegrationBuilder<?>
            && ((AbstractGitIntegrationBuilder<?>) integrationBuilder).getRepositoryUrl() != null) {
            mavenGitApp = new MavenGitRepository((AbstractMavenGitIntegrationBuilder<?>) integrationBuilder
                , getName(), getLogPath(Phase.BUILD)
                , ((AbstractMavenGitIntegrationBuilder<?>) integrationBuilder).buildProject());
            shouldRun = ((AbstractGitIntegrationBuilder<?>) integrationBuilder).runApplication();
        } else if (getExistingJar(integrationBuilder) == null) {
            LOG.info("Creating Camel Spring Boot application project for integration {}", name);

            Map<String, String> properties = new HashMap<>(13);
            properties.putAll(Map.of(
                "archetypeGroupId", SpringBootConfiguration.camelSpringBootArchetypeGroupId(),
                "archetypeArtifactId", SpringBootConfiguration.camelSpringBootArchetypeArtifactId(),
                "archetypeVersion", SpringBootConfiguration.camelSpringBootArchetypeVersion(),
                "groupId", TestConfiguration.appGroupId(),
                "artifactId", name,
                "version", "1.0.0-SNAPSHOT",
                "package", TestConfiguration.appGroupId(),
                "maven-compiler-plugin-version", SpringBootConfiguration.mavenCompilerPluginVersion(),
                "default-spring-boot-version", SpringBootConfiguration.springBootVersion(),
                "default-camel-spring-boot-version", SpringBootConfiguration.camelSpringBootVersion()));
            if (SpringBootConfiguration.camelSpringBootVersion().contains("redhat")) {
                properties.put("dependencies-resolution", "redhat-platform");
            }
            if (Integer.parseInt(System.getProperty("java.version").split("\\.")[0]) >= 17) {
                properties.put("java-version", "17");
            }
            properties.put("archetypeCatalog", "internal");

            Maven.invoke(new BuildRequest.Builder()
                .withBaseDirectory(TestConfiguration.appLocation())
                .withGoals("archetype:generate")
                .withProperties(properties)
                .withLogFile(getLogPath(Phase.GENERATE))
                .withLogMarker(LogStream.marker(name, Phase.GENERATE))
                .build());

            IntegrationGenerator.toFile(integrationBuilder, TestConfiguration.appLocation().resolve(name));

            customizeMain(integrationBuilder, TestConfiguration.appLocation().resolve(name));

            customizeDependencies(integrationBuilder.getDependencies());

            customizePlugins(integrationBuilder.getPlugins());

            BuildRequest.Builder requestBuilder = new BuildRequest.Builder()
                .withBaseDirectory(TestConfiguration.appLocation().resolve(name))
                .withGoals("clean", "package")
                .withProperties(Map.of(
                    "skipTests", "true"
                ))
                .withLogFile(getLogPath(Phase.BUILD))
                .withLogMarker(LogStream.marker(name, Phase.BUILD));

            LOG.info("Building {} application project", name);
            Maven.invoke(requestBuilder.build());
        }
    }

    protected Path getExistingJar(AbstractIntegrationBuilder<?> integrationBuilder) {
        return integrationBuilder instanceof SpringBootIntegrationBuilder
            ? ((SpringBootIntegrationBuilder) integrationBuilder).getExistingJar() : null;
    }

    private void customizeDependencies(List<Dependency> mavenDependencies) {
        File pom = TestConfiguration.appLocation().resolve(name).resolve("pom.xml").toFile();
        Model model = Maven.loadPom(pom);

        mavenDependencies.forEach(model.getDependencies()::add);

        Maven.writePom(pom, model);
    }

    private void customizeMain(AbstractIntegrationBuilder<?> integrationBuilder, Path location) {
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
