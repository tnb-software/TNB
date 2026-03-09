package software.tnb.ollama.resource.openshift;

import software.tnb.common.config.OpenshiftConfiguration;
import software.tnb.common.deployment.OpenshiftDeployable;
import software.tnb.common.deployment.WithExternalHostname;
import software.tnb.common.deployment.WithName;
import software.tnb.common.openshift.OpenshiftClient;
import software.tnb.common.utils.NetworkUtils;
import software.tnb.common.utils.WaitUtils;
import software.tnb.common.utils.waiter.Waiter;
import software.tnb.ollama.service.Ollama;

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
import io.fabric8.kubernetes.client.PortForward;
import io.fabric8.openshift.api.model.RouteBuilder;

@AutoService(Ollama.class)
public class OpenshiftOllama extends Ollama implements OpenshiftDeployable, WithExternalHostname, WithName {
    private static final Logger LOG = LoggerFactory.getLogger(OpenshiftOllama.class);

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
                LOG.warn("Unable to close Ollama port forward", e);
            }
        }
        if (client != null) {
            try {
                client.close();
            } catch (Exception e) {
                LOG.warn("Unable to close Ollama client", e);
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

        // @formatter:off
        OpenshiftClient.get().createDeployment(Map.of(
            "name", name(),
            "image", image(),
            "ports", ports
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
    public String externalHostname() {
        return OpenshiftClient.get().routes().withName(name()).get().getSpec().getHost();
    }

    @Override
    public String name() {
        return "ollama";
    }
}
