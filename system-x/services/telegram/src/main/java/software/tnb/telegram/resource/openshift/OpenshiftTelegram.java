package software.tnb.telegram.resource.openshift;

import software.tnb.common.config.OpenshiftConfiguration;
import software.tnb.common.deployment.OpenshiftDeployable;
import software.tnb.common.deployment.WithName;
import software.tnb.common.openshift.OpenshiftClient;
import software.tnb.common.utils.WaitUtils;
import software.tnb.telegram.service.Telegram;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.auto.service.AutoService;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import io.fabric8.kubernetes.api.model.ContainerPort;
import io.fabric8.kubernetes.api.model.ContainerPortBuilder;
import io.fabric8.kubernetes.api.model.HTTPGetActionBuilder;
import io.fabric8.kubernetes.api.model.IntOrString;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.Probe;
import io.fabric8.kubernetes.api.model.ProbeBuilder;
import io.fabric8.kubernetes.api.model.ServiceBuilder;
import io.fabric8.kubernetes.api.model.ServicePortBuilder;
import io.fabric8.openshift.api.model.RouteBuilder;
import io.fabric8.openshift.api.model.RoutePortBuilder;
import io.fabric8.openshift.api.model.RouteSpecBuilder;
import io.fabric8.openshift.api.model.RouteTargetReferenceBuilder;

@AutoService(Telegram.class)
public class OpenshiftTelegram extends Telegram implements OpenshiftDeployable, WithName {

    private static final Logger LOG = LoggerFactory.getLogger(OpenshiftTelegram.class);
    private static final int HTTP_PORT = 8080;

    @Override
    public void undeploy() {
        LOG.info("Undeploying Telegram client");
        LOG.debug("Deleting route");
        OpenshiftClient.get().routes().withLabel(OpenshiftConfiguration.openshiftDeploymentLabel(), name()).delete();
        LOG.debug("Deleting service");
        OpenshiftClient.get().services().withLabel(OpenshiftConfiguration.openshiftDeploymentLabel(), name()).delete();
        LOG.debug("Deleting deployment");
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
        LOG.info("Deploying Telegram client");

        Map<String, Integer> ports = Map.of(name(), HTTP_PORT);
        List<ContainerPort> containerPorts = ports.entrySet().stream()
            .map(e -> new ContainerPortBuilder().withName(e.getKey()).withContainerPort(e.getValue()).withProtocol("TCP").build())
            .collect(Collectors.toList());

        final Probe probe = new ProbeBuilder()
            .withHttpGet(new HTTPGetActionBuilder().withPath("/health").withPort(new IntOrString(HTTP_PORT)).build())
            .withTimeoutSeconds(10)
            .build();

        OpenshiftClient.get().createDeployment(Map.of(
            "name", name(),
            "image", image(),
            "env", getEnv(),
            "ports", containerPorts,
            "readinessProbe", probe,
            "replicas", 1
        ));

        LOG.debug("Creating service {}", name());
        OpenshiftClient.get().services().resource(
            new ServiceBuilder()
                .editOrNewMetadata()
                    .withName(name())
                    .addToLabels(OpenshiftConfiguration.openshiftDeploymentLabel(), name())
                .endMetadata()
                .editOrNewSpec()
                    .addToSelector(OpenshiftConfiguration.openshiftDeploymentLabel(), name())
                    .addToPorts(new ServicePortBuilder()
                        .withName(name())
                        .withPort(HTTP_PORT)
                        .withTargetPort(new IntOrString(HTTP_PORT))
                        .build()
                    )
                .endSpec()
                .build()
        ).serverSideApply();

        LOG.debug("Creating route {}", name());
        OpenshiftClient.get().routes().resource(
            new RouteBuilder()
                .editOrNewMetadata()
                    .withName(name())
                    .addToLabels(OpenshiftConfiguration.openshiftDeploymentLabel(), name())
                .endMetadata()
                .editOrNewSpecLike(new RouteSpecBuilder()
                    .withTo(new RouteTargetReferenceBuilder()
                        .withName(name())
                        .withKind("Service")
                        .build())
                    .withPort(new RoutePortBuilder()
                        .withTargetPort(new IntOrString(HTTP_PORT))
                        .build())
                    .build())
                .endSpec()
                .build()
        ).serverSideApply();
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
    protected String getHttpEndpoint() {
        return "http://" + OpenshiftClient.get().routes().withName(name()).get().getSpec().getHost();
    }

    @Override
    public String name() {
        return "telegram-client";
    }
}
