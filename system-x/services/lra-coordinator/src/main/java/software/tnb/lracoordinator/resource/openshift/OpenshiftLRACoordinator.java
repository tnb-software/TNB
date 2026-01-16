package software.tnb.lracoordinator.resource.openshift;

import software.tnb.common.config.OpenshiftConfiguration;
import software.tnb.common.deployment.OpenshiftDeployable;
import software.tnb.common.deployment.WithExternalHostname;
import software.tnb.common.deployment.WithInClusterHostname;
import software.tnb.common.deployment.WithName;
import software.tnb.common.openshift.OpenshiftClient;
import software.tnb.common.utils.WaitUtils;
import software.tnb.common.utils.waiter.Waiter;
import software.tnb.lracoordinator.service.LRACoordinator;

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
import io.fabric8.kubernetes.api.model.Volume;
import io.fabric8.kubernetes.api.model.VolumeBuilder;
import io.fabric8.kubernetes.api.model.VolumeMount;
import io.fabric8.kubernetes.api.model.VolumeMountBuilder;
import io.fabric8.kubernetes.client.dsl.PodResource;
import io.fabric8.openshift.api.model.RouteBuilder;
import io.fabric8.openshift.api.model.RoutePortBuilder;
import io.fabric8.openshift.api.model.RouteSpecBuilder;
import io.fabric8.openshift.api.model.RouteTargetReferenceBuilder;

@AutoService(LRACoordinator.class)
public class OpenshiftLRACoordinator extends LRACoordinator implements OpenshiftDeployable, WithName, WithInClusterHostname, WithExternalHostname {
    private static final Logger LOG = LoggerFactory.getLogger(OpenshiftLRACoordinator.class);
    protected static final String MOUNT_POINT_DEFAULT_STORE = "/home/jboss/ObjectStore/ShadowNoFileLockStore/defaultStore";
    protected static final String VOLUME_NAME = "data";

    @Override
    public void undeploy() {
        LOG.info("Undeploying OpenShift LRA Coordinator");
        LOG.debug("Deleting route");
        OpenshiftClient.get().routes().withLabel(OpenshiftConfiguration.openshiftDeploymentLabel(), name()).delete();
        LOG.debug("Deleting service");
        OpenshiftClient.get().services().withLabel(OpenshiftConfiguration.openshiftDeploymentLabel(), name()).delete();
        LOG.debug("Deleting deployment {}", name());
        OpenshiftClient.get().apps().deployments().withName(name()).delete();
        WaitUtils.waitFor(new Waiter(() -> servicePod() == null, "Waiting until the pod is removed"));
    }

    @Override
    public String getUrl() {
        return String.format("http://%s:%s", name(), port());
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

        final Probe probe = new ProbeBuilder()
            .withHttpGet(new HTTPGetActionBuilder()
                .withPort(new IntOrString(port()))
                .withPath("/lra-coordinator")
                .withScheme("HTTP")
                .build()
            )
            .withInitialDelaySeconds(60)
            .build();

        List<Volume> volumes = List.of(
            new VolumeBuilder().withNewEmptyDir().endEmptyDir().withName(VOLUME_NAME).build()
        );
        List<VolumeMount> volumeMounts = List.of(
            new VolumeMountBuilder().withMountPath(MOUNT_POINT_DEFAULT_STORE).withName(VOLUME_NAME).build()
        );

        // @formatter:off
        OpenshiftClient.get().createDeployment(Map.of(
            "name", name(),
            "image", image(),
            "env", containerEnvironment(),
            "ports", containerPorts,
            "volumes", volumes,
            "volumeMounts", volumeMounts,
            "readinessProbe", probe,
            "livenessProbe", probe
        ));

        ports.forEach((key, value) -> {
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
        return pod != null && pod.isReady() && OpenshiftClient.get().getLogs(pod.get()).contains("Profile prod activated");
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
    public String hostname() {
        return servicePod().get().getMetadata().getName();
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

    @Override
    public String getLogs() {
        return OpenshiftDeployable.super.getLogs();
    }
}
