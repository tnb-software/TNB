package org.jboss.fuse.tnb.product.csb.application;

import org.jboss.fuse.tnb.common.config.OpenshiftConfiguration;
import org.jboss.fuse.tnb.common.config.SpringBootConfiguration;
import org.jboss.fuse.tnb.common.config.TestConfiguration;
import org.jboss.fuse.tnb.common.utils.IOUtils;
import org.jboss.fuse.tnb.product.application.App;
import org.jboss.fuse.tnb.product.git.MavenGitRepository;
import org.jboss.fuse.tnb.product.integration.GitIntegrationBuilder;
import org.jboss.fuse.tnb.product.integration.IntegrationBuilder;
import org.jboss.fuse.tnb.product.integration.IntegrationGenerator;
import org.jboss.fuse.tnb.product.integration.ResourceType;
import org.jboss.fuse.tnb.product.util.maven.BuildRequest;
import org.jboss.fuse.tnb.product.util.maven.Maven;

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

public abstract class SpringBootApp extends App {
    private static final Logger LOG = LoggerFactory.getLogger(SpringBootApp.class);
    protected MavenGitRepository mavenGitApp = null;

    public SpringBootApp(IntegrationBuilder integrationBuilder) {
        super(integrationBuilder.getIntegrationName());

        if (integrationBuilder instanceof GitIntegrationBuilder) {
            mavenGitApp = new MavenGitRepository((GitIntegrationBuilder) integrationBuilder, getName());
        } else {
            LOG.info("Creating Camel Spring Boot application project for integration {}", name);

            Maven.invoke(new BuildRequest.Builder()
                .withBaseDirectory(TestConfiguration.appLocation())
                .withGoals("archetype:generate")
                .withProperties(Map.of(
                    "archetypeGroupId", SpringBootConfiguration.camelSpringBootArchetypeGroupId(),
                    "archetypeArtifactId", SpringBootConfiguration.camelSpringBootArchetypeArtifactId(),
                    "archetypeVersion", SpringBootConfiguration.camelSpringBootArchetypeVersion(),
                    "groupId", TestConfiguration.appGroupId(),
                    "artifactId", name,
                    "version", "1.0.0-SNAPSHOT",
                    "package", TestConfiguration.appGroupId(),
                    "maven-compiler-plugin-version", SpringBootConfiguration.mavenCompilerPluginVersion(),
                    "spring-boot-version", SpringBootConfiguration.springBootVersion(),
                    "camel-version", SpringBootConfiguration.camelSpringBootVersion()))
                .withLogFile(TestConfiguration.appLocation().resolve(name + "-generate.log"))
                .build());

            IntegrationGenerator.toFile(integrationBuilder, TestConfiguration.appLocation().resolve(name));

            customizeMain(integrationBuilder, TestConfiguration.appLocation().resolve(name));

            customizeDependencies(integrationBuilder.getDependencies());

            BuildRequest.Builder requestBuilder = new BuildRequest.Builder()
                .withBaseDirectory(TestConfiguration.appLocation().resolve(name))
                .withGoals("clean", "package")
                .withProperties(Map.of(
                    "skipTests", "true"
                ))
                .withLogFile(TestConfiguration.appLocation().resolve(name + "-build.log"));

            if (OpenshiftConfiguration.isOpenshift()) {
                requestBuilder.withProperties(Map.of(
                    "skipTests", "true"
                    , "openshift-maven-plugin-version", SpringBootConfiguration.openshiftMavenPluginVersion()
                    , "openshift-maven-plugin-group-id", SpringBootConfiguration.openshiftMavenPluginGroupId()
                ));
                requestBuilder.withGoals("clean", "package", "oc:resource");
                requestBuilder.withProfiles("openshift");
            }

            LOG.info("Building {} application project", name);
            Maven.invoke(requestBuilder.build());
        }
    }

    private void customizeDependencies(List<Dependency> mavenDependencies) {
        File pom = TestConfiguration.appLocation().resolve(name).resolve("pom.xml").toFile();
        Model model = Maven.loadPom(pom);

        mavenDependencies.forEach(model.getDependencies()::add);

        Maven.writePom(pom, model);
    }

    private void customizeMain(IntegrationBuilder integrationBuilder, Path location) {
        addXmlResourceImport(integrationBuilder, location);
    }

    private void addXmlResourceImport(IntegrationBuilder integrationBuilder, Path location) {
        final Path sources = location.resolve("src/main/java");
        final Path resourcesPath = location.resolve("src/main/resources");

        List<String> xmlImportFiles = new ArrayList<>();
        integrationBuilder.getXmlCamelContext().forEach(resource -> {
            if (ResourceType.XML_CAMEL_CONTEXT.equals(resource.getType())) {
                IOUtils.writeFile(resourcesPath.resolve(resource.getName()), resource.getContent());

                xmlImportFiles.add("\"classpath:" + resource.getName() + "\"");
            }
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

    /**
     * Customizes the project generated by spring boot mvn archetype to TNB app needs.
     *
     * @param dependencies dependencies to add to the project
     */
    private void customizeProject(List<String> dependencies) {
        File pom = TestConfiguration.appLocation().resolve(name).resolve("pom.xml").toFile();
        Model model = Maven.loadPom(pom);

        dependencies.forEach(dep -> model.getDependencies().add(Maven.toDependency(dep)));

        Maven.writePom(pom, model);
    }
}
