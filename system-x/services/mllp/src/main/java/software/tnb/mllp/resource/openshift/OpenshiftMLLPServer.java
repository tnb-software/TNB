package software.tnb.mllp.resource.openshift;

import software.tnb.common.config.OpenshiftConfiguration;
import software.tnb.common.deployment.OpenshiftDeployable;
import software.tnb.common.deployment.WithInClusterHostname;
import software.tnb.common.deployment.WithName;
import software.tnb.common.openshift.OpenshiftClient;
import software.tnb.common.utils.WaitUtils;
import software.tnb.common.utils.waiter.Waiter;
import software.tnb.mllp.service.MLLPServer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.auto.service.AutoService;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import io.fabric8.kubernetes.api.model.ContainerPort;
import io.fabric8.kubernetes.api.model.ContainerPortBuilder;
import io.fabric8.kubernetes.api.model.IntOrString;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.ServiceBuilder;
import io.fabric8.kubernetes.api.model.ServicePortBuilder;
import io.fabric8.kubernetes.client.dsl.PodResource;

@AutoService(MLLPServer.class)
public class OpenshiftMLLPServer extends MLLPServer implements OpenshiftDeployable, WithName, WithInClusterHostname {
    private static final Logger LOG = LoggerFactory.getLogger(OpenshiftMLLPServer.class);

    @Override
    public void undeploy() {
        LOG.info("Undeploying MLLP server");
        OpenshiftClient.get().services().withName(name()).delete();
        OpenshiftClient.get().apps().deployments().withName(name()).delete();
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
        List<ContainerPort> ports = List.of(
            new ContainerPortBuilder().withName("mllp-port").withContainerPort(port()).withProtocol("TCP").build()
        );

        LOG.info("Creating MLLP server deployment");
        OpenshiftClient.get().createDeployment(Map.of(
            "name", name(),
            "image", image(),
            "ports", ports
        ));

        LOG.info("Creating MLLP server service");
        // @formatter:off
        OpenshiftClient.get().services().resource(
            new ServiceBuilder()
                .editOrNewMetadata()
                    .withName(name())
                    .addToLabels(OpenshiftConfiguration.openshiftDeploymentLabel(), name())
                .endMetadata()
                .editOrNewSpec()
                    .addToSelector(OpenshiftConfiguration.openshiftDeploymentLabel(), name())
                    .addToPorts(new ServicePortBuilder()
                        .withName("mllp-port")
                        .withPort(port())
                        .withTargetPort(new IntOrString(port()))
                        .build()
                    )
                .endSpec()
                .build()
        ).serverSideApply();
        // @formatter:on
    }

    @Override
    public boolean isReady() {
        final PodResource pod = servicePod();
        return pod != null && pod.isReady() && OpenshiftClient.get().getLogs(pod.get()).contains("Accepting connections on port");
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
        return "mllp-test-server";
    }

    @Override
    public String host() {
        return inClusterHostname();
    }

    @Override
    public int port() {
        return MLLPServer.LISTENING_PORT;
    }

    @Override
    public String getLogs() {
        return OpenshiftDeployable.super.getLogs();
    }
}
