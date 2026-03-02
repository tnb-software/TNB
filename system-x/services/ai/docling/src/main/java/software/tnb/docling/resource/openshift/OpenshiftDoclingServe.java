package software.tnb.docling.resource.openshift;

import software.tnb.common.config.OpenshiftConfiguration;
import software.tnb.common.deployment.OpenshiftDeployable;
import software.tnb.common.deployment.WithName;
import software.tnb.common.openshift.OpenshiftClient;
import software.tnb.common.utils.NetworkUtils;
import software.tnb.common.utils.WaitUtils;
import software.tnb.common.utils.waiter.Waiter;
import software.tnb.docling.service.DoclingServe;

import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.auto.service.AutoService;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import io.fabric8.kubernetes.api.model.ContainerPort;
import io.fabric8.kubernetes.api.model.ContainerPortBuilder;
import io.fabric8.kubernetes.api.model.IntOrString;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.ServiceBuilder;
import io.fabric8.kubernetes.api.model.Volume;
import io.fabric8.kubernetes.api.model.VolumeBuilder;
import io.fabric8.kubernetes.api.model.VolumeMount;
import io.fabric8.kubernetes.api.model.VolumeMountBuilder;
import io.fabric8.kubernetes.client.PortForward;
import io.fabric8.openshift.api.model.RouteBuilder;

@AutoService(DoclingServe.class)
public class OpenshiftDoclingServe extends DoclingServe implements OpenshiftDeployable, WithName {
    private static final Logger LOG = LoggerFactory.getLogger(OpenshiftDoclingServe.class);

    private PortForward portForward;
    private int localPort;

    @Override
    public void undeploy() {
        OpenshiftClient.get().apps().deployments().withName(name()).delete();
        OpenshiftClient.get().services().withLabel(OpenshiftConfiguration.openshiftDeploymentLabel(), name()).delete();
        OpenshiftClient.get().routes().withLabel(OpenshiftConfiguration.openshiftDeploymentLabel(), name()).delete();
        WaitUtils.waitFor(new Waiter(() -> servicePod() == null, "Waiting until the pod is removed"));
    }

    @Override
    public void openResources() {
        localPort = NetworkUtils.getFreePort();
        portForward = OpenshiftClient.get().services().withName(name()).portForward(PORT, localPort);
        client = HttpClients.createDefault();
    }

    @Override
    public void closeResources() {
        NetworkUtils.releasePort(localPort);
        if (portForward != null) {
            try {
                portForward.close();
            } catch (Exception e) {
                LOG.warn("Unable to close DoclingServe port forward", e);
            }
        }
        if (client != null) {
            try {
                client.close();
            } catch (Exception e) {
                LOG.warn("Unable to close DoclingServe client", e);
            }
        }
    }

    @Override
    public void create() {
        List<ContainerPort> ports = new LinkedList<>();
        ports.add(new ContainerPortBuilder()
            .withName("http")
            .withProtocol("TCP")
            .withContainerPort(PORT)
            .build());

        List<Volume> volumes = new LinkedList<>();
        volumes.add(new VolumeBuilder()
            .withName("models-cache")
            .withNewEmptyDir()
            .endEmptyDir()
            .build());

        List<VolumeMount> volumeMounts = new LinkedList<>();
        volumeMounts.add(new VolumeMountBuilder()
            .withName("models-cache")
            .withMountPath("/usr/local/lib/python3.11/site-packages/rapidocr/models")
            .build());

        // Set environment variables to use writable directories
        Map<String, String> env = Map.of(
            "HOME", "/tmp",
            "XDG_CACHE_HOME", "/tmp/.cache",
            "TORCH_HOME", "/tmp/.cache/torch",
            "HF_HOME", "/tmp/.cache/huggingface",
            "TRANSFORMERS_CACHE", "/tmp/.cache/transformers"
        );

        // @formatter:off
        OpenshiftClient.get().createDeployment(Map.of(
            "name", name(),
            "image", image(),
            "ports", ports,
            "env", env,
            "volumes", volumes,
            "volumeMounts", volumeMounts
        ));

        OpenshiftClient.get().services().resource(new ServiceBuilder()
            .editOrNewMetadata()
                .withName(name())
                .addToLabels(OpenshiftConfiguration.openshiftDeploymentLabel(), name())
            .endMetadata()
            .editOrNewSpec()
                .addToSelector(OpenshiftConfiguration.openshiftDeploymentLabel(), name())
                .addNewPort()
                    .withName("http")
                    .withProtocol("TCP")
                    .withPort(PORT)
                    .withTargetPort(new IntOrString(PORT))
                .endPort()
            .endSpec()
            .build()
        ).serverSideApply();

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
                .withNewPort()
                    .withNewTargetPort(PORT)
                .endPort()
                .withNewTls()
                    .withTermination("edge")
                    .withInsecureEdgeTerminationPolicy("Redirect")
                .endTls()
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
    public String host() {
        return "localhost";
    }

    @Override
    public int port() {
        return localPort;
    }

    @Override
    public String getLogs() {
        return OpenshiftDeployable.super.getLogs();
    }

    @Override
    public String name() {
        return "docling";
    }
}
