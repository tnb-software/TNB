package org.jboss.fuse.tnb.product.cq.application;

import org.jboss.fuse.tnb.common.config.TestConfiguration;
import org.jboss.fuse.tnb.common.openshift.OpenshiftClient;
import org.jboss.fuse.tnb.product.cq.OpenshiftCamelQuarkus;
import org.jboss.fuse.tnb.product.cq.configuration.QuarkusConfiguration;
import org.jboss.fuse.tnb.product.endpoint.Endpoint;
import org.jboss.fuse.tnb.product.integration.builder.AbstractIntegrationBuilder;
import org.jboss.fuse.tnb.product.log.OpenshiftLog;
import org.jboss.fuse.tnb.product.log.stream.LogStream;
import org.jboss.fuse.tnb.product.log.stream.OpenshiftLogStream;
import org.jboss.fuse.tnb.product.util.maven.BuildRequest;
import org.jboss.fuse.tnb.product.util.maven.Maven;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import cz.xtf.core.openshift.helpers.ResourceFunctions;
import io.fabric8.kubernetes.api.model.ContainerStatus;
import io.fabric8.kubernetes.api.model.DeletionPropagation;
import io.fabric8.kubernetes.api.model.Pod;

public class OpenshiftQuarkusApp extends QuarkusApp {
    private static final Logger LOG = LoggerFactory.getLogger(OpenshiftCamelQuarkus.class);

    public OpenshiftQuarkusApp(AbstractIntegrationBuilder<?> integrationBuilder) {
        super(integrationBuilder);
    }

    @Override
    public void start() {
        final BuildRequest.Builder builder = new BuildRequest.Builder()
            .withBaseDirectory(TestConfiguration.appLocation().resolve(name))
            .withGoals("package")
            .withProperties(Map.of(
                "quarkus.kubernetes-client.master-url", OpenshiftClient.get().getConfiguration().getMasterUrl(),
                "quarkus.kubernetes-client.token", OpenshiftClient.get().getConfiguration().getOauthToken(),
                "quarkus.kubernetes-client.namespace", OpenshiftClient.get().getNamespace(),
                "quarkus.kubernetes-client.trust-certs", "true",
                "quarkus.kubernetes.deploy", "true",
                "quarkus.native.container-build", "true",
                "skipTests", "true"
            ))
            .withLogFile(TestConfiguration.appLocation().resolve(name + "-deploy.log"))
            .withLogMarker(LogStream.marker(name, "deploy"));
        if (QuarkusConfiguration.isQuarkusNative()) {
            builder.withProfiles("native");
        }
        Maven.invoke(builder.build());

        endpoint = new Endpoint(() -> "http://" + OpenshiftClient.get().routes()
            .withName(name).get().getSpec().getHost());

        Predicate<Pod> podSelector = p -> p.getMetadata().getLabels().containsKey("app.kubernetes.io/name")
            && name.equals(p.getMetadata().getLabels().get("app.kubernetes.io/name"));
        log = new OpenshiftLog(podSelector, getName());
        logStream = new OpenshiftLogStream(podSelector, LogStream.marker(name));
    }

    @Override
    public void stop() {
        if (logStream != null) {
            logStream.stop();
        }
        if (getLog() != null) {
            ((OpenshiftLog) getLog()).save(started);
        }
        LOG.info("Undeploying integration resources");
        Path openshiftResources = TestConfiguration.appLocation().resolve(name).resolve("target").resolve("kubernetes/openshift.yml");

        try (InputStream is = IOUtils.toInputStream(Files.readString(openshiftResources), "UTF-8")) {
            LOG.info("Deleting openshift resources for integration from file {}", openshiftResources.toAbsolutePath());
            OpenshiftClient.get().load(is).withPropagationPolicy(DeletionPropagation.BACKGROUND).delete();
        } catch (IOException e) {
            throw new RuntimeException("Unable to read openshift.yml resource: ", e);
        }
    }

    @Override
    public boolean isReady() {
        return ResourceFunctions.areExactlyNPodsRunning(1).apply(OpenshiftClient.get().getLabeledPods("app.kubernetes.io/name", name));
    }

    @Override
    public boolean isFailed() {
        try {
            List<ContainerStatus> containerStatuses = OpenshiftClient.get().getLabeledPods("app.kubernetes.io/name", name)
                .get(0).getStatus().getContainerStatuses();
            if (containerStatuses == null || containerStatuses.isEmpty()) {
                return false;
            }
            return containerStatuses.stream().anyMatch(p -> "error".equalsIgnoreCase(p.getLastState().getTerminated().getReason()));
        } catch (Exception ignored) {
            return false;
        }
    }

    /**
     * Parses the needed imagestream:tag from the build config and waits until the imagestream contains that tag.
     *
     * @param bcName buildconfig name
     */
    private void waitForImageStream(String bcName) {
        final String[] imageStreamTag = OpenshiftClient.get().buildConfigs().withName(bcName).get().getSpec().getStrategy()
            .getSourceStrategy().getFrom().getName().split(":");
        OpenshiftClient.get().waitForImageStream(imageStreamTag[0], imageStreamTag[1]);
    }
}
