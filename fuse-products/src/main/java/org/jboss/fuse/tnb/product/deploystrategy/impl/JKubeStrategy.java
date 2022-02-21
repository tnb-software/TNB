package org.jboss.fuse.tnb.product.deploystrategy.impl;

import org.jboss.fuse.tnb.common.config.OpenshiftConfiguration;
import org.jboss.fuse.tnb.common.config.SpringBootConfiguration;
import org.jboss.fuse.tnb.common.config.TestConfiguration;
import org.jboss.fuse.tnb.common.openshift.OpenshiftClient;
import org.jboss.fuse.tnb.common.product.ProductType;
import org.jboss.fuse.tnb.product.csb.application.OpenshiftSpringBootApp;
import org.jboss.fuse.tnb.product.deploystrategy.OpenshiftBaseDeployer;
import org.jboss.fuse.tnb.product.deploystrategy.OpenshiftDeployStrategy;
import org.jboss.fuse.tnb.product.deploystrategy.OpenshiftDeployStrategyType;
import org.jboss.fuse.tnb.product.integration.builder.AbstractMavenGitIntegrationBuilder;
import org.jboss.fuse.tnb.product.util.maven.BuildRequest;
import org.jboss.fuse.tnb.product.util.maven.Maven;

import org.apache.commons.io.FileUtils;

import com.google.auto.service.AutoService;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.stream.Collectors;

@AutoService(OpenshiftDeployStrategy.class)
public class JKubeStrategy extends OpenshiftBaseDeployer {

    @Override
    public ProductType[] products() {
        return new ProductType[]{ProductType.CAMEL_SPRINGBOOT};
    }

    @Override
    public OpenshiftDeployStrategyType deployType() {
        return OpenshiftDeployStrategyType.JKUBE;
    }

    @Override
    public void doDeploy() {
        String oc = SpringBootConfiguration.openshiftMavenPluginGroupId()
            + ":openshift-maven-plugin:"
            + SpringBootConfiguration.openshiftMavenPluginVersion();
        final BuildRequest.Builder requestBuilder = new BuildRequest.Builder()
            .withBaseDirectory(baseDirectory)
            .withProperties(Map.of(
                "skipTests", "true"
                , "openshift-maven-plugin-version", SpringBootConfiguration.openshiftMavenPluginVersion()
                , "openshift-maven-plugin-group-id", SpringBootConfiguration.openshiftMavenPluginGroupId()
                , "jkube.namespace", OpenshiftConfiguration.openshiftNamespace()
                , "jkube.masterUrl", OpenshiftConfiguration.openshiftUrl() != null ? OpenshiftConfiguration.openshiftUrl()
                    : OpenshiftClient.get().getMasterUrl().toString()
                , "jkube.username", OpenshiftConfiguration.openshiftUsername()
                , "jkube.generator.from", SpringBootConfiguration.openshiftBaseImage()
                , "jkube.build.recreate", "all"
                , "jkube.docker.logStdout", "true"
            )).withGoals("clean", "package", oc + ":resource", oc + ":build", oc + ":apply")
            .withProfiles("openshift")
            .withLogFile(TestConfiguration.appLocation().resolve(name + "-deploy.log"));
        Maven.invoke(requestBuilder.build());
    }

    @Override
    public void preDeploy() {
        if (integrationBuilder instanceof AbstractMavenGitIntegrationBuilder) {
            Path jkubeFolder = Path.of(baseDirectory.toAbsolutePath().toString(), "src", "main", "jkube");
            jkubeFolder.toFile().mkdirs();

            String properties = ((AbstractMavenGitIntegrationBuilder<?>) integrationBuilder).getJavaProperties().entrySet().stream()
                .map(entry -> "    " + entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining(System.lineSeparator()));

            String jkubeFileContent = "data:" + System.lineSeparator()
                + "  application.properties: |" + System.lineSeparator()
                + properties;

            try {
                Files.copy(OpenshiftSpringBootApp.class.getResourceAsStream("/openshift/csb/deployment.yaml"),
                    jkubeFolder.resolve("deployment.yaml"));
                FileUtils.writeStringToFile(new File(jkubeFolder.toFile(), "configmap.yaml"), jkubeFileContent, StandardCharsets.UTF_8);
            } catch (IOException e) {
                throw new RuntimeException("Error creating jkube configmap.yaml", e);
            }
        }
    }
}
