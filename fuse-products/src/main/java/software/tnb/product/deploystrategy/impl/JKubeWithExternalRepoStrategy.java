package software.tnb.product.deploystrategy.impl;

import software.tnb.common.product.ProductType;
import software.tnb.common.utils.IOUtils;
import software.tnb.product.config.DockerConfiguration;
import software.tnb.product.csb.configuration.SpringBootConfiguration;
import software.tnb.product.deploystrategy.OpenshiftDeployStrategy;
import software.tnb.product.deploystrategy.OpenshiftDeployStrategyType;
import software.tnb.product.deploystrategy.impl.custom.CustomJKubeStrategy;
import software.tnb.product.util.maven.Maven;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.xml.Xpp3DomBuilder;
import org.codehaus.plexus.util.xml.Xpp3DomUtils;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import com.google.auto.service.AutoService;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@AutoService(OpenshiftDeployStrategy.class)
public class JKubeWithExternalRepoStrategy extends CustomJKubeStrategy {

    protected static final String MVN_PROFILE = "openshift";

    private boolean customizeDeployment = true;

    public JKubeWithExternalRepoStrategy() {
        super(new String[]{"clean", "package", "oc:build", "oc:push", "oc:apply"}, new String[]{MVN_PROFILE});
    }

    @Override
    public ProductType[] products() {
        return new ProductType[] {ProductType.CAMEL_SPRINGBOOT};
    }

    @Override
    public OpenshiftDeployStrategyType deployType() {
        return OpenshiftDeployStrategyType.JKUBE_EXT_REPO;
    }

    public JKubeWithExternalRepoStrategy customizeDeployment(boolean customizeDeployment) {
        this.customizeDeployment = customizeDeployment;
        return this;
    }

    @Override
    protected Map<String, String> getOmpProperties() {
        final Map<String, String> props = new HashMap<>(super.getOmpProperties());
        props.put("jkube.build.strategy", "jib");
        props.put("jkube.enricher.jkube-controller.type", "Deployment");
        props.put("jkube.docker.push.retries", String.valueOf(DockerConfiguration.pushImageRetries()));
        props.put("jkube.docker.push.registry", SpringBootConfiguration.openshiftResultImageRepository());
        props.put("jkube.docker.pull.registry", SpringBootConfiguration.openshiftResultImageRepository());
        props.put("jkube.imagePullPolicy", "Always");

        Optional.ofNullable(DockerConfiguration.dockerRegistryUsername())
            .filter(StringUtils::isNotBlank)
            .map(String::trim)
            .ifPresent(v -> {
                props.put("jkube.docker.push.username", v);
                props.put("jkube.docker.pull.username", v);
            });

        Optional.ofNullable(DockerConfiguration.dockerRegistryPassword())
            .filter(StringUtils::isNotBlank)
            .map(String::trim)
            .ifPresent(v -> {
                props.put("jkube.docker.push.password", v);
                props.put("jkube.docker.pull.password", v);
            });

        return props;
    }

    @Override
    public void preDeploy() {
        super.preDeploy();
        if (customizeDeployment) {
            final File pomFile = baseDirectory.resolve("pom.xml").toFile();
            createCustomDeployment(baseDirectory.resolve("src").resolve("main").resolve("jkube"));
            final File addConfigFile;
            try {
                addConfigFile = Files.createTempFile(name, "jkube-config.xml").toFile();
                addConfigFile.deleteOnExit();
                FileUtils.copyInputStreamToFile(getClass().getResourceAsStream("/openshift/csb/jkube-config.xml"), addConfigFile);
                IOUtils.replaceInFile(addConfigFile.toPath(), Map.of("XX_JAVA_OPTS_APPEND", getPropertiesForJVM(integrationBuilder)
                    .replaceAll("\"", "\\\\\"")));
                //remove empty env to avoid https://github.com/eclipse-jkube/jkube/issues/3220
                IOUtils.replaceInFile(addConfigFile.toPath(), Map.of("<JAVA_OPTS_APPEND></JAVA_OPTS_APPEND>", ""));
            } catch (IOException e) {
                throw new RuntimeException("unable to create temp file for env-config", e);
            }
            try (InputStreamReader reader =
                     new InputStreamReader(new FileInputStream(addConfigFile))) {

                final Xpp3Dom ompConfig = Xpp3DomBuilder.build(reader);

                final Model model = Maven.loadPom(pomFile);

                final Optional<Plugin> existingOmp = model.getProfiles().stream().filter(profile -> MVN_PROFILE.equals(profile.getId()))
                    .findFirst().orElseThrow(() -> new IllegalArgumentException("unable to find " + MVN_PROFILE + " profile in Maven module"))
                    .getBuild().getPlugins().stream()
                    .filter(plugin -> plugin.getArtifactId().equals(OPENSHIFT_MAVEN_PLUGIN_AID))
                    .findFirst();
                existingOmp.ifPresentOrElse(plugin -> {
                    if (existingOmp.get().getConfiguration() != null  //existing plugin in pom.xml
                        && ((Xpp3Dom) existingOmp.get().getConfiguration()).getChild("env") != null) { //existing plugin config
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
        }

    }
}
