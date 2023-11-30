package software.tnb.db.common.openshift;

import software.tnb.common.config.OpenshiftConfiguration;
import software.tnb.common.deployment.OpenshiftDeployable;
import software.tnb.common.deployment.WithName;
import software.tnb.common.openshift.OpenshiftClient;
import software.tnb.common.utils.IOUtils;
import software.tnb.common.utils.MapUtils;
import software.tnb.common.utils.NetworkUtils;
import software.tnb.common.utils.WaitUtils;
import software.tnb.db.common.service.SQL;

import java.util.function.Predicate;

import io.fabric8.kubernetes.api.model.IntOrString;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.SecurityContextBuilder;
import io.fabric8.kubernetes.api.model.ServiceBuilder;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.DeploymentBuilder;
import io.fabric8.kubernetes.client.LocalPortForward;

public class OpenshiftDB implements OpenshiftDeployable, WithName {

    private LocalPortForward portForward;
    private int localPort;
    private final SQL sqlService;
    private final int port;
    private final String sccName;

    public OpenshiftDB(SQL sqlService, int port) {
        this.sqlService = sqlService;
        this.port = port;
        sccName = "tnb-openshift-db-" + OpenshiftClient.get().getNamespace();
    }

    @Override
    public void create() {
        OpenshiftClient.get().addGroupsToSecurityContext(
            OpenshiftClient.get().createSecurityContext(sccName, "restricted"),
            "system:serviceaccounts:" + OpenshiftClient.get().getNamespace());

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
                                .withImage(sqlService.image())
                                .withSecurityContext(new SecurityContextBuilder().withAllowPrivilegeEscalation(true).build())
                                .addNewPort()
                                    .withContainerPort(port)
                                    .withName(name())
                                .endPort()
                                .withImagePullPolicy("IfNotPresent")
                                .withEnv(MapUtils.toEnvVars(sqlService.containerEnvironment()))
                                .withNewReadinessProbe()
                                    .withNewTcpSocket()
                                        .withNewPort(name())
                                    .endTcpSocket()
                                    .withInitialDelaySeconds(5)
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
                        .withPort(port)
                        .withTargetPort(new IntOrString(port))
                    .endPort()
                .endSpec()
            .build()
        );
        //@formatter:on
    }

    @Override
    public void undeploy() {
        OpenshiftClient.get().securityContextConstraints().withName(sccName).delete();
        OpenshiftClient.get().services().withName(name()).delete();
        OpenshiftClient.get().apps().deployments().withName(name()).delete();
        WaitUtils.waitFor(() -> servicePod() == null, "Waiting until the pod is removed");
    }

    @Override
    public void openResources() {
        localPort = NetworkUtils.getFreePort();
        portForward = OpenshiftClient.get().services().withName(name()).portForward(port, localPort);
    }

    @Override
    public void closeResources() {
        IOUtils.closeQuietly(portForward);
        NetworkUtils.releasePort(localPort);
    }

    @Override
    public boolean isDeployed() {
        final Deployment deployment = OpenshiftClient.get().apps().deployments().withName(name()).get();
        return deployment != null && !deployment.isMarkedForDeletion();
    }

    @Override
    public Predicate<Pod> podSelector() {
        return WithName.super.podSelector();
    }

    @Override
    public String name() {
        return sqlService.name().toLowerCase();
    }

    public int localPort() {
        return localPort;
    }
}
