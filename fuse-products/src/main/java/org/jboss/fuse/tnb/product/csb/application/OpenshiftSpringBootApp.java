package org.jboss.fuse.tnb.product.csb.application;

import org.jboss.fuse.tnb.common.config.OpenshiftConfiguration;
import org.jboss.fuse.tnb.common.config.SpringBootConfiguration;
import org.jboss.fuse.tnb.common.config.TestConfiguration;
import org.jboss.fuse.tnb.common.openshift.OpenshiftClient;
import org.jboss.fuse.tnb.product.endpoint.Endpoint;
import org.jboss.fuse.tnb.product.integration.builder.AbstractIntegrationBuilder;
import org.jboss.fuse.tnb.product.integration.builder.AbstractMavenGitIntegrationBuilder;
import org.jboss.fuse.tnb.product.log.OpenshiftLog;
import org.jboss.fuse.tnb.product.util.maven.BuildRequest;
import org.jboss.fuse.tnb.product.util.maven.Maven;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.internal.readiness.Readiness;
import io.fabric8.kubernetes.client.utils.PodStatusUtil;

public class OpenshiftSpringBootApp extends SpringBootApp {
    private static final Logger LOG = LoggerFactory.getLogger(OpenshiftSpringBootApp.class);
    private final Path baseDirectory;
    private final String finalName;

    public OpenshiftSpringBootApp(AbstractIntegrationBuilder<?> integrationBuilder) {
        super(integrationBuilder);

        if (integrationBuilder instanceof AbstractMavenGitIntegrationBuilder) {
            AbstractMavenGitIntegrationBuilder<?> mavenGitIntegrationBuilder = (AbstractMavenGitIntegrationBuilder<?>) integrationBuilder;

            baseDirectory = mavenGitApp.getProjectLocation();
            Path jkubeFolder = Path.of(baseDirectory.toAbsolutePath().toString(), "src", "main", "jkube");
            jkubeFolder.toFile().mkdirs();

            String properties = mavenGitIntegrationBuilder.getJavaProperties().entrySet().stream()
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

            finalName = mavenGitIntegrationBuilder.getFinalName().orElse(getName());
        } else {
            baseDirectory = TestConfiguration.appLocation().resolve(name);

            finalName = getName();
        }
    }

    @Override
    public void start() {
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
            )).withGoals(oc + ":resource", oc + ":build", oc + ":apply")
            .withProfiles("openshift")
            .withLogFile(TestConfiguration.appLocation().resolve(finalName + "-deploy.log"));
        Maven.invoke(requestBuilder.build());

        endpoint = new Endpoint(() -> "http://" + OpenshiftClient.get().routes()
            .withName(finalName).get().getSpec().getHost());

        log = new OpenshiftLog(p -> p.getMetadata().getLabels().containsKey("app")
            && finalName.equals(p.getMetadata().getLabels().get("app"))
            && p.getMetadata().getLabels().containsKey("provider")
            && "jkube".equals(p.getMetadata().getLabels().get("provider")), getName());
    }

    @Override
    public void stop() {
        if (getLog() != null) {
            ((OpenshiftLog) getLog()).save(started);
        }
        LOG.info("Undeploy integration resources");
        final Map<String, String> labelMap = Map.of("app", finalName, "provider", "jkube");
        //builds
        //delete build's pod
        OpenshiftClient.get().builds().withLabels(labelMap).list()
            .getItems().forEach(build -> OpenshiftClient.get().pods()
                .withLabels(Map.of("openshift.io/build.name", build.getMetadata().getName())).delete()
            );
        OpenshiftClient.get().builds().withLabels(labelMap).delete();
        OpenshiftClient.get().buildConfigs().withLabels(labelMap).delete();
        OpenshiftClient.get().imageStreams().withLabels(labelMap).delete();
        //app
        OpenshiftClient.get().deploymentConfigs().withLabels(labelMap).delete();
        //network
        OpenshiftClient.get().services().withLabels(labelMap).delete();
        OpenshiftClient.get().routes().withLabels(labelMap).delete();
        //remaining pods
        OpenshiftClient.get().pods().withLabels(labelMap).delete();
    }

    @Override
    public boolean isReady() {
        try {
            final List<Pod> pods = OpenshiftClient.get().getLabeledPods(Map.of("app", finalName, "provider", "jkube"));
            return !pods.isEmpty() && pods.stream().allMatch(Readiness::isPodReady);
        } catch (Exception ignored) {
            return false;
        }
    }

    @Override
    public boolean isFailed() {
        try {
            final List<Pod> pods = OpenshiftClient.get().getLabeledPods(Map.of("app", finalName, "provider", "jkube"));
            return !pods.isEmpty() && pods.stream().map(PodStatusUtil::getContainerStatus)
                .allMatch(containerStatuses -> containerStatuses.stream()
                    .anyMatch(containerStatus -> "error".equalsIgnoreCase(containerStatus.getLastState().getTerminated().getReason())));
        } catch (Exception ignored) {
            return false;
        }
    }
}
