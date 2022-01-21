package org.jboss.fuse.tnb.product.csb.application;

import org.jboss.fuse.tnb.common.config.QuarkusConfiguration;
import org.jboss.fuse.tnb.common.config.SpringBootConfiguration;
import org.jboss.fuse.tnb.common.config.TestConfiguration;
import org.jboss.fuse.tnb.product.application.App;
import org.jboss.fuse.tnb.product.integration.IntegrationBuilder;
import org.jboss.fuse.tnb.product.integration.IntegrationGenerator;
import org.jboss.fuse.tnb.product.util.maven.BuildRequest;
import org.jboss.fuse.tnb.product.util.maven.Maven;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class SpringBootApp extends App {
    private static final Logger LOG = LoggerFactory.getLogger(SpringBootApp.class);
    private final boolean isProd = SpringBootConfiguration.camelSpringBootVersion().contains("redhat");

    public SpringBootApp(IntegrationBuilder integrationBuilder) {
        super(integrationBuilder.getIntegrationName());
        LOG.info("Creating Camel Spring Boot application project for integration {}", name);
/*
        -DarchetypeGroupId=org.apache.camel.archetypes \
        -DarchetypeArtifactId=camel3-archetype-spring-boot \
        -DarchetypeVersion=1.0.0 \
        -DgroupId=org.apache.camel.archetypes.archetypeIT.camel-archetype-spring-boot \
        -DartifactId=build-it \
        -Dversion=0.0.1 \
        -Dpackage=org.apache.camel.archetypes.archetypeIT \
        -Dmaven-compiler-plugin-version=3.8.1 \
        -Dspring-boot-version=2.6.2 \
        -Dcamel-version=3.14.0
 */

        Maven.invoke(new BuildRequest.Builder()
            .withBaseDirectory(TestConfiguration.appLocation())
            .withGoals("archetype:generate")
            .withProperties(Map.of(
                "archetypeGroupId", SpringBootConfiguration.camelSpringBootArchetypeGroupId(),
                "archetypeArtifactId", SpringBootConfiguration.camelSpringBootArchetypeArtifactId(),
                "archetypeVersion", SpringBootConfiguration.camelSpringBootArchetypeVersion(),
                "groupId", TestConfiguration.appGroupId(),
                "artifactId", TestConfiguration.appTemplateName(),
                "version", "1.0.0-SNAPSHOT",
                "package", TestConfiguration.appGroupId(),
                "maven-compiler-plugin-version", "3.8.1",
                "spring-boot-version", SpringBootConfiguration.springBootVersion(),
                "camel-version", SpringBootConfiguration.camelSpringBootVersion()))
            .withLogFile(TestConfiguration.appLocation().resolve("app-template-generate.log"))
            .build());

        IntegrationGenerator.toFile(integrationBuilder, TestConfiguration.appLocation().resolve(name));

        // customizeProject(integrationBuilder.getDependencies());

        BuildRequest.Builder requestBuilder = new BuildRequest.Builder()
            .withBaseDirectory(TestConfiguration.appLocation().resolve(name))
            .withGoals("clean", "package")
            .withProperties(Map.of(
                "skipTests", "true",
                "quarkus.native.container-build", "true"
            ))
            .withLogFile(TestConfiguration.appLocation().resolve(name + "-build.log"));
        if (QuarkusConfiguration.isQuarkusNative()) {
            requestBuilder.withProfiles("native");
        }

        LOG.info("Building {} application project ({})", name, QuarkusConfiguration.isQuarkusNative() ? "native" : "JVM");
        Maven.invoke(requestBuilder.build());
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Override
    public boolean isReady() {
        return false;
    }

    @Override
    public boolean isFailed() {
        return false;
    }
}
