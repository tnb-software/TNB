package org.jboss.fuse.tnb.product.log;

import org.jboss.fuse.tnb.common.openshift.OpenshiftClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.function.Predicate;

import io.fabric8.kubernetes.api.model.Pod;

public class OpenshiftLog extends Log {
    private static final Logger LOG = LoggerFactory.getLogger(OpenshiftLog.class);
    private final Predicate<Pod> podPredicate;

    public OpenshiftLog(Predicate<Pod> podPredicate) {
        this.podPredicate = podPredicate;
    }

    @Override
    public String toString() {
        Optional<Pod> podOptional = OpenshiftClient.get().getPods().stream().filter(podPredicate).findFirst();
        if (podOptional.isEmpty()) {
            LOG.trace("Specified pod doesn't exist (yet), returning empty string as logs");
            return "";
        } else {
            return OpenshiftClient.getLogs(podOptional.get());
        }
    }
}
