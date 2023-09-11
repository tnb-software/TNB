package software.tnb.ldap.resource.openshift;

import software.tnb.common.config.OpenshiftConfiguration;
import software.tnb.common.deployment.ReusableOpenshiftDeployable;
import software.tnb.common.deployment.WithName;
import software.tnb.common.openshift.OpenshiftClient;
import software.tnb.common.utils.IOUtils;
import software.tnb.common.utils.MapUtils;
import software.tnb.common.utils.NetworkUtils;
import software.tnb.common.utils.WaitUtils;
import software.tnb.ldap.service.LDAPLocalStack;

import com.google.auto.service.AutoService;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPConnectionPool;
import com.unboundid.ldap.sdk.LDAPException;

import java.util.function.Predicate;

import io.fabric8.kubernetes.api.model.IntOrString;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.Probe;
import io.fabric8.kubernetes.api.model.ProbeBuilder;
import io.fabric8.kubernetes.api.model.ServiceAccountBuilder;
import io.fabric8.kubernetes.api.model.ServiceBuilder;
import io.fabric8.kubernetes.api.model.TCPSocketActionBuilder;
import io.fabric8.kubernetes.client.PortForward;
import io.fabric8.openshift.api.model.DeploymentConfigBuilder;

@AutoService(LDAPLocalStack.class)
public class OpenshiftLDAP extends LDAPLocalStack implements ReusableOpenshiftDeployable, WithName {

    private PortForward portForward;
    private int localPort;
    private String sccName;
    private String serviceAccountName;

    @Override
    public void undeploy() {
        OpenshiftClient.get().deploymentConfigs().withName(name()).delete();
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
            ((LDAPConnectionPool) client).close();
        }

        if (portForward != null && portForward.isAlive()) {
            IOUtils.closeQuietly(portForward);
        }
    }

    @Override
    public void create() {

        sccName = "tnb-ldap-" + OpenshiftClient.get().getNamespace();

        serviceAccountName = name() + "-sa";

        OpenshiftClient.get().serviceAccounts()
            .createOrReplace(new ServiceAccountBuilder()
                .withNewMetadata()
                .withName(serviceAccountName)
                .endMetadata()
                .build()
            );

        OpenshiftClient.get().addUsersToSecurityContext(
            OpenshiftClient.get().createSecurityContext(sccName, "anyuid", "SYS_CHROOT"),
            OpenshiftClient.get().getServiceAccountRef(serviceAccountName));

        final Probe probe = new ProbeBuilder()
            .withTcpSocket(new TCPSocketActionBuilder().withPort(new IntOrString(PORT)).build())
            .withTimeoutSeconds(15)
            .build();

        OpenshiftClient.get().deploymentConfigs().createOrReplace(new DeploymentConfigBuilder()
            .withNewMetadata()
                .withName(name())
                .addToLabels(OpenshiftConfiguration.openshiftDeploymentLabel(), name())
                .addToAnnotations("openshift.io/scc", sccName)
            .endMetadata()
                .editOrNewSpec()
                    .addToSelector(OpenshiftConfiguration.openshiftDeploymentLabel(), name())
                    .withReplicas(1)
                    .editOrNewTemplate()
                        .editOrNewMetadata()
                            .addToLabels(OpenshiftConfiguration.openshiftDeploymentLabel(), name())
                        .endMetadata()
                        .editOrNewSpec()
                            .withServiceAccount(serviceAccountName)
                            .addNewContainer()
                                .withName(name())
                                .withImage(defaultImage())
                                .addNewPort()
                                    .withContainerPort(PORT)
                                    .withName(name())
                                .endPort()
                                .editOrNewSecurityContext()
                                    .editOrNewCapabilities()
                                        .addNewAdd("SYS_CHROOT")
                                    .endCapabilities()
                                .endSecurityContext()
                                .withEnv(MapUtils.toEnvVars(environmentVariables()))
                                .withReadinessProbe(probe)
                                .withLivenessProbe(probe)
                            .endContainer()
                        .endSpec()
                    .endTemplate()
                    .addNewTrigger()
                        .withType("ConfigChange")
                    .endTrigger()
                .endSpec()
            .build());

        OpenshiftClient.get().services().createOrReplace(new ServiceBuilder()
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
            .build());
    }

    @Override
    public boolean isDeployed() {
        return OpenshiftClient.get().apps().deployments().withLabel(OpenshiftConfiguration.openshiftDeploymentLabel(), name()).list()
            .getItems().size() > 0;
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
        return String.format("ldap://%s:%d", name(), PORT);
    }
}
