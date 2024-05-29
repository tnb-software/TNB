package software.tnb.fhir.resource.openshift;

import software.tnb.common.config.OpenshiftConfiguration;
import software.tnb.common.deployment.ReusableOpenshiftDeployable;
import software.tnb.common.deployment.WithInClusterHostname;
import software.tnb.common.deployment.WithName;
import software.tnb.common.openshift.OpenshiftClient;
import software.tnb.common.utils.WaitUtils;
import software.tnb.fhir.service.Fhir;

import com.google.auto.service.AutoService;

import java.util.function.Predicate;
import java.util.stream.Collectors;

import io.fabric8.kubernetes.api.model.EnvVar;
import io.fabric8.kubernetes.api.model.IntOrString;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.Probe;
import io.fabric8.kubernetes.api.model.ProbeBuilder;
import io.fabric8.kubernetes.api.model.ServiceBuilder;
import io.fabric8.kubernetes.api.model.VolumeMountBuilder;
import io.fabric8.kubernetes.api.model.apps.DeploymentBuilder;

@AutoService(Fhir.class)
public class OpenshiftFhir extends Fhir implements ReusableOpenshiftDeployable, WithName, WithInClusterHostname {

    @Override
    public void undeploy() {
        OpenshiftClient.get().apps().deployments().withName(name()).delete();
        OpenshiftClient.get().services().withLabel(OpenshiftConfiguration.openshiftDeploymentLabel(), name()).delete();
        WaitUtils.waitFor(() -> servicePod() == null, "Waiting until the pod is removed");
    }

    @Override
    public void create() {

        final Probe probe = new ProbeBuilder()
            .editOrNewHttpGet()
                .withPort(new IntOrString(PORT))
                .withPath("/fhir/metadata").withScheme("HTTP")
            .endHttpGet()
            .withInitialDelaySeconds(60)
            .withTimeoutSeconds(5)
            .withFailureThreshold(10)
            .build();

        OpenshiftClient.get().apps().deployments().createOrReplace(new DeploymentBuilder()
            .withNewMetadata()
                .withName(name())
                .addToLabels(OpenshiftConfiguration.openshiftDeploymentLabel(), name())
            .endMetadata()
                .editOrNewSpec()
                    .withNewSelector()
                        .addToMatchLabels(OpenshiftConfiguration.openshiftDeploymentLabel(), name())
                    .endSelector()
                    .withReplicas(1)
                    .editOrNewTemplate()
                        .editOrNewMetadata()
                            .addToLabels(OpenshiftConfiguration.openshiftDeploymentLabel(), name())
                        .endMetadata()
                        .editOrNewSpec()
                            .addNewVolume()
                                .withName("data").withNewEmptyDir().endEmptyDir()
                            .endVolume()
                            .addNewContainer()
                                .withName(name())
                                .withImage(image())
                                .addNewPort()
                                    .withContainerPort(PORT)
                                    .withName(name())
                                .endPort()
                                .addAllToEnv(containerEnvironment().entrySet().stream().map(e -> new EnvVar(e.getKey(), e.getValue(), null))
                                    .collect(Collectors.toList()))
                                .withVolumeMounts(new VolumeMountBuilder()
                                    .withName("data")
                                    .withMountPath("/app/target")
                                    .build())
                                .withReadinessProbe(probe)
                                .withLivenessProbe(probe)
                            .endContainer()
                        .endSpec()
                    .endTemplate()
                .endSpec()
            .build());

        OpenshiftClient.get().services().createOrReplace(new ServiceBuilder()
            .editOrNewMetadata()
                .withName(name())
                .addToLabels(OpenshiftConfiguration.openshiftDeploymentLabel(), name())
            .endMetadata()
            .editOrNewSpec()
                .addToSelector(OpenshiftConfiguration.openshiftDeploymentLabel(), name())
                .addNewPort()
                    .withName(name())
                    .withProtocol("TCP")
                    .withPort(PORT)
                    .withTargetPort(new IntOrString(PORT))
            .endPort()
            .endSpec()
            .build());
    }

    @Override
    public boolean isDeployed() {
        return OpenshiftClient.get().apps().deployments().withLabel(OpenshiftConfiguration.openshiftDeploymentLabel(), name()).list()
            .getItems().size() > 0;
    }

    @Override
    public Predicate<Pod> podSelector() {
        return WithName.super.podSelector();
    }

    @Override
    public void cleanup() {

    }

    @Override
    public int getPortMapping() {
        return PORT;
    }

    @Override
    public String getServerUrl() {
        return String.format("http://%s:%s/fhir", inClusterHostname(), getPortMapping());
    }

    @Override
    public String name() {
        return "fhir";
    }

    @Override
    public void openResources() {

    }

    @Override
    public void closeResources() {

    }
}
