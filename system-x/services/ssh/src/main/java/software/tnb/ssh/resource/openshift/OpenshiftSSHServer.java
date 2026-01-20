package software.tnb.ssh.resource.openshift;

import software.tnb.common.config.OpenshiftConfiguration;
import software.tnb.common.deployment.OpenshiftDeployable;
import software.tnb.common.deployment.WithName;
import software.tnb.common.openshift.OpenshiftClient;
import software.tnb.common.utils.WaitUtils;
import software.tnb.ssh.service.SSHServer;

import org.junit.jupiter.api.extension.ExtensionContext;

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
import io.fabric8.kubernetes.api.model.ServiceAccountBuilder;
import io.fabric8.kubernetes.api.model.ServiceBuilder;

@AutoService(SSHServer.class)
public class OpenshiftSSHServer extends SSHServer implements OpenshiftDeployable, WithName {

    private static final Logger LOG = LoggerFactory.getLogger(OpenshiftSSHServer.class);
    private String sccName;
    private String serviceAccountName;

    @Override
    public void undeploy() {
        LOG.info("Undeploying SSH server");
        OpenshiftClient.get().securityContextConstraints().withName(sccName).delete();
        OpenshiftClient.get().serviceAccounts().withName(serviceAccountName).delete();
        OpenshiftClient.get().apps().deployments().withName(name()).delete();
        OpenshiftClient.get().services().withLabel(OpenshiftConfiguration.openshiftDeploymentLabel(), name()).delete();
        WaitUtils.waitFor(() -> servicePod() == null, "Waiting until the pod is removed");
    }

    @Override
    public void openResources() {
    }

    @Override
    public void closeResources() {
    }

    @Override
    public void create() {
        LOG.info("Deploying SSH server");

        sccName = "tnb-ssh-" + OpenshiftClient.get().getNamespace();

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

        List<ContainerPort> ports = List.of(
            new ContainerPortBuilder().withName(name()).withProtocol("TCP").withContainerPort(port()).build()
        );

        OpenshiftClient.get().createDeployment(Map.of(
            "name", name(),
            "image", image(),
            "serviceAccount", serviceAccountName,
            "scc", sccName,
            "ports", ports,
            "capabilities", List.of("SYS_CHROOT")
        ));

        // @formatter:off
        OpenshiftClient.get().services().resource(new ServiceBuilder()
            .editOrNewMetadata()
                .withName(name())
                .addToLabels(OpenshiftConfiguration.openshiftDeploymentLabel(), name())
            .endMetadata()
            .editOrNewSpec()
                .addNewPort()
                    .withName(name())
                    .withProtocol("TCP")
                    .withPort(port())
                    .withTargetPort(new IntOrString(port()))
                .endPort()
                .addToSelector(OpenshiftConfiguration.openshiftDeploymentLabel(), name())
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
    public String name() {
        return "ssh";
    }

    @Override
    public String host() {
        return name();
    }

    @Override
    public int port() {
        return SSHD_LISTENING_PORT;
    }

    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {
        OpenshiftDeployable.super.beforeAll(extensionContext);
    }

    @Override
    public void afterAll(ExtensionContext extensionContext) throws Exception {
        OpenshiftDeployable.super.afterAll(extensionContext);
    }
}
