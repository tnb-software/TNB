package software.tnb.common.deployment;

import software.tnb.common.config.OpenshiftConfiguration;
import software.tnb.common.openshift.OpenshiftClient;

import java.util.Map;
import java.util.function.Predicate;

import io.fabric8.kubernetes.api.model.Pod;

/**
 * Resource that has a name.
 */
public interface WithName {
    String name();

    default Predicate<Pod> podSelector() {
        return p -> OpenshiftClient.get().hasLabels(p, Map.of(OpenshiftConfiguration.openshiftDeploymentLabel(), name()));
    }
}
