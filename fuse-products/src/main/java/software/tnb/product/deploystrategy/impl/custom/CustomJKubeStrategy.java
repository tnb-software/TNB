package software.tnb.product.deploystrategy.impl.custom;

import software.tnb.common.config.OpenshiftConfiguration;
import software.tnb.common.config.TestConfiguration;
import software.tnb.common.openshift.OpenshiftClient;
import software.tnb.product.application.Phase;
import software.tnb.product.csb.application.OpenshiftSpringBootApp;
import software.tnb.product.csb.configuration.SpringBootConfiguration;
import software.tnb.product.integration.builder.AbstractMavenGitIntegrationBuilder;
import software.tnb.product.log.stream.LogStream;
import software.tnb.product.util.maven.BuildRequest;
import software.tnb.product.util.maven.Maven;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.xml.Xpp3DomBuilder;
import org.codehaus.plexus.util.xml.Xpp3DomUtils;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.fabric8.kubernetes.api.model.Pod;

public class CustomJKubeStrategy extends OpenshiftCustomDeployer {

    public static final String OPENSHIFT_MAVEN_PLUGIN_AID = "openshift-maven-plugin";

    private final String[] goals;
    private final String[] profiles;
    private boolean createTnbDeployment = false;

    public CustomJKubeStrategy(final String[] goals, final String[] profiles) {
        this.goals = goals;
        this.profiles = profiles;
    }

    public CustomJKubeStrategy createTnbDeployment(final boolean createTnbDeployment) {
        this.createTnbDeployment = createTnbDeployment;
        return this;
    }

    @Override
    public void doDeploy() {
        Map<String, String> mvnProps = new HashMap<>();
        if (integrationBuilder instanceof  AbstractMavenGitIntegrationBuilder
            && ((AbstractMavenGitIntegrationBuilder<?>) integrationBuilder).getMavenProperties() != null) {
            mvnProps = ((AbstractMavenGitIntegrationBuilder<?>) integrationBuilder).getMavenProperties();
        }
        final Map<String, String> ompProperties = Map.of(
            "skipTests", "true"
            , "openshift-maven-plugin-version", SpringBootConfiguration.openshiftMavenPluginVersion()
            , "openshift-maven-plugin-group-id", SpringBootConfiguration.openshiftMavenPluginGroupId()
            , "jkube.namespace", OpenshiftClient.get().getNamespace()
            , "jkube.masterUrl", OpenshiftConfiguration.openshiftUrl() != null ? OpenshiftConfiguration.openshiftUrl()
                : OpenshiftClient.get().getMasterUrl().toString()
            , "jkube.username", OpenshiftConfiguration.openshiftUsername()
            , "jkube.generator.from", SpringBootConfiguration.openshiftBaseImage()
            , "jkube.enricher.jkube-service.port", String.format("%s:%s", integrationBuilder.getPort(), integrationBuilder.getPort())
            , "jkube.enricher.jkube-service.expose", "true"
        );
        final BuildRequest.Builder requestBuilder = new BuildRequest.Builder()
            .withBaseDirectory(baseDirectory)
            .withProperties(Stream.concat(ompProperties.entrySet().stream()
                    , mvnProps.entrySet().stream())
                .collect(Collectors
                    .toMap(Map.Entry::getKey, Map.Entry::getValue, (k, v) -> k)))
            .withGoals(goals)
            .withProfiles(profiles)
            .withLogFile(TestConfiguration.appLocation().resolve(String.format("%s-%s-deploy.log", name, System.currentTimeMillis())))
            .withLogMarker(LogStream.marker(name, Phase.DEPLOY));
        Maven.invoke(requestBuilder.build());
    }

    @Override
    public void preDeploy() {
        final Path jkubeFolder = Path.of(baseDirectory.toAbsolutePath().toString(), "src", "main", "jkube");
        jkubeFolder.toFile().mkdirs();

        final File pomFile = baseDirectory.resolve("pom.xml").toFile();

        if (integrationBuilder instanceof AbstractMavenGitIntegrationBuilder) {
            //patch to avoid too characters on OCP resource names
            final Model model = Maven.loadPom(pomFile);
            model.setArtifactId(model.getArtifactId().replaceAll("camel-example-spring-boot-", "csb-"));

            //set final name if present
            ((AbstractMavenGitIntegrationBuilder) integrationBuilder).getFinalName().ifPresent(finalName -> {
                model.getBuild().setFinalName((String) finalName);
            });
            Maven.writePom(pomFile, model);
        }

        if (createTnbDeployment) {
            addCustomResources(pomFile);
            createConfigMap(jkubeFolder, pomFile);
            createCustomDeployment(jkubeFolder);
        }
    }

    private void addCustomResources(File pomFile) {
        //copy resources
        copyResources(baseDirectory, "jkube-resources");

        //add resources via plugin config
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

            model.getBuild().setFinalName(integrationBuilder.getIntegrationName());

            Maven.writePom(pomFile, model);
        } catch (IOException | XmlPullParserException e) {
            throw new RuntimeException("Error configuring jkube plugin", e);
        }
    }

    private void createCustomDeployment(Path jkubeFolder) {
        try {
            final String deploymentContent = StringUtils.replace(IOUtils.resourceToString("/openshift/csb/deployment.yaml", StandardCharsets.UTF_8),
                "XX_JAVA_OPTS_APPEND", getPropertiesForJVM(integrationBuilder).replaceAll("\"", "\\\\\""));
            software.tnb.common.utils.IOUtils.writeFile(jkubeFolder.resolve("deployment.yaml"), deploymentContent);
        } catch (IOException e) {
            throw new RuntimeException("Error creating custom deployment.yaml", e);
        }
    }

    private void createConfigMap(Path jkubeFolder, final File pomFile) {
        String properties = "";
        if (integrationBuilder instanceof AbstractMavenGitIntegrationBuilder) {
            properties = ((AbstractMavenGitIntegrationBuilder<?>) integrationBuilder).getJavaProperties().entrySet().stream()
                .map(entry -> "    " + entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining(System.lineSeparator()));
        }
        String jkubeFileContent = "data:" + System.lineSeparator()
            + "  application.properties: |" + System.lineSeparator()
            + properties;

        software.tnb.common.utils.IOUtils.writeFile(jkubeFolder.resolve("configmap.yaml"), jkubeFileContent);
    }

    @Override
    public boolean isFailed() {
        return deployPodFailed() || integrationPodFailed();
    }

    private boolean deployPodFailed() {
        long lastVersion;
        try {
            lastVersion = OpenshiftClient.get().buildConfigs().withName(name).get().getStatus().getLastVersion();
            return OpenshiftClient.get()
                .isPodFailed(OpenshiftClient.get().getLabeledPods("openshift.io/deployer-pod-for.name", name + "-" + lastVersion).get(0));
        } catch (Exception e) {
            return false;
        }
    }

    private boolean integrationPodFailed() {
        final List<Pod> pods = OpenshiftClient.get().getLabeledPods("app.kubernetes.io/name", name);
        if (pods.size() == 0) {
            return isIntegrationPodFailed();
        } else {
            return OpenshiftClient.get().isPodFailed(OpenshiftClient.get().getLabeledPods("app.kubernetes.io/name", name).get(0));
        }
    }
}
