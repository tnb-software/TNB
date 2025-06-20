package software.tnb.db.common.openshift;

import software.tnb.common.config.OpenshiftConfiguration;
import software.tnb.common.deployment.OpenshiftDeployable;
import software.tnb.common.deployment.WithName;
import software.tnb.common.openshift.OpenshiftClient;
import software.tnb.common.utils.IOUtils;
import software.tnb.common.utils.NetworkUtils;
import software.tnb.common.utils.WaitUtils;
import software.tnb.db.common.service.SQL;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import io.fabric8.kubernetes.api.model.ContainerPort;
import io.fabric8.kubernetes.api.model.ContainerPortBuilder;
import io.fabric8.kubernetes.api.model.IntOrString;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.Probe;
import io.fabric8.kubernetes.api.model.ProbeBuilder;
import io.fabric8.kubernetes.api.model.SecurityContext;
import io.fabric8.kubernetes.api.model.SecurityContextBuilder;
import io.fabric8.kubernetes.api.model.ServiceBuilder;
import io.fabric8.kubernetes.client.LocalPortForward;

public class OpenshiftDB implements OpenshiftDeployable, WithName {

    private LocalPortForward portForward;
    private int localPort;
    private final SQL sqlService;
    private final int port;

    public OpenshiftDB(SQL sqlService, int port) {
        this.sqlService = sqlService;
        this.port = port;
    }

    protected String getSccName() {
        return "tnb-openshift-db-" + OpenshiftClient.get().getNamespace();
    }

    @Override
    public void create() {
        OpenshiftClient.get().addGroupsToSecurityContext(
            OpenshiftClient.get().createSecurityContext(getSccName(), "restricted"),
            "system:serviceaccounts:" + OpenshiftClient.get().getNamespace());

        List<ContainerPort> ports = List.of(
            new ContainerPortBuilder().withName(name()).withContainerPort(port).build()
        );

        //@formatter:off
        Probe probe = new ProbeBuilder()
            .withNewTcpSocket()
                .withNewPort(name())
            .endTcpSocket()
            .withInitialDelaySeconds(5)
            .withTimeoutSeconds(5)
            .withFailureThreshold(6)
            .build();

        OpenshiftClient.get().createDeployment(Map.of(
            "name", name(),
            "image", sqlService.image(),
            "env", sqlService.getConfiguration().getEnvironmentVariables(),
            "ports", ports,
            "readinessProbe", probe
        ), builder -> builder
            .editSpec()
                .editTemplate()
                    .editSpec()
                        .editContainer(0)
                            .withNewSecurityContextLike(getSecurityContext()).endSecurityContext()
                        .endContainer()
                    .endSpec()
                .endTemplate()
            .endSpec()
        );

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
                        .withPort(port)
                        .withTargetPort(new IntOrString(port))
                    .endPort()
                .endSpec()
            .build()
        ).serverSideApply();
        //@formatter:on
    }

    private static SecurityContext getSecurityContext() {
        //@formatter:off
        return OpenshiftConfiguration.isMicroshift()
            ? new SecurityContextBuilder().withAllowPrivilegeEscalation(false)
            .withNewCapabilities()
                .addToDrop("ALL")
            .endCapabilities()
            .withRunAsNonRoot(true)
            .withNewSeccompProfile()
                .withType("RuntimeDefault")
            .endSeccompProfile()
            .build()
            : new SecurityContextBuilder().withAllowPrivilegeEscalation(true).build();
        //@formatter:on
    }

    @Override
    public void undeploy() {
        OpenshiftClient.get().securityContextConstraints().withName(getSccName()).delete();
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
        return WithName.super.isDeployed();
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
