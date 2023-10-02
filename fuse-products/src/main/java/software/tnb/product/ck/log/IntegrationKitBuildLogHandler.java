package software.tnb.product.ck.log;

import software.tnb.common.openshift.OpenshiftClient;
import software.tnb.common.utils.IOUtils;
import software.tnb.common.utils.WaitUtils;
import software.tnb.product.application.Phase;
import software.tnb.product.log.stream.LogStream;
import software.tnb.product.log.stream.OpenshiftLogStream;
import software.tnb.product.rp.Attachments;

import org.apache.camel.v1.Integration;
import org.apache.camel.v1.IntegrationKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Predicate;

import cz.xtf.core.openshift.helpers.ResourceParsers;
import io.fabric8.kubernetes.api.model.Pod;

/**
 * Handles the log streaming from integration kit build.
 * <p>
 * Starts the log stream when the integration kit build pod appears and ends the streaming when it's done
 */
public class IntegrationKitBuildLogHandler implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(IntegrationKitBuildLogHandler.class);
    private final String integrationName;
    private final Path logPath;

    public IntegrationKitBuildLogHandler(String integrationName, Path logPath) {
        this.integrationName = integrationName;
        this.logPath = logPath;
    }

    @Override
    public void run() {
        String kitName = null;
        // Wait until the kit name is generated
        while (kitName == null) {
            WaitUtils.sleep(1000L);
            try {
                kitName = OpenshiftClient.get().resources(Integration.class).withName(integrationName).get().getStatus().getIntegrationKit()
                    .getName();
            } catch (Exception ignored) {
            }
        }

        try {
            if ("Ready".equals(OpenshiftClient.get().resources(IntegrationKit.class).withName(kitName).get().getStatus().getPhase())) {
                // Kit already exists
                return;
            }
        } catch (Exception ignored) {
        }

        String finalKitName = kitName;
        Predicate<Pod> kitPodSelector = p -> p.getMetadata().getLabels().containsKey("openshift.io/build.name")
            && p.getMetadata().getLabels().get("openshift.io/build.name").contains("camel-k-" + finalKitName);

        boolean podRunning = false;
        // Wait until the kit build pod is running
        while (!podRunning) {
            WaitUtils.sleep(1000L);

            Optional<Pod> kitBuildPod = getKitBuildPod(kitPodSelector);
            if (kitBuildPod.isPresent()) {
                podRunning = ResourceParsers.isPodReady(kitBuildPod.get());
            }
        }

        LogStream logStream = new OpenshiftLogStream(kitPodSelector, LogStream.marker(integrationName, Phase.BUILD));
        // Stop streaming the logs when the build pod ends
        while (ResourceParsers.isPodRunning(getKitBuildPod(kitPodSelector).get())) {
            WaitUtils.sleep(1000);
        }
        logStream.stop();

        // Save the logs of the build pod
        LOG.info("Collecting logs of integration kit {}", kitName);
        IOUtils.writeFile(logPath, OpenshiftClient.get().getLogs(getKitBuildPod(kitPodSelector).get()));
        Attachments.addAttachment(logPath);
    }

    private Optional<Pod> getKitBuildPod(Predicate<Pod> podPredicate) {
        try {
            return OpenshiftClient.get().pods().list().getItems().stream().filter(podPredicate).findFirst();
        } catch (Exception ignored) {
            return Optional.empty();
        }
    }
}
