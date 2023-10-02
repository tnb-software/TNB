package software.tnb.product.ck.log;

import software.tnb.common.openshift.OpenshiftClient;
import software.tnb.common.utils.WaitUtils;
import software.tnb.product.application.Phase;
import software.tnb.product.log.stream.LogStream;
import software.tnb.product.log.stream.OpenshiftLogStream;

import org.apache.camel.v1.Integration;
import org.apache.camel.v1.IntegrationKit;

import java.util.function.Predicate;

import io.fabric8.kubernetes.api.model.Pod;

/**
 * Handles the log streaming from camel-k operator and should capture the maven build.
 * <p>
 * Starts the log stream when the integration is created
 * Ends once if either the kit is in ready state (was reused) or when the kit build pod appears
 */
public class MavenBuildLogHandler implements Runnable {
    private final String integrationName;
    private LogStream logStream;

    public MavenBuildLogHandler(String integrationName) {
        this.integrationName = integrationName;
    }

    @Override
    public void run() {
        Predicate<Pod> operatorPod = p -> p.getMetadata().getLabels().containsKey("camel.apache.org/component")
            && p.getMetadata().getLabels().get("camel.apache.org/component").equals("operator");

        logStream = new OpenshiftLogStream(operatorPod, LogStream.marker(integrationName, Phase.GENERATE));
        String kitName = null;
        // Wait until the kit name is generated
        while (kitName == null) {
            WaitUtils.sleep(1000L);
            try {
                kitName = OpenshiftClient.get().resources(Integration.class).withName(integrationName).get()
                    .getStatus().getIntegrationKit().getName();
            } catch (Exception ignored) {
            }
        }

        String finalKitName = kitName;
        Predicate<Pod> kitPodSelector = p -> p.getMetadata().getLabels().containsKey("openshift.io/build.name")
            && p.getMetadata().getLabels().get("openshift.io/build.name").contains("camel-k-" + finalKitName);
        // Stop streaming the logs when the kit build pod appears
        // or when the kit is in Ready phase (it was already built and it is reused)
        while (OpenshiftClient.get().pods().list().getItems().stream().noneMatch(kitPodSelector)
            && !"Ready".equals(OpenshiftClient.get().resources(IntegrationKit.class).withName(kitName).get().getStatus().getPhase())
        ) {
            WaitUtils.sleep(1000);
        }
        logStream.stop();
    }

    public void stop() {
        logStream.stop();
    }
}
