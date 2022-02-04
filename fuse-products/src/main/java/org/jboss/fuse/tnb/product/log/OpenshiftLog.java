package org.jboss.fuse.tnb.product.log;

import org.jboss.fuse.tnb.common.exception.TimeoutException;
import org.jboss.fuse.tnb.common.openshift.OpenshiftClient;
import org.jboss.fuse.tnb.common.utils.StringUtils;
import org.jboss.fuse.tnb.common.utils.WaitUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.function.Predicate;

import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodConditionBuilder;
import io.fabric8.kubernetes.client.utils.PodStatusUtil;

public class OpenshiftLog extends Log {
    private static final Logger LOG = LoggerFactory.getLogger(OpenshiftLog.class);
    private final Predicate<Pod> podPredicate;

    public OpenshiftLog(Predicate<Pod> podPredicate) {
        this.podPredicate = podPredicate;
    }

    @Override
    public String toString() {
        // This method is called when the integration is started correctly, so find such pod where all containers are ready
        // sometimes in case of knative integrations it's possible that it gets the pod that is in terminating state (due to auto scaling to 0)
        Predicate<Pod> readyPredicate = podPredicate.and(p ->
            "True".equals(p.getStatus().getConditions().stream().filter(c -> "ContainersReady".equals(c.getType())).findFirst()
                .orElse(new PodConditionBuilder().withStatus("False").build()).getStatus()));
        Optional<Pod> podOptional = OpenshiftClient.get().getPods().stream().filter(readyPredicate).findFirst();
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

        if (OpenshiftClient.get().getPods().stream().filter(podPredicate).findFirst().isEmpty()) {
            // It can happen that the integration wasn't built at all, so no pod will be present
            return "<No integration pod, probably the build of the integration failed>";
        }

        try {
            WaitUtils.waitFor(
                () -> OpenshiftClient.get().getPods().stream().filter(podPredicate).findFirst().filter(this::podFailed).isPresent(),
                12,
                5000,
                "Waiting until the pod is terminated to collect logs from failed integration");
            Pod p = OpenshiftClient.get().getPods().stream().filter(podPredicate).findFirst().get();
            return StringUtils.removeColorCodes(OpenshiftClient.get().pods().withName(p.getMetadata().getName()).terminated().getLog());
        } catch (TimeoutException e) {
            LOG.error("Unable to find terminated pod to collect logs from failed integration");
            return "<Timed out waiting to have a terminated pod>";
        }
    }

    private boolean podFailed(Pod p) {
        return PodStatusUtil.getContainerStatus(p).stream().anyMatch(containerStatus ->
            "error".equalsIgnoreCase(containerStatus.getLastState().getTerminated().getReason()));
    }
}
