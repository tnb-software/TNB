package software.tnb.common.deployment;

import software.tnb.common.config.TestConfiguration;
import software.tnb.common.openshift.OpenshiftClient;
import software.tnb.common.utils.WaitUtils;

import org.junit.jupiter.api.extension.ExtensionContext;

import java.util.List;
import java.util.function.Predicate;
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
        WaitUtils.waitFor(this::isReady, retries, waitTime() / retries,
            "Waiting until the " + this.getClass().getSimpleName() + " resource is ready");
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
}
