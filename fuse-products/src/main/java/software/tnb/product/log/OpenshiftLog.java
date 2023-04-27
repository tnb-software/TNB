package software.tnb.product.log;

import software.tnb.common.exception.TimeoutException;
import software.tnb.common.openshift.OpenshiftClient;
import software.tnb.common.utils.IOUtils;
import software.tnb.common.utils.StringUtils;
import software.tnb.common.utils.WaitUtils;
import software.tnb.product.rp.Attachments;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Predicate;

import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodConditionBuilder;
import io.fabric8.kubernetes.client.utils.PodStatusUtil;

public class OpenshiftLog extends Log {
    private static final Logger LOG = LoggerFactory.getLogger(OpenshiftLog.class);
    private final Predicate<Pod> podPredicate;
    private final Path logPath;

    public OpenshiftLog(Predicate<Pod> podPredicate, Path logPath) {
        this.podPredicate = podPredicate;
        this.logPath = logPath;
    }

    @Override
    public String toString() {
        // This method is called when the integration is started correctly, so find such pod where all containers are ready
        // sometimes in case of knative integrations it's possible that it gets the pod that is in terminating state (due to auto scaling to 0)
        Predicate<Pod> readyPredicate = podPredicate.and(p ->
            "True".equals(p.getStatus().getConditions().stream().filter(c -> "ContainersReady".equals(c.getType())).findFirst()
                .orElse(new PodConditionBuilder().withStatus("False").build()).getStatus()));
        Optional<Pod> podOptional = OpenshiftClient.get().pods().list().getItems().stream().filter(readyPredicate).findFirst();

        if (podOptional.isEmpty()) {
            LOG.trace("Specified pod doesn't exist (yet), returning empty string as logs");
            return "";
        } else {
            try {
                return StringUtils.removeColorCodes(OpenshiftClient.get().getLogs(podOptional.get()));
            } catch (Exception e) {
                LOG.error("Exception while getting logs: " + e.getMessage() + ", returning empty string");
                return "";
            }
        }
    }

    public String toString(boolean started) {
        if (started) {
            return toString();
        }

        if (OpenshiftClient.get().pods().list().getItems().stream().filter(podPredicate).findFirst().isEmpty()) {
            // It can happen that the integration wasn't built at all, so no pod will be present
            return "<No integration pod, probably the build of the integration failed>";
        }

        try {
            WaitUtils.waitFor(
                () -> {
                    try {
                        return OpenshiftClient.get().pods().list().getItems().stream().filter(podPredicate).findFirst().filter(this::podFailed)
                            .isPresent();
                    } catch (Exception ignored) {
                        return false;
                    }
                },
                60,
                1000,
                "Waiting until the pod is terminated to collect logs from failed integration");
            Pod p = OpenshiftClient.get().pods().list().getItems().stream().filter(podPredicate).findFirst().get();

            return StringUtils.removeColorCodes(OpenshiftClient.get().pods().withName(p.getMetadata().getName())
                .inContainer(OpenshiftClient.get().getIntegrationContainer(p)).terminated().getLog());
        } catch (TimeoutException e) {
            LOG.error("Unable to find terminated pod to collect logs from failed integration");
            return "<Timed out waiting to have a terminated pod>";
        }
    }

    private boolean podFailed(Pod p) {
        return PodStatusUtil.getContainerStatus(p).stream().anyMatch(containerStatus ->
            "error".equalsIgnoreCase(containerStatus.getLastState().getTerminated().getReason()));
    }

    public void save(boolean started) {
        LOG.info("Saving integration logs to {}", logPath);
        IOUtils.writeFile(
            logPath,
            toString(started)
        );
        Attachments.addAttachment(logPath);
    }

    @Override
    public void save() {
        save(true);
    }
}
