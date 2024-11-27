package software.tnb.redis.resource.openshift;

import software.tnb.common.config.OpenshiftConfiguration;
import software.tnb.common.deployment.ReusableOpenshiftDeployable;
import software.tnb.common.deployment.WithName;
import software.tnb.common.openshift.OpenshiftClient;
import software.tnb.common.utils.IOUtils;
import software.tnb.common.utils.NetworkUtils;
import software.tnb.common.utils.WaitUtils;
import software.tnb.redis.service.Redis;

import com.google.auto.service.AutoService;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import io.fabric8.kubernetes.api.model.ContainerPort;
import io.fabric8.kubernetes.api.model.ContainerPortBuilder;
import io.fabric8.kubernetes.api.model.IntOrString;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.ServiceBuilder;
import io.fabric8.kubernetes.client.PortForward;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;

@AutoService(Redis.class)
public class OpenshiftRedis extends Redis implements ReusableOpenshiftDeployable, WithName {

    private PortForward portForward;
    private int localPort;

    @Override
    public void undeploy() {
        OpenshiftClient.get().apps().deployments().withName(name()).delete();
        OpenshiftClient.get().services().withLabel(OpenshiftConfiguration.openshiftDeploymentLabel(), name()).delete();
        WaitUtils.waitFor(() -> servicePod() == null, "Waiting until the pod is removed");
    }

    @Override
    public void openResources() {
        localPort = NetworkUtils.getFreePort();
        portForward = OpenshiftClient.get().services().withName(name()).portForward(PORT, localPort);

        client = RedisClient.create(RedisURI.builder()
            .withHost("localhost")
            .withPort(localPort)
            .build());
    }

    @Override
    public void closeResources() {
        if (portForward != null && portForward.isAlive()) {
            IOUtils.closeQuietly(portForward);
        }
        NetworkUtils.releasePort(localPort);
    }

    @Override
    public void create() {
        List<ContainerPort> ports = List.of(
            new ContainerPortBuilder().withContainerPort(PORT).withName(name()).build()
        );

        OpenshiftClient.get().createDeployment(Map.of(
            "name", name(),
            "image", image(),
            "ports", ports
        ));

        // @formatter:off
        OpenshiftClient.get().services().resource(new ServiceBuilder()
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
    public void cleanup() {

    }

    @Override
    public String name() {
        return "redis";
    }

    @Override
    public String host() {
        return name();
    }

    @Override
    public int port() {
        return PORT;
    }
}
