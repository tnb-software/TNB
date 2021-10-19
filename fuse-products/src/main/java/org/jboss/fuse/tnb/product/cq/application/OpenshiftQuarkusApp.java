package org.jboss.fuse.tnb.product.cq.application;

import static org.jboss.fuse.tnb.common.utils.IOUtils.createTar;

import org.jboss.fuse.tnb.common.config.QuarkusConfiguration;
import org.jboss.fuse.tnb.common.config.TestConfiguration;
import org.jboss.fuse.tnb.common.openshift.OpenshiftClient;
import org.jboss.fuse.tnb.product.cq.OpenshiftCamelQuarkus;
import org.jboss.fuse.tnb.product.integration.IntegrationBuilder;
import org.jboss.fuse.tnb.product.log.OpenshiftLog;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import cz.xtf.core.openshift.helpers.ResourceFunctions;
import io.fabric8.kubernetes.api.model.ContainerStatus;
import io.fabric8.kubernetes.api.model.DeletionPropagation;
import io.fabric8.kubernetes.api.model.HasMetadata;

public class OpenshiftQuarkusApp extends QuarkusApp {
    private static final Logger LOG = LoggerFactory.getLogger(OpenshiftCamelQuarkus.class);
    // Resources generated from quarkus maven plugin and created in openshift
    private List<HasMetadata> createdResources;

    public OpenshiftQuarkusApp(IntegrationBuilder integrationBuilder) {
        super(integrationBuilder);
    }

    @Override
    public void start() {
        Path integrationTarget = TestConfiguration.appLocation().resolve(name).resolve("target");
        Path openshiftResources = integrationTarget.resolve("kubernetes/openshift.yml");

        try (InputStream is = IOUtils.toInputStream(Files.readString(openshiftResources), "UTF-8")) {
            LOG.info("Creating openshift resources for integration from file {}", openshiftResources.toAbsolutePath());
            createdResources = OpenshiftClient.get().load(is).createOrReplace();
        } catch (IOException e) {
            throw new RuntimeException("Unable to read openshift.yml resource: ", e);
        }

        waitForImageStream(name);
        Path filePath;
        if (QuarkusConfiguration.isQuarkusNative()) {
            filePath = createTar(integrationTarget.resolve(name + "-1.0.0-SNAPSHOT-runner"));
        } else {
            filePath = createTar(integrationTarget.resolve("quarkus-app"));
        }
        OpenshiftClient.get().doS2iBuild(name, filePath);
        log = new OpenshiftLog(p -> p.getMetadata().getLabels().containsKey("app.kubernetes.io/name")
            && name.equals(p.getMetadata().getLabels().get("app.kubernetes.io/name")));
    }

    @Override
    public void stop() {
        LOG.info("Collecting logs of integration {}", name);
        org.jboss.fuse.tnb.common.utils.IOUtils.writeFile(TestConfiguration.appLocation().resolve(name + ".log"), getLog().toString());
        LOG.info("Undeploying integration resources");
        for (HasMetadata createdResource : createdResources) {
            LOG.debug("Undeploying {} {}", createdResource.getKind(), createdResource.getMetadata().getName());
            OpenshiftClient.get().resource(createdResource).withPropagationPolicy(DeletionPropagation.BACKGROUND).delete();
        }
    }

    @Override
    public boolean isReady() {
        return ResourceFunctions.areExactlyNPodsRunning(1).apply(OpenshiftClient.get().getLabeledPods("app.kubernetes.io/name", name))
            && isCamelStarted();
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
