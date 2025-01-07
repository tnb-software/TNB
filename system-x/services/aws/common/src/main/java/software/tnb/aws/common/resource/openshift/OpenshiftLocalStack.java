package software.tnb.aws.common.resource.openshift;

import software.tnb.aws.common.service.LocalStack;
import software.tnb.common.config.OpenshiftConfiguration;
import software.tnb.common.deployment.OpenshiftDeployable;
import software.tnb.common.deployment.WithExternalHostname;
import software.tnb.common.deployment.WithInClusterHostname;
import software.tnb.common.deployment.WithName;
import software.tnb.common.openshift.OpenshiftClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.auto.service.AutoService;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import cz.xtf.core.openshift.OpenShiftWaiters;
import io.fabric8.kubernetes.api.model.ContainerPort;
import io.fabric8.kubernetes.api.model.ContainerPortBuilder;
import io.fabric8.kubernetes.api.model.IntOrString;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.ServiceBuilder;
import io.fabric8.kubernetes.api.model.ServicePortBuilder;
import io.fabric8.kubernetes.api.model.Volume;
import io.fabric8.kubernetes.api.model.VolumeBuilder;
import io.fabric8.kubernetes.api.model.VolumeMount;
import io.fabric8.kubernetes.api.model.VolumeMountBuilder;
import io.fabric8.kubernetes.client.dsl.PodResource;
import io.fabric8.openshift.api.model.RouteBuilder;

@AutoService(LocalStack.class)
public class OpenshiftLocalStack extends LocalStack implements OpenshiftDeployable, WithName, WithInClusterHostname, WithExternalHostname {
    private static final Logger LOG = LoggerFactory.getLogger(OpenshiftLocalStack.class);

    @Override
    public void undeploy() {
        LOG.info("Undeploying OpenShift LocalStack");
        LOG.debug("Deleting route {}", name());
        OpenshiftClient.get().routes().withName(name()).delete();
        LOG.debug("Deleting service {}", name());
        OpenshiftClient.get().services().withName(name()).delete();
        LOG.debug("Deleting deployment {}", name());
        OpenshiftClient.get().apps().deployments().withName(name()).delete();
        OpenShiftWaiters.get(OpenshiftClient.get(), () -> false).areNoPodsPresent(OpenshiftConfiguration.openshiftDeploymentLabel(), name())
            .timeout(120_000).waitFor();
    }

    @Override
    public void openResources() {
    }

    @Override
    public void closeResources() {
    }

    @Override
    public void create() {
        LOG.info("Deploying OpenShift LocalStack");
        List<ContainerPort> ports = new LinkedList<>();
        ports.add(new ContainerPortBuilder()
            .withName("main")
            .withContainerPort(PORT)
            .withProtocol("TCP").build());
        List<Volume> volumes = List.of(
            new VolumeBuilder().withNewEmptyDir().endEmptyDir().withName("empty").build()
        );
        List<VolumeMount> volumeMounts = List.of(
            new VolumeMountBuilder().withMountPath("/var/lib/localstack").withName("empty").build()
        );

        OpenshiftClient.get().createDeployment(Map.of(
            "name", name(),
            "image", image(),
            "ports", ports,
            "volumes", volumes,
            "volumeMounts", volumeMounts
        ));

        // @formatter:off
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
                        .withPort(PORT)
                        .withTargetPort(new IntOrString(PORT))
                        .build()
                    )
                .endSpec()
                .build()
        ).serverSideApply();

        LOG.debug("Creating route {}", name());
        OpenshiftClient.get().routes().resource(new RouteBuilder()
            .editOrNewMetadata()
                .withName(name())
                .addToLabels(OpenshiftConfiguration.openshiftDeploymentLabel(), name())
            .endMetadata()
            .editOrNewSpec()
                .withNewTo()
                   .withKind("Service")
                    .withName(name())
                .endTo()
                .editOrNewPort()
                    .withTargetPort(new IntOrString(PORT))
                .endPort()
            .endSpec()
            .build()
        ).serverSideApply();
        // @formatter:on
    }

    @Override
    public boolean isReady() {
        final PodResource pod = servicePod();
        return pod != null && pod.isReady() && OpenshiftClient.get().getLogs(pod.get()).contains("Ready.");
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
        return "localstack";
    }

    @Override
    public String serviceUrl() {
        return "http://" + inClusterHostname() + ":" + PORT;
    }

    @Override
    public String clientUrl() {
        return externalHostname();
    }

    @Override
    public String externalHostname() {
        return "http://" + OpenshiftClient.get().routes().withName(name()).get().getSpec().getHost();
    }
}
