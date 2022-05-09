package org.jboss.fuse.tnb.product.deploystrategy.impl;

import org.jboss.fuse.tnb.common.config.OpenshiftConfiguration;
import org.jboss.fuse.tnb.common.config.TestConfiguration;
import org.jboss.fuse.tnb.common.openshift.OpenshiftClient;
import org.jboss.fuse.tnb.common.product.ProductType;
import org.jboss.fuse.tnb.product.csb.application.OpenshiftSpringBootApp;
import org.jboss.fuse.tnb.product.csb.configuration.SpringBootConfiguration;
import org.jboss.fuse.tnb.product.deploystrategy.OpenshiftBaseDeployer;
import org.jboss.fuse.tnb.product.deploystrategy.OpenshiftDeployStrategy;
import org.jboss.fuse.tnb.product.deploystrategy.OpenshiftDeployStrategyType;
import org.jboss.fuse.tnb.product.integration.builder.AbstractMavenGitIntegrationBuilder;
import org.jboss.fuse.tnb.product.log.stream.LogStream;
import org.jboss.fuse.tnb.product.util.maven.BuildRequest;
import org.jboss.fuse.tnb.product.util.maven.Maven;

import org.apache.commons.io.IOUtils;
import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.xml.Xpp3DomBuilder;
import org.codehaus.plexus.util.xml.Xpp3DomUtils;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import com.google.auto.service.AutoService;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@AutoService(OpenshiftDeployStrategy.class)
public class JKubeStrategy extends OpenshiftBaseDeployer {

    public static final String OPENSHIFT_MAVEN_PLUGIN_AID = "openshift-maven-plugin";

    @Override
    public ProductType[] products() {
        return new ProductType[] {ProductType.CAMEL_SPRINGBOOT};
    }

    @Override
    public OpenshiftDeployStrategyType deployType() {
        return OpenshiftDeployStrategyType.JKUBE;
    }

    @Override
    public void doDeploy() {
        String oc = String.format("%s:%s:%s", SpringBootConfiguration.openshiftMavenPluginGroupId()
            , OPENSHIFT_MAVEN_PLUGIN_AID
            , SpringBootConfiguration.openshiftMavenPluginVersion());
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
                , "jkube.enricher.jkube-service.port", "8080:8080"
                , "jkube.enricher.jkube-service.expose", "true"
            )).withGoals("clean", "package", oc + ":resource", oc + ":build", oc + ":apply")
            .withProfiles("openshift")
            .withLogFile(TestConfiguration.appLocation().resolve(name + "-deploy.log"))
            .withLogMarker(LogStream.marker(name, "deploy"));
        Maven.invoke(requestBuilder.build());
    }

    @Override
    public void preDeploy() {
        Path jkubeFolder = Path.of(baseDirectory.toAbsolutePath().toString(), "src", "main", "jkube");
        jkubeFolder.toFile().mkdirs();
        String properties = "";
        if (integrationBuilder instanceof AbstractMavenGitIntegrationBuilder) {

            properties = ((AbstractMavenGitIntegrationBuilder<?>) integrationBuilder).getJavaProperties().entrySet().stream()
                .map(entry -> "    " + entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining(System.lineSeparator()));

            //copy resources
            copyResources(baseDirectory, "jkube-resources");

            //add resources via plugin config
            final File pomFile = baseDirectory.resolve("pom.xml").toFile();

            try (InputStreamReader reader =
                     new InputStreamReader(OpenshiftSpringBootApp.class.getResourceAsStream("/openshift/csb/jkube-fileset-config.xml"))) {

                final Xpp3Dom ompConfig = Xpp3DomBuilder.build(reader);

                final Model model = Maven.loadPom(pomFile);

                final Optional<Plugin> existingOmp = model.getBuild().getPlugins().stream()
                    .filter(plugin -> plugin.getArtifactId().equals(OPENSHIFT_MAVEN_PLUGIN_AID))
                    .findFirst();
                existingOmp.ifPresentOrElse(plugin -> {
                    if (existingOmp.get().getConfiguration() != null  //existing plugin in pom.xml
                        && ((Xpp3Dom) existingOmp.get().getConfiguration()).getChild("images") != null) { //existing plugin config
                        existingOmp.get().setConfiguration(Xpp3DomUtils.mergeXpp3Dom(ompConfig
                            , ((Xpp3Dom) existingOmp.get().getConfiguration())));
                    } else {
                        existingOmp.get().setConfiguration(ompConfig); //non existing config in existing plugin
                    }
                }, () -> { //non existing plugin in pom.xml
                    final Plugin omp = new Plugin();
                    omp.setArtifactId(OPENSHIFT_MAVEN_PLUGIN_AID);
                    omp.setGroupId(SpringBootConfiguration.openshiftMavenPluginGroupId());
                    omp.setVersion(SpringBootConfiguration.openshiftMavenPluginVersion());
                    omp.setConfiguration(ompConfig);
                    model.getBuild().getPlugins().add(omp);
                });

                Maven.writePom(pomFile, model);
            } catch (IOException | XmlPullParserException e) {
                throw new RuntimeException("Error configuring jkube plugin", e);
            }

            //patch to avoid too characters on OCP resource names
            final Model model = Maven.loadPom(pomFile);
            model.setArtifactId(model.getArtifactId().replaceAll("camel-example-spring-boot-", "csb-"));
            Maven.writePom(pomFile, model);
        }

        String jkubeFileContent = "data:" + System.lineSeparator()
            + "  application.properties: |" + System.lineSeparator()
            + properties;

        org.jboss.fuse.tnb.common.utils.IOUtils.writeFile(jkubeFolder.resolve("configmap.yaml"), jkubeFileContent);

        try {
            final String deploymentContent = IOUtils.resourceToString("/openshift/csb/deployment.yaml", StandardCharsets.UTF_8)
                .replaceAll("XX_JAVA_OPTS_APPEND", getPropertiesForJVM(integrationBuilder));
            org.jboss.fuse.tnb.common.utils.IOUtils.writeFile(jkubeFolder.resolve("deployment.yaml"), deploymentContent);
        } catch (IOException e) {
            throw new RuntimeException("Error creating custom deployment.yaml", e);
        }

    }
}
