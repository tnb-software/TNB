package software.tnb.common.deployment;

import io.fabric8.kubernetes.api.model.GenericKubernetesResource;

public interface WithCustomResource {
    String kind();

    String apiVersion();

    GenericKubernetesResource customResource();
}
