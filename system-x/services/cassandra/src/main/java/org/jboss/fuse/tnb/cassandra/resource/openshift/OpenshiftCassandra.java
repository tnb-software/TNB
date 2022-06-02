package org.jboss.fuse.tnb.cassandra.resource.openshift;

import org.jboss.fuse.tnb.cassandra.service.Cassandra;
import org.jboss.fuse.tnb.common.config.OpenshiftConfiguration;
import org.jboss.fuse.tnb.common.deployment.OpenshiftDeployable;
import org.jboss.fuse.tnb.common.deployment.WithExternalHostname;
import org.jboss.fuse.tnb.common.deployment.WithInClusterHostname;
import org.jboss.fuse.tnb.common.deployment.WithName;
import org.jboss.fuse.tnb.common.openshift.OpenshiftClient;
import org.jboss.fuse.tnb.common.utils.IOUtils;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.config.DefaultDriverOption;
import com.datastax.oss.driver.api.core.config.DriverConfigLoader;
import com.google.auto.service.AutoService;

import java.net.InetSocketAddress;
import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

import cz.xtf.core.openshift.OpenShiftWaiters;
import cz.xtf.core.openshift.helpers.ResourceFunctions;
import io.fabric8.kubernetes.api.model.EnvVar;
import io.fabric8.kubernetes.api.model.IntOrString;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.ServiceBuilder;
import io.fabric8.kubernetes.api.model.apps.DeploymentBuilder;
import io.fabric8.kubernetes.client.LocalPortForward;

@AutoService(Cassandra.class)
public class OpenshiftCassandra extends Cassandra implements OpenshiftDeployable, WithName, WithInClusterHostname, WithExternalHostname {

    private CqlSession session;
    private LocalPortForward portForward;

    @Override
    public void create() {

        //@formatter:off
        OpenshiftClient.get().apps().deployments().createOrReplace(

            new DeploymentBuilder()
                .withNewMetadata()
                    .withName(name())
                    .addToLabels(OpenshiftConfiguration.openshiftDeploymentLabel(), name())
                .endMetadata()
                .editOrNewSpec()
                    .editOrNewSelector()
                        .addToMatchLabels(OpenshiftConfiguration.openshiftDeploymentLabel(), name())
                    .endSelector()
                    .withReplicas(1)
                    .editOrNewTemplate()
                        .editOrNewMetadata()
                            .addToLabels(OpenshiftConfiguration.openshiftDeploymentLabel(), name())
                        .endMetadata()
                        .withNewSpec()
                            .addNewContainer()
                                .withName(name())
                                .withImage(image())
                                .addAllToEnv(containerEnvironment().entrySet().stream().map(e -> new EnvVar(e.getKey(), e.getValue(), null))
                                    .collect(Collectors.toList()))
                                .addNewPort()
                                    .withContainerPort(port())
                                    .withName(name())
                                .endPort()
                                .withImagePullPolicy("IfNotPresent")
                                .withNewReadinessProbe()
                                    .withNewTcpSocket()
                                        .withNewPort(name())
                                    .endTcpSocket()
                                    .withInitialDelaySeconds(30) // cannot go much lower before seeing issues
                                    .withTimeoutSeconds(5)
                                    .withFailureThreshold(6)
                                .endReadinessProbe()
                            .endContainer()
                        .endSpec()
                    .endTemplate()
                .endSpec()
                .build()
        );
        //@formatter:on

        //@formatter:off
        OpenshiftClient.get().services().createOrReplace(
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
        );
        //@formatter:on

    }

    @Override
    protected CqlSession session() {
        return session;
    }

    @Override
    public int port() {
        return CASSANDRA_PORT;
    }

    @Override
    public String host() {
        return name();
    }

    @Override
    public void undeploy() {
        OpenshiftClient.get().services().withName(name()).delete();
        OpenshiftClient.get().apps().deployments().withName(name()).delete();
        OpenShiftWaiters.get(OpenshiftClient.get(), () -> false)
            .areNoPodsPresent(OpenshiftConfiguration.openshiftDeploymentLabel(), name()).timeout(120_000).waitFor();
    }

    @Override
    public void openResources() {
        portForward = OpenshiftClient.get().services().withName(name()).portForward(port(), port());

        // default timeout pretty much always failed when creating table, increase to 30s
        DriverConfigLoader loader =
            DriverConfigLoader.programmaticBuilder()
                .withDuration(DefaultDriverOption.REQUEST_TIMEOUT, Duration.ofSeconds(30))
                .build();

        session = CqlSession.builder()
            .withConfigLoader(loader)
            .addContactPoint(new InetSocketAddress(externalHostname(), port()))
            .withAuthCredentials(account().username(), account().password())
            .withLocalDatacenter(account().datacenter())
            .build();
    }

    @Override
    public void closeResources() {
        if (session != null) {
            session.close();
            session = null;
        }
        IOUtils.closeQuietly(portForward);
    }

    @Override
    public boolean isReady() {
        List<Pod> pods = OpenshiftClient.get().getLabeledPods(OpenshiftConfiguration.openshiftDeploymentLabel(), name());
        if (ResourceFunctions.areExactlyNPodsReady(1).apply(pods)) {
            return OpenshiftClient.get().getLogs(pods.get(0)).contains("Startup complete");
        }
        return false;
    }

    @Override
    public boolean isDeployed() {
        return OpenshiftClient.get().getLabeledPods(OpenshiftConfiguration.openshiftDeploymentLabel(), name()).size() > 0;
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
