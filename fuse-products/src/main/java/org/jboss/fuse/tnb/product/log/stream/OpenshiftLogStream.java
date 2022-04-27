package org.jboss.fuse.tnb.product.log.stream;

import org.jboss.fuse.tnb.common.config.TestConfiguration;
import org.jboss.fuse.tnb.common.openshift.OpenshiftClient;
import org.jboss.fuse.tnb.common.utils.WaitUtils;
import org.jboss.fuse.tnb.product.util.executor.Executor;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.apache.logging.log4j.io.IoBuilder;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import cz.xtf.core.openshift.helpers.ResourceParsers;
import io.fabric8.kubernetes.api.model.Container;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.dsl.LogWatch;

public class OpenshiftLogStream implements LogStream {
    private final Predicate<Pod> podPredicate;
    private final Marker marker;
    private final Map<String, LogWatch> watchers;
    private boolean run = true;

    public OpenshiftLogStream(Predicate<Pod> podPredicate, String marker) {
        this.podPredicate = podPredicate;
        this.watchers = new HashMap<>();

        // Create a marker that will be printed in the logs
        this.marker = MarkerManager.getMarker(marker);
        // Add a new parent to the marker, with the start time - this is used to filter logs in CamelKOperatorLogFilter
        // (you can have only one marker in the log event, but you can avoid it with marker parents)
        this.marker.addParents(MarkerManager.getMarker(Instant.now().toEpochMilli() + ""));

        if (TestConfiguration.streamLogs()) {
            start();
        }
    }

    /**
     * Runs a loop that checks the pods in the namespace, filtered by the given predicate.
     * For each pod start/stop the log stream, depending on the pod state
     */
    private void start() {
        Executor.get().submit(() -> {
            while (run) {
                WaitUtils.sleep(1000);
                List<Pod> pods = OpenshiftClient.get().pods().list().getItems().stream().filter(podPredicate).collect(Collectors.toList());
                for (Pod p : pods) {
                    if (p.getMetadata().getDeletionTimestamp() != null) {
                        if (watchers.containsKey(p.getMetadata().getName())) {
                            stopWatch(p);
                        }
                    } else {
                        if (!watchers.containsKey(p.getMetadata().getName()) && ResourceParsers.isPodReady(p)) {
                            startWatch(p);
                        }
                    }
                }
            }
        });
    }

    /**
     * Start the log stream for given pod.
     *
     * If the pod has multiple containers, use "integration" container (for knative integrations)
     * @param pod pod
     */
    private void startWatch(Pod pod) {
        String container;
        List<Container> containerList = OpenshiftClient.get().getAllContainers(pod);
        if (containerList.size() > 1) {
            Optional<Container> integrationContainer = containerList.stream().filter(c -> "integration".equalsIgnoreCase(c.getName())).findFirst();
            if (integrationContainer.isEmpty()) {
                throw new RuntimeException("There were multiple containers in pod and \"integration\" container was not present");
            } else {
                container = integrationContainer.get().getName();
            }
        } else {
            container = containerList.get(0).getName();
        }

        watchers.put(
            pod.getMetadata().getName(),
            OpenshiftClient.get().pods().withName(pod.getMetadata().getName()).inContainer(container)
                .watchLog(IoBuilder.forLogger(OpenshiftLogStream.class).setMarker(marker).setLevel(Level.INFO).buildOutputStream())
        );
    }

    /**
     * Stop the watch for given pod name.
     *
     * After stopping the log stream, don't remove the pod name from the map, so that the watcher isn't started back in the next loop step
     * (for example, the log stream is stopped before the integration pod is stopped, so it is very likely, that the next loop step would see
     * the integration pod as running and it would start the stream again)
     *
     * @param podName pod name
     */
    private void stopWatch(String podName) {
        if (watchers.get(podName) != null) {
            watchers.get(podName).close();
            watchers.put(podName, null);
        }
    }

    private void stopWatch(Pod pod) {
        stopWatch(pod.getMetadata().getName());
    }

    @Override
    public void stop() {
        run = false;
        watchers.keySet().forEach(this::stopWatch);
    }
}
