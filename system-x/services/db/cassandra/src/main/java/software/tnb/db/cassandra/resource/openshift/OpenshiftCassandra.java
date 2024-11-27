package software.tnb.db.cassandra.resource.openshift;

import software.tnb.common.config.OpenshiftConfiguration;
import software.tnb.common.deployment.OpenshiftDeployable;
import software.tnb.common.deployment.WithExternalHostname;
import software.tnb.common.deployment.WithInClusterHostname;
import software.tnb.common.deployment.WithName;
import software.tnb.common.openshift.OpenshiftClient;
import software.tnb.common.utils.IOUtils;
import software.tnb.common.utils.NetworkUtils;
import software.tnb.common.utils.WaitUtils;
import software.tnb.db.cassandra.service.Cassandra;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.config.DefaultDriverOption;
import com.datastax.oss.driver.api.core.config.DriverConfigLoader;
import com.google.auto.service.AutoService;

import java.net.InetSocketAddress;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import io.fabric8.kubernetes.api.model.ContainerPort;
import io.fabric8.kubernetes.api.model.ContainerPortBuilder;
import io.fabric8.kubernetes.api.model.IntOrString;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.Probe;
import io.fabric8.kubernetes.api.model.ProbeBuilder;
import io.fabric8.kubernetes.api.model.ServiceBuilder;
import io.fabric8.kubernetes.client.LocalPortForward;
import io.fabric8.kubernetes.client.dsl.PodResource;

@AutoService(Cassandra.class)
public class OpenshiftCassandra extends Cassandra implements OpenshiftDeployable, WithName, WithInClusterHostname, WithExternalHostname {
    private LocalPortForward portForward;
    private int localPort;

    @Override
    public void create() {
        List<ContainerPort> ports = List.of(
            new ContainerPortBuilder().withName(name()).withContainerPort(port()).build()
        );

        //@formatter:off
        Probe probe = new ProbeBuilder()
            .withNewTcpSocket()
                .withNewPort(name())
            .endTcpSocket()
            .withInitialDelaySeconds(30) // cannot go much lower before seeing issues
            .withTimeoutSeconds(5)
            .withFailureThreshold(6)
            .build();

        OpenshiftClient.get().createDeployment(Map.of(
            "name", name(),
            "image", image(),
            "env", containerEnvironment(),
            "ports", ports,
            "readinessProbe", probe
        ));

        OpenshiftClient.get().services().resource(
            new ServiceBuilder()
                .withNewMetadata()
                    .withName(name())
                    .addToLabels(OpenshiftConfiguration.openshiftDeploymentLabel(), name())
                .endMetadata()
                .withNewSpec()
                    .addToSelector(OpenshiftConfiguration.openshiftDeploymentLabel(), name())
                    .addNewPort()
                        .withName(name())
                        .withPort(port())
                        .withTargetPort(new IntOrString(port()))
                    .endPort()
                .endSpec()
                .build()
        ).serverSideApply();
        //@formatter:on
    }

    @Override
    public String host() {
        return name();
    }

    @Override
    public void undeploy() {
        OpenshiftClient.get().services().withName(name()).delete();
        OpenshiftClient.get().apps().deployments().withName(name()).delete();
        WaitUtils.waitFor(() -> servicePod() == null, "Waiting until the pod is removed");
    }

    @Override
    public void openResources() {
        localPort = NetworkUtils.getFreePort();
        portForward = OpenshiftClient.get().services().withName(name()).portForward(port(), localPort);

        // default timeout pretty much always failed when creating table, increase to 30s
        DriverConfigLoader loader =
            DriverConfigLoader.programmaticBuilder()
                .withDuration(DefaultDriverOption.REQUEST_TIMEOUT, Duration.ofSeconds(30))
                .build();

        client = CqlSession.builder()
            .withConfigLoader(loader)
            .addContactPoint(new InetSocketAddress(externalHostname(), localPort))
            .withAuthCredentials(account().username(), account().password())
            .withLocalDatacenter(account().datacenter())
            .build();
    }

    @Override
    public void closeResources() {
        validation = null;
        if (client != null) {
            client.close();
            client = null;
        }
        IOUtils.closeQuietly(portForward);
        NetworkUtils.releasePort(localPort);
    }

    @Override
    public boolean isReady() {
        final PodResource pod = servicePod();
        if (pod != null && pod.isReady()) {
            return OpenshiftClient.get().getLogs(pod.get()).contains("Startup complete");
        }
        return false;
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
        return "cassandra";
    }

    @Override
    public String externalHostname() {
        return "localhost";
    }
}
