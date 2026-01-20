package software.tnb.ldap.resource.openshift;

import software.tnb.common.config.OpenshiftConfiguration;
import software.tnb.common.deployment.ReusableOpenshiftDeployable;
import software.tnb.common.deployment.WithName;
import software.tnb.common.openshift.OpenshiftClient;
import software.tnb.common.utils.IOUtils;
import software.tnb.common.utils.NetworkUtils;
import software.tnb.common.utils.WaitUtils;
import software.tnb.common.utils.waiter.Waiter;
import software.tnb.ldap.service.LDAP;

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
import io.fabric8.kubernetes.api.model.Probe;
import io.fabric8.kubernetes.api.model.ProbeBuilder;
import io.fabric8.kubernetes.api.model.ServiceAccountBuilder;
import io.fabric8.kubernetes.api.model.ServiceBuilder;
import io.fabric8.kubernetes.api.model.TCPSocketActionBuilder;
import io.fabric8.kubernetes.client.PortForward;

@AutoService(LDAP.class)
public class OpenshiftLDAP extends LDAP implements ReusableOpenshiftDeployable, WithName {

    private static final Logger LOG = LoggerFactory.getLogger(OpenshiftLDAP.class);
    private PortForward portForward;
    private String sccName;
    private String serviceAccountName;

    @Override
    public void undeploy() {
        if (!getConfiguration().isRemoteServer()) {
            LOG.info("Undeploying OpenShift LDAP");
            OpenshiftClient.get().securityContextConstraints().withName(sccName).delete();
            OpenshiftClient.get().serviceAccounts().withName(serviceAccountName).delete();
            OpenshiftClient.get().apps().deployments().withName(name()).delete();
            OpenshiftClient.get().services().withLabel(OpenshiftConfiguration.openshiftDeploymentLabel(), name()).delete();
            WaitUtils.waitFor(new Waiter(() -> servicePod() == null, "Waiting until the pod is removed"));
        }
    }

    @Override
    public void openResources() {
        if (getConfiguration().isRemoteServer()) {
            port = PORT;
        } else {
            port = NetworkUtils.getFreePort();
            portForward = OpenshiftClient.get().services().withName(name()).portForward(PORT, port);
        }

        initializeClient(account());
    }

    @Override
    public void closeResources() {
        if (client != null) {
            client.close();
        }

        if (portForward != null && portForward.isAlive()) {
            IOUtils.closeQuietly(portForward);
        }

        NetworkUtils.releasePort(port);
    }

    @Override
    public void create() {
        if (!getConfiguration().isRemoteServer()) {
            sccName = "tnb-ldap-" + OpenshiftClient.get().getNamespace();

            serviceAccountName = name() + "-sa";

            OpenshiftClient.get().serviceAccounts().resource(new ServiceAccountBuilder()
                .withNewMetadata()
                .withName(serviceAccountName)
                .endMetadata()
                .build()
            ).serverSideApply();

            OpenshiftClient.get().addUsersToSecurityContext(
                OpenshiftClient.get().createSecurityContext(sccName, "anyuid", "SYS_CHROOT"),
                OpenshiftClient.get().getServiceAccountRef(serviceAccountName));

            final Probe probe = new ProbeBuilder()
                .withTcpSocket(new TCPSocketActionBuilder().withPort(new IntOrString(PORT)).build())
                .withTimeoutSeconds(15)
                .build();

            final List<ContainerPort> ports = List.of(
                new ContainerPortBuilder().withName(name()).withContainerPort(PORT).build()
            );

            OpenshiftClient.get().createDeployment(Map.of(
                "name", name(),
                "image", image(),
                "env", environmentVariables(),
                "ports", ports,
                "scc", sccName,
                "serviceAccount", serviceAccountName,
                "capabilities", List.of("SYS_CHROOT"),
                "readinessProbe", probe,
                "livenessProbe", probe
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
            .build())
            .serverSideApply();
        // @formatter:on
        }
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
        return "ldap";
    }

    @Override
    public String url() {
        return "ldap://" + (getConfiguration().isRemoteServer()
            ? (host + ":" + port)
            : String.format("%s.%s.svc.cluster.local:%d", name(), OpenshiftClient.get().getNamespace(), PORT));
    }

    @Override
    public boolean isReady() {
        return getConfiguration().isRemoteServer() || ReusableOpenshiftDeployable.super.isReady();
    }
}
