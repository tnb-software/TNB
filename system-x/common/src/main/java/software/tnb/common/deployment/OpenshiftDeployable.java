package software.tnb.common.deployment;

import software.tnb.common.config.OpenshiftConfiguration;
import software.tnb.common.config.TestConfiguration;
import software.tnb.common.exception.TimeoutException;
import software.tnb.common.openshift.OpenshiftClient;
import software.tnb.common.utils.WaitUtils;
import software.tnb.common.utils.waiter.Waiter;

import org.junit.jupiter.api.extension.ExtensionContext;

import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.KubernetesClientException;
import io.fabric8.kubernetes.client.dsl.PodResource;
import io.fabric8.kubernetes.client.dsl.Resource;

public interface OpenshiftDeployable extends Deployable {
    void create();

    default boolean isReady() {
        final List<PodResource> servicePods = servicePods();
        return servicePods != null && !servicePods.isEmpty() && servicePods.stream().allMatch(Resource::isReady);
    }

    boolean isDeployed();

    default long waitTime() {
        return 300_000;
    }

    default PodResource servicePod() {
        var pods = servicePods();
        return pods.isEmpty() ? null : pods.get(0);
    }

    default List<PodResource> servicePods() {
        try {
            return OpenshiftClient.get().pods().list().getItems().stream()
                .filter(podSelector())
                .map(p -> OpenshiftClient.get().pods().withName(p.getMetadata().getName()))
                .collect(Collectors.toList());
        } catch (KubernetesClientException kce) {
            // Just in case of some transient error
            return null;
        }
    }

    Predicate<Pod> podSelector();

    @Override
    default void deploy() {
        final int retries = 60;
        if (!isDeployed()) {
            create();
        }

        Supplier<TimeoutException> exception = () -> {
            // Get all the pods in the namespace and add them to the TimeoutException message
            String message = "Timeout exceeded when deploying " + this.getClass().getSimpleName() + " service\n"
                + "Pods in the '" + OpenshiftClient.get().getNamespace() + "' namespace:\n"
                + OpenshiftClient.get().getPods().stream()
                .map(p -> "  " + p.getMetadata().getName() + " - " + p.getStatus().getPhase()).collect(Collectors.joining("\n"));
            return new TimeoutException(message);
        };

        WaitUtils.waitFor(new Waiter(this::isReady, "Waiting until the " + this.getClass().getSimpleName() + " resource is ready")
            .timeout(retries, waitTime() / retries).timeoutException(exception));
    }

    @Override
    default void afterAll(ExtensionContext extensionContext) throws Exception {
        Deployable.super.afterAll(extensionContext);
        if (TestConfiguration.parallel()) {
            // In parallel execution, each test class has its own namespace
            OpenshiftClient.deleteNamespace();
        }
    }

    @Override
    default void restart() {
        closeResources();
        servicePod().delete();
        WaitUtils.waitFor(() -> servicePods().stream().allMatch(p -> p.isReady() && !p.get().isMarkedForDeletion()),
            "Restart: Waiting until the service is restarted");
        openResources();
    }

    @Override
    default boolean enabled() {
        return OpenshiftConfiguration.isOpenshift();
    }

    @Override
    default int priority() {
        return 1;
    }
}
