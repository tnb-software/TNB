package software.tnb.infinispan.resource.openshift.deployment;

import software.tnb.common.config.OpenshiftConfiguration;
import software.tnb.common.deployment.MicroshiftDeployable;
import software.tnb.common.deployment.WithName;
import software.tnb.common.openshift.OpenshiftClient;
import software.tnb.common.utils.WaitUtils;
import software.tnb.common.utils.waiter.Waiter;
import software.tnb.infinispan.service.Infinispan;

import org.apache.commons.io.IOUtils;

import com.google.auto.service.AutoService;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import io.fabric8.kubernetes.api.model.ConfigMapVolumeSourceBuilder;
import io.fabric8.kubernetes.api.model.ContainerPort;
import io.fabric8.kubernetes.api.model.ContainerPortBuilder;
import io.fabric8.kubernetes.api.model.HTTPGetActionBuilder;
import io.fabric8.kubernetes.api.model.IntOrString;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.Probe;
import io.fabric8.kubernetes.api.model.ProbeBuilder;
import io.fabric8.kubernetes.api.model.ServiceBuilder;
import io.fabric8.kubernetes.api.model.Volume;
import io.fabric8.kubernetes.api.model.VolumeBuilder;
import io.fabric8.kubernetes.api.model.VolumeMount;
import io.fabric8.kubernetes.api.model.VolumeMountBuilder;

@AutoService(Infinispan.class)
public class DeploymentInfinispan extends Infinispan implements MicroshiftDeployable, WithName {

    protected static final String CONFIG_MAP_NAME = "infinispan-config";

    @Override
    public void undeploy() {
        OpenshiftClient.get().apps().deployments().withName(name()).delete();
        OpenshiftClient.get().services().withLabel(OpenshiftConfiguration.openshiftDeploymentLabel(), name()).delete();
        OpenshiftClient.get().configMaps().withName(CONFIG_MAP_NAME).delete();
        WaitUtils.waitFor(new Waiter(() -> servicePod() == null, "Waiting until the pod is removed"));
    }

    @Override
    public void openResources() {

    }

    @Override
    public void closeResources() {

    }

    @Override
    public void create() {
        try {
            OpenshiftClient.get()
                .createConfigMap(CONFIG_MAP_NAME, Map.of("infinispan.xml", IOUtils.resourceToString("/infinispan.xml", StandardCharsets.UTF_8)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        final List<ContainerPort> ports = List.of(
            new ContainerPortBuilder().withContainerPort(PORT).withName(name()).build()
        );

        final Probe probe = new ProbeBuilder()
            .withHttpGet(new HTTPGetActionBuilder()
                .withPort(new IntOrString(PORT))
                .withPath("/console")
                .build()
            ).build();

        List<String> args = List.of("-c", "/user-config/infinispan.xml");
        List<Volume> volumes = List.of(
            new VolumeBuilder().withName(CONFIG_MAP_NAME)
                .withConfigMap(new ConfigMapVolumeSourceBuilder()
                    .withName(CONFIG_MAP_NAME)
                    .build()
                ).build()
        );
        List<VolumeMount> volumeMounts = List.of(
            new VolumeMountBuilder().withName(CONFIG_MAP_NAME).withMountPath("/user-config").build()
        );

        OpenshiftClient.get().createDeployment(Map.of(
            "name", name(),
            "image", image(),
            "env", containerEnvironment(),
            "ports", ports,
            "args", args,
            "volumes", volumes,
            "volumeMounts", volumeMounts,
            "readinessProbe", probe,
            "livenessProbe", probe
        ));

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
    public String name() {
        return "infinispan";
    }

    @Override
    public int getPortMapping() {
        return PORT;
    }

    @Override
    public String getHost() {
        return name();
    }
}
