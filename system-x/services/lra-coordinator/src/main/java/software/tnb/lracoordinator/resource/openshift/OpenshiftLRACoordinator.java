package software.tnb.lracoordinator.resource.openshift;

import software.tnb.common.config.OpenshiftConfiguration;
import software.tnb.common.deployment.OpenshiftDeployable;
import software.tnb.common.deployment.WithExternalHostname;
import software.tnb.common.deployment.WithInClusterHostname;
import software.tnb.common.deployment.WithName;
import software.tnb.common.openshift.OpenshiftClient;
import software.tnb.lracoordinator.service.LRACoordinator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.auto.service.AutoService;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import cz.xtf.core.openshift.OpenShiftWaiters;
import cz.xtf.core.openshift.helpers.ResourceFunctions;
import io.fabric8.kubernetes.api.model.ContainerPort;
import io.fabric8.kubernetes.api.model.ContainerPortBuilder;
import io.fabric8.kubernetes.api.model.EnvVar;
import io.fabric8.kubernetes.api.model.IntOrString;
import io.fabric8.kubernetes.api.model.ObjectReferenceBuilder;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.ProbeBuilder;
import io.fabric8.kubernetes.api.model.ServiceBuilder;
import io.fabric8.kubernetes.api.model.ServicePortBuilder;
import io.fabric8.kubernetes.api.model.ServiceSpecBuilder;
import io.fabric8.openshift.api.model.DeploymentConfigBuilder;
import io.fabric8.openshift.api.model.DeploymentTriggerImageChangeParams;
import io.fabric8.openshift.api.model.ImageStream;
import io.fabric8.openshift.api.model.ImageStreamBuilder;
import io.fabric8.openshift.api.model.RouteBuilder;
import io.fabric8.openshift.api.model.RoutePortBuilder;
import io.fabric8.openshift.api.model.RouteSpecBuilder;
import io.fabric8.openshift.api.model.RouteTargetReferenceBuilder;

@AutoService(LRACoordinator.class)
public class OpenshiftLRACoordinator extends LRACoordinator implements OpenshiftDeployable, WithName, WithInClusterHostname, WithExternalHostname {
    private static final Logger LOG = LoggerFactory.getLogger(OpenshiftLRACoordinator.class);

    private long uid;

    @Override
    public void undeploy() {
        LOG.info("Undeploying OpenShift LRA Coordinator");
        LOG.debug("Deleting route");
        OpenshiftClient.get().routes().withLabel(OpenshiftConfiguration.openshiftDeploymentLabel(), name()).delete();
        LOG.debug("Deleting service");
        OpenshiftClient.get().services().withLabel(OpenshiftConfiguration.openshiftDeploymentLabel(), name()).delete();
        LOG.debug("Deleting deploymentconfig {}", name());
        OpenshiftClient.get().deploymentConfigs().withName(name()).delete();
        OpenShiftWaiters.get(OpenshiftClient.get(), () -> false).areNoPodsPresent(OpenshiftConfiguration.openshiftDeploymentLabel(), name())
            .timeout(120_000).waitFor();
        LOG.debug("Deleting image stream");
        OpenshiftClient.get().imageStreams().withLabel(OpenshiftConfiguration.openshiftDeploymentLabel(), name()).delete();
    }

    @Override
    public String getLog() {
        return OpenshiftClient.get().pods().withName(OpenshiftClient.get().getAnyPod(name()).getMetadata().getName()).getLog();
    }

    @Override
    public String getUrl() {
        return String.format("http://%s:%s", hostname(), port());
    }

    @Override
    public String getExternalUrl() {
        return String.format("http://%s", OpenshiftClient.get().routes().withLabel(OpenshiftConfiguration.openshiftDeploymentLabel(), name())
            .list().getItems().stream().findFirst().orElseThrow(() -> new RuntimeException("unable to find route for " + name()))
            .getSpec().getHost());
    }

    @Override
    public void create() {
        LOG.info("Deploying OpenShift LRA Coordinator");

        Map<String, Integer> ports = Map.of(name(), port());
        List<ContainerPort> containerPorts = ports.entrySet().stream()
            .map(e -> new ContainerPortBuilder().withName(e.getKey()).withContainerPort(e.getValue()).withProtocol("TCP").build())
            .collect(Collectors.toList());

        // @formatter:off

        ImageStream imageStream = new ImageStreamBuilder()
            .withNewMetadata()
                .withName("lra-coordinator")
            .endMetadata()
            .withNewSpec()
                .addNewTag()
                    .withName("latest")
                    .withFrom(new ObjectReferenceBuilder().withKind("DockerImage").withName(defaultImage()).build())
                .endTag()
            .endSpec()
            .build();

        OpenshiftClient.get().imageStreams().createOrReplace(imageStream);

        LOG.debug("Creating deploymentconfig {}", name());
        OpenshiftClient.get().deploymentConfigs().createOrReplace(
            new DeploymentConfigBuilder()
                .editOrNewMetadata()
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
                                .withImagePullPolicy("IfNotPresent")
                                .addAllToPorts(containerPorts)
                                .addAllToEnv(containerEnvironment().entrySet().stream().map(e -> new EnvVar(e.getKey(), e.getValue(), null))
                                    .collect(Collectors.toList()))
                                .withLivenessProbe(new ProbeBuilder()
                                    .editOrNewHttpGet()
                                        .withPath("/lra-coordinator")
                                        .withPort(new IntOrString(port()))
                                        .withScheme("HTTP")
                                    .endHttpGet()
                                    .withInitialDelaySeconds(60)
                                    .build())
                                .withReadinessProbe(new ProbeBuilder()
                                    .editOrNewHttpGet()
                                        .withPath("/lra-coordinator")
                                        .withPort(new IntOrString(port()))
                                        .withScheme("HTTP")
                                    .endHttpGet()
                                    .withInitialDelaySeconds(10)
                                    .build())
                            .endContainer()
                        .endSpec()
                    .endTemplate()
                    .addNewTrigger()
                        .withType("ConfigChange")
                    .endTrigger()
                    .addNewTrigger()
                        .withType("ImageChange")
                        .withImageChangeParams(
                            new DeploymentTriggerImageChangeParams(true, Arrays.asList("lra-coordinator"),
                                new ObjectReferenceBuilder().withKind("ImageStreamTag").withName("lra-coordinator:latest").build()
                                , null)
                        )
                    .endTrigger()
                .endSpec()
                .build()
        );

        ports.forEach((key, value) -> {
            ServiceSpecBuilder serviceSpecBuilder = new ServiceSpecBuilder().addToSelector(OpenshiftConfiguration.openshiftDeploymentLabel(), name());

            serviceSpecBuilder.addToPorts(new ServicePortBuilder()
                .withName(key)
                .withPort(value)
                .withTargetPort(new IntOrString(value))
                .build());

            LOG.debug("Creating service {}", key);
            OpenshiftClient.get().services().createOrReplace(
                new ServiceBuilder()
                    .editOrNewMetadata()
                        .withName(key)
                        .addToLabels(OpenshiftConfiguration.openshiftDeploymentLabel(), name())
                    .endMetadata()
                    .editOrNewSpecLike(serviceSpecBuilder.build())
                    .endSpec()
                    .build()
            );

            LOG.debug("Creating route {}", key);
            OpenshiftClient.get().routes().createOrReplace(
                new RouteBuilder()
                    .editOrNewMetadata()
                    .withName(key)
                    .addToLabels(OpenshiftConfiguration.openshiftDeploymentLabel(), name())
                    .endMetadata()
                    .editOrNewSpecLike(new RouteSpecBuilder()
                        .withTo(new RouteTargetReferenceBuilder()
                            .withName(key)
                            .withKind("Service")
                            .build())
                        .withPort(new RoutePortBuilder()
                            .withTargetPort(new IntOrString(value))
                            .build())
                        .build())
                    .endSpec()
                    .build()
            );
        });

        // @formatter:on
    }

    @Override
    public boolean isReady() {
        List<Pod> pods = OpenshiftClient.get().getLabeledPods(OpenshiftConfiguration.openshiftDeploymentLabel(), name());
        if (ResourceFunctions.areExactlyNPodsReady(1).apply(pods)) {
            return OpenshiftClient.get().getLogs(pods.get(0)).contains("Profile prod activated");
        }
        return false;
    }

    @Override
    public boolean isDeployed() {
        return OpenshiftClient.get().getLabeledPods(OpenshiftConfiguration.openshiftDeploymentLabel(), name()).size() != 0;
    }

    @Override
    public String hostname() {
        return OpenshiftClient.get().services().withLabel(OpenshiftConfiguration.openshiftDeploymentLabel(), name())
            .list().getItems().stream().findFirst().orElseThrow(() -> new RuntimeException("unable to find service for " + name()))
            .getMetadata().getName();
    }

    @Override
    public int port() {
        return DEFAULT_PORT;
    }

    @Override
    public String name() {
        return "lra-coordinator";
    }

    @Override
    public String externalHostname() {
        return "lra-coordinator";
    }
}
