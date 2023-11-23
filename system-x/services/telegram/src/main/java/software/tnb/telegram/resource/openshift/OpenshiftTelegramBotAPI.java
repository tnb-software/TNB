package software.tnb.telegram.resource.openshift;

import software.tnb.common.config.OpenshiftConfiguration;
import software.tnb.common.deployment.OpenshiftDeployable;
import software.tnb.common.deployment.WithName;
import software.tnb.common.openshift.OpenshiftClient;
import software.tnb.common.utils.WaitUtils;
import software.tnb.telegram.service.TelegramBotApi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.auto.service.AutoService;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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
import io.fabric8.kubernetes.client.dsl.PodResource;
import io.fabric8.openshift.api.model.DeploymentConfig;
import io.fabric8.openshift.api.model.DeploymentConfigBuilder;
import io.fabric8.openshift.api.model.DeploymentTriggerImageChangeParams;
import io.fabric8.openshift.api.model.ImageStream;
import io.fabric8.openshift.api.model.ImageStreamBuilder;
import io.fabric8.openshift.api.model.RouteBuilder;
import io.fabric8.openshift.api.model.RoutePortBuilder;
import io.fabric8.openshift.api.model.RouteSpecBuilder;
import io.fabric8.openshift.api.model.RouteTargetReferenceBuilder;

@AutoService(TelegramBotApi.class)
public class OpenshiftTelegramBotAPI extends TelegramBotApi implements OpenshiftDeployable, WithName {

    private static final Logger LOG = LoggerFactory.getLogger(OpenshiftTelegramBotAPI.class);

    @Override
    public void undeploy() {
        LOG.info("Undeploying Telegram Bot API");
        LOG.debug("Deleting route");
        OpenshiftClient.get().routes().withLabel(OpenshiftConfiguration.openshiftDeploymentLabel(), name()).delete();
        LOG.debug("Deleting service");
        OpenshiftClient.get().services().withLabel(OpenshiftConfiguration.openshiftDeploymentLabel(), name()).delete();
        LOG.debug("Deleting deploymentconfig {}", name());
        OpenshiftClient.get().deploymentConfigs().withName(name()).delete();
        WaitUtils.waitFor(() -> servicePod() == null, "Waiting until the pod is removed");
        LOG.debug("Deleting image stream");
        OpenshiftClient.get().imageStreams().withLabel(OpenshiftConfiguration.openshiftDeploymentLabel(), name()).delete();

    }

    @Override
    public void openResources() {

    }

    @Override
    public void closeResources() {

    }

    @Override
    public void create() {
        LOG.info("Deploying Telegram Bot API");

        Map<String, Integer> ports = Map.of(name(), getPort());
        List<ContainerPort> containerPorts = ports.entrySet().stream()
            .map(e -> new ContainerPortBuilder().withName(e.getKey()).withContainerPort(e.getValue()).withProtocol("TCP").build())
            .collect(Collectors.toList());

        // @formatter:off

        ImageStream imageStream = new ImageStreamBuilder()
            .withNewMetadata()
            .withName(name())
            .endMetadata()
            .withNewSpec()
            .addNewTag()
            .withName("latest")
            .withFrom(new ObjectReferenceBuilder().withKind("DockerImage").withName(image()).build())
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
                .withArgs(startupParams())
                .withImagePullPolicy("IfNotPresent")
                .addAllToPorts(containerPorts)
                .addAllToEnv(getEnv().entrySet().stream().map(e -> new EnvVar(e.getKey(), e.getValue(), null))
                    .collect(Collectors.toList()))
                .withLivenessProbe(new ProbeBuilder()
                    .editOrNewTcpSocket()
                    .withPort(new IntOrString(getPort()))
                    .endTcpSocket()
                    .withInitialDelaySeconds(10)
                    .build())
                .withReadinessProbe(new ProbeBuilder()
                    .editOrNewTcpSocket()
                    .withPort(new IntOrString(getPort()))
                    .endTcpSocket()
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
                    new DeploymentTriggerImageChangeParams(true, Arrays.asList(name()),
                        new ObjectReferenceBuilder().withKind("ImageStreamTag").withName(name() + ":latest").build()
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
        final PodResource pod = servicePod();
        return pod != null && pod.isReady() && OpenshiftClient.get().getLogs(pod.get())
            .contains("Create tcp listener [address:0.0.0.0][port:" + getPort() + "]");
    }

    @Override
    public boolean isDeployed() {
        final DeploymentConfig dc = OpenshiftClient.get().deploymentConfigs().withName(name()).get();
        return dc != null && !dc.isMarkedForDeletion();
    }

    @Override
    public Predicate<Pod> podSelector() {
        return WithName.super.podSelector();
    }

    @Override
    public String externalHostname() {
        return OpenshiftClient.get().services().withLabel(OpenshiftConfiguration.openshiftDeploymentLabel(), name())
            .list().getItems().stream().findFirst().orElseThrow(() -> new RuntimeException("unable to find service for " + name()))
            .getMetadata().getName();
    }

    @Override
    protected String getWorkingDir() {
        return "/tmp";
    }

    @Override
    protected String getUploadDir() {
        return getWorkingDir();
    }

    @Override
    public String getLogs() {
        return OpenshiftClient.get().getLogs(servicePod().get());
    }

    @Override
    public String name() {
        return "telegram-api";
    }
}
