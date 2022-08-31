package software.tnb.infinispan.resource.openshift;

import software.tnb.common.config.OpenshiftConfiguration;
import software.tnb.common.deployment.ReusableOpenshiftDeployable;
import software.tnb.common.deployment.WithName;
import software.tnb.common.openshift.OpenshiftClient;
import software.tnb.infinispan.service.Infinispan;

import org.apache.commons.io.IOUtils;

import com.google.auto.service.AutoService;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import cz.xtf.core.openshift.OpenShiftWaiters;
import cz.xtf.core.openshift.helpers.ResourceFunctions;
import io.fabric8.kubernetes.api.model.ConfigMapVolumeSourceBuilder;
import io.fabric8.kubernetes.api.model.EnvVar;
import io.fabric8.kubernetes.api.model.HTTPGetActionBuilder;
import io.fabric8.kubernetes.api.model.IntOrString;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.Probe;
import io.fabric8.kubernetes.api.model.ProbeBuilder;
import io.fabric8.kubernetes.api.model.ServiceBuilder;
import io.fabric8.openshift.api.model.DeploymentConfigBuilder;

@AutoService(Infinispan.class)
public class OpenshiftInfinispan extends Infinispan implements ReusableOpenshiftDeployable, WithName {

    @Override
    public void undeploy() {
        OpenshiftClient.get().deploymentConfigs().withName(name()).delete();
        OpenshiftClient.get().services().withLabel(OpenshiftConfiguration.openshiftDeploymentLabel(), name()).delete();
        OpenShiftWaiters.get(OpenshiftClient.get(), () -> false)
            .areNoPodsPresent(OpenshiftConfiguration.openshiftDeploymentLabel(), name()).timeout(120_000).waitFor();
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
                .createConfigMap("infinispan-config", Map.of("infinispan.xml", IOUtils.resourceToString("/infinispan.xml", StandardCharsets.UTF_8)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        final Probe probe = new ProbeBuilder()
            .withHttpGet(new HTTPGetActionBuilder()
                .withPort(new IntOrString(PORT))
                .withPath("/console")
                .build()
            ).build();

        OpenshiftClient.get().deploymentConfigs().createOrReplace(new DeploymentConfigBuilder()
            .withNewMetadata()
                .withName(name())
                .addToLabels(OpenshiftConfiguration.openshiftDeploymentLabel(), name())
            .endMetadata()
                .editOrNewSpec()
                    .addToSelector(OpenshiftConfiguration.openshiftDeploymentLabel(), name())
                    .withReplicas(1)
                    .editOrNewTemplate()
                        .editOrNewMetadata()
                            .addToLabels(OpenshiftConfiguration.openshiftDeploymentLabel(), name())
                        .endMetadata()
                        .editOrNewSpec()
                            .addNewContainer()
                                .withName(name())
                                .withImage(defaultImage())
                                .withArgs("-c", "/user-config/infinispan.xml")
                                .addNewPort()
                                    .withContainerPort(PORT)
                                    .withName(name())
                                .endPort()
                                .addAllToEnv(containerEnvironment().entrySet().stream().map(e -> new EnvVar(e.getKey(), e.getValue(), null))
                                    .collect(Collectors.toList()))
                                .addNewVolumeMount()
                                    .withName("infinispan-config")
                                    .withMountPath("/user-config")
                                .endVolumeMount()
                                .withReadinessProbe(probe)
                                .withLivenessProbe(probe)
                            .endContainer()
                            .addNewVolume()
                                .withName("infinispan-config")
                                .withConfigMap(new ConfigMapVolumeSourceBuilder()
                                    .withName("infinispan-config")
                                    .build())
                            .endVolume()
                        .endSpec()
                    .endTemplate()
                    .addNewTrigger()
                        .withType("ConfigChange")
                    .endTrigger()
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
    public boolean isReady() {
        List<Pod> list = OpenshiftClient.get().getLabeledPods(OpenshiftConfiguration.openshiftDeploymentLabel(), name());
        return ResourceFunctions.areExactlyNPodsReady(1).apply(list);
    }

    @Override
    public boolean isDeployed() {
        return OpenshiftClient.get().getLabeledPods(OpenshiftConfiguration.openshiftDeploymentLabel(), name()).size() > 0;
    }

    @Override
    public void cleanup() {

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
