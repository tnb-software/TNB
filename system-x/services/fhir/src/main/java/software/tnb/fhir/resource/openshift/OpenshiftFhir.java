package software.tnb.fhir.resource.openshift;

import software.tnb.common.config.OpenshiftConfiguration;
import software.tnb.common.deployment.ReusableOpenshiftDeployable;
import software.tnb.common.deployment.WithInClusterHostname;
import software.tnb.common.deployment.WithName;
import software.tnb.common.openshift.OpenshiftClient;
import software.tnb.common.utils.WaitUtils;
import software.tnb.fhir.service.Fhir;

import com.google.auto.service.AutoService;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import io.fabric8.kubernetes.api.model.ContainerPort;
import io.fabric8.kubernetes.api.model.ContainerPortBuilder;
import io.fabric8.kubernetes.api.model.IntOrString;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.Probe;
import io.fabric8.kubernetes.api.model.ProbeBuilder;
import io.fabric8.kubernetes.api.model.ServiceBuilder;
import io.fabric8.kubernetes.api.model.Volume;
import io.fabric8.kubernetes.api.model.VolumeBuilder;
import io.fabric8.kubernetes.api.model.VolumeMount;
import io.fabric8.kubernetes.api.model.VolumeMountBuilder;

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
        List<ContainerPort> ports = List.of(
            new ContainerPortBuilder().withName(name()).withContainerPort(PORT).build()
        );

        final Probe probe = new ProbeBuilder()
            .editOrNewHttpGet()
                .withPort(new IntOrString(PORT))
                .withPath("/fhir/metadata").withScheme("HTTP")
            .endHttpGet()
            .withInitialDelaySeconds(60)
            .withTimeoutSeconds(5)
            .withFailureThreshold(10)
            .build();
        List<Volume> volumes = List.of(
            new VolumeBuilder().withNewEmptyDir().endEmptyDir().withName("data").build()
        );
        List<VolumeMount> volumeMounts = List.of(
            new VolumeMountBuilder().withMountPath("/app/target").withName("data").build()
        );

        OpenshiftClient.get().createDeployment(Map.of(
            "name", name(),
            "image", image(),
            "env", containerEnvironment(),
            "ports", ports,
            "readinessProbe", probe,
            "livenessProbe", probe,
            "volumes", volumes,
            "volumeMounts", volumeMounts
        ));

        // @formatter:off
        OpenshiftClient.get().services().resource(new ServiceBuilder()
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
            .build()
        ).serverSideApply();
        // @formatter:on
    }

    @Override
    public boolean isDeployed() {
        return WithName.super.isDeployed();
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
