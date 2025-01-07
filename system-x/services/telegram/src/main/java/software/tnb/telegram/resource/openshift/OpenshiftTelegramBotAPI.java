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
import io.fabric8.kubernetes.api.model.IntOrString;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.Probe;
import io.fabric8.kubernetes.api.model.ProbeBuilder;
import io.fabric8.kubernetes.api.model.ServiceBuilder;
import io.fabric8.kubernetes.api.model.ServicePortBuilder;
import io.fabric8.kubernetes.api.model.ServiceSpecBuilder;
import io.fabric8.kubernetes.api.model.TCPSocketActionBuilder;
import io.fabric8.kubernetes.client.dsl.PodResource;
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
        LOG.debug("Deleting deployment {}", name());
        OpenshiftClient.get().apps().deployments().withName(name()).delete();
        WaitUtils.waitFor(() -> servicePod() == null, "Waiting until the pod is removed");
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

        final Probe probe = new ProbeBuilder()
            .withTcpSocket(new TCPSocketActionBuilder().withPort(new IntOrString(getPort())).build())
            .withTimeoutSeconds(10)
            .build();

        OpenshiftClient.get().createDeployment(Map.of(
            "name", name(),
            "image", image(),
            "env", getEnv(),
            "ports", containerPorts,
            "args", Arrays.stream(startupParams()).toList(),
            "livenessProbe", probe,
            "readinessProbe", probe
        ));

        // @formatter:off
        ports.forEach((key, value) -> {
            ServiceSpecBuilder serviceSpecBuilder = new ServiceSpecBuilder().addToSelector(OpenshiftConfiguration.openshiftDeploymentLabel(), name());

            serviceSpecBuilder.addToPorts(new ServicePortBuilder()
                .withName(key)
                .withPort(value)
                .withTargetPort(new IntOrString(value))
                .build());

            LOG.debug("Creating service {}", key);
            OpenshiftClient.get().services().resource(
                new ServiceBuilder()
                    .editOrNewMetadata()
                        .withName(key)
                        .addToLabels(OpenshiftConfiguration.openshiftDeploymentLabel(), name())
                    .endMetadata()
                    .editOrNewSpec()
                        .addToSelector(OpenshiftConfiguration.openshiftDeploymentLabel(), name())
                        .addToPorts(new ServicePortBuilder()
                            .withName(key)
                            .withPort(value)
                            .withTargetPort(new IntOrString(value))
                            .build()
                        )
                    .endSpec()
                    .build()
            ).serverSideApply();

            LOG.debug("Creating route {}", key);
            OpenshiftClient.get().routes().resource(
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
            ).serverSideApply();
        });
        // @formatter:on
    }

    @Override
    public boolean isReady() {
        final PodResource pod = servicePod();
        return pod != null && pod.isReady() && OpenshiftClient.get().getLogs(pod.get()).toLowerCase()
            .contains(("Create tcp listener [address:0.0.0.0][port:" + getPort() + "]").toLowerCase());
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
