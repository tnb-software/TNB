package software.tnb.common.utils;

import java.util.List;
import java.util.function.Function;

import io.fabric8.kubernetes.api.model.Pod;

// FIXME: copied from xtf, figure out license
public final class ResourceFunctions {
    public static Function<List<Pod>, Boolean> areExactlyNPodsReady(int n) {
        return (pods) -> pods.stream().filter(ResourceParsers::isPodReady).count() == (long) n;
    }

    public static Function<List<Pod>, Boolean> areExactlyNPodsRunning(int n) {
        return (pods) -> pods.stream().filter(ResourceParsers::isPodRunning).count() == (long) n;
    }

    public static Function<List<Pod>, Boolean> haveAnyPodRestarted() {
        return haveAnyPodRestartedAtLeastNTimes(1);
    }

    public static Function<List<Pod>, Boolean> haveAnyPodRestartedAtLeastNTimes(int n) {
        return (pods) -> pods.stream().anyMatch((pod) -> ResourceParsers.hasPodRestartedAtLeastNTimes(pod, n));
    }

    public static boolean isSinglePodReady(List<Pod> pods) {
        return areExactlyNPodsReady(1).apply(pods);
    }

    private ResourceFunctions() {
    }
}
