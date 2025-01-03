package software.tnb.ldap.resource.openshift;

import software.tnb.common.config.OpenshiftConfiguration;
import software.tnb.common.deployment.ReusableOpenshiftDeployable;
import software.tnb.common.deployment.WithName;
import software.tnb.common.openshift.OpenshiftClient;
import software.tnb.common.utils.IOUtils;
import software.tnb.common.utils.NetworkUtils;
import software.tnb.common.utils.WaitUtils;
import software.tnb.ldap.service.LDAPLocalStack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.auto.service.AutoService;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPConnectionPool;
import com.unboundid.ldap.sdk.LDAPException;

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

@AutoService(LDAPLocalStack.class)
public class OpenshiftLDAP extends LDAPLocalStack implements ReusableOpenshiftDeployable, WithName {

    private static final Logger LOG = LoggerFactory.getLogger(OpenshiftLDAP.class);
    private PortForward portForward;
    private int localPort;
    private String sccName;
    private String serviceAccountName;

    @Override
    public void undeploy() {
        LOG.info("Undeploying OpenShift LDAP");
        OpenshiftClient.get().securityContextConstraints().withName(sccName).delete();
        OpenshiftClient.get().serviceAccounts().withName(serviceAccountName).delete();
        OpenshiftClient.get().apps().deployments().withName(name()).delete();
        OpenshiftClient.get().services().withLabel(OpenshiftConfiguration.openshiftDeploymentLabel(), name()).delete();
        WaitUtils.waitFor(() -> servicePod() == null, "Waiting until the pod is removed");
    }

    @Override
    public void openResources() {
        localPort = NetworkUtils.getFreePort();
        portForward = OpenshiftClient.get().services().withName(name()).portForward(PORT, localPort);
        final LDAPConnection ldapConnection = new LDAPConnection();
        try {
            ldapConnection.connect("localhost", localPort, 20000);
            ldapConnection.bind(account().getUsername(), account().getPassword());
            client = new LDAPConnectionPool(ldapConnection, 1);
        } catch (LDAPException e) {
            throw new RuntimeException("Error when connecting to LDAP server: " + e.getMessage());
        }
    }

    @Override
    public void closeResources() {
        if (client != null) {
            client.close();
        }

        if (portForward != null && portForward.isAlive()) {
            IOUtils.closeQuietly(portForward);
        }

        NetworkUtils.releasePort(localPort);
    }

    @Override
    public void create() {
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
        String serviceHost = String.format("%s.%s.svc.cluster.local", name(), OpenshiftClient.get().getNamespace());
        return String.format("ldap://%s:%d", serviceHost, PORT);
    }

    @Override
    public LDAPConnectionPool getConnection() {
        return client;
    }
}
