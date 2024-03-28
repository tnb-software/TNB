package software.tnb.ssh.resource.openshift;

import software.tnb.common.config.OpenshiftConfiguration;
import software.tnb.common.deployment.OpenshiftDeployable;
import software.tnb.common.deployment.WithName;
import software.tnb.common.openshift.OpenshiftClient;
import software.tnb.common.utils.WaitUtils;
import software.tnb.ssh.service.SshServer;

import org.junit.jupiter.api.extension.ExtensionContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.auto.service.AutoService;

import java.util.function.Predicate;

import io.fabric8.kubernetes.api.model.IntOrString;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.ServiceAccountBuilder;
import io.fabric8.kubernetes.api.model.ServiceBuilder;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.DeploymentBuilder;

@AutoService(SshServer.class)
public class OpenshiftSsh extends SshServer implements OpenshiftDeployable, WithName {

    private static final Logger LOG = LoggerFactory.getLogger(OpenshiftSsh.class);
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

        sccName = "tnb-ssh-" + OpenshiftClient.get().getNamespace();

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

        LOG.info("Deploying SSH server");

        OpenshiftClient.get().apps().deployments().createOrReplace(
            new DeploymentBuilder()
            .withNewMetadata()
                .withName(name())
                .addToLabels(OpenshiftConfiguration.openshiftDeploymentLabel(), name())
                .addToAnnotations("openshift.io/scc", sccName)
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
                        .editOrNewSpec()
                            .withServiceAccount(serviceAccountName)
                            .addNewContainer()
                                .withName(name())
                                .withImage(defaultImage())
                                .addNewPort()
                                    .withContainerPort(port())
                                    .withProtocol("TCP")
                                .endPort()
                                .editOrNewSecurityContext()
                                    .editOrNewCapabilities()
                                        .addToAdd("SYS_CHROOT")
                                    .endCapabilities()
                                .endSecurityContext()
                            .endContainer()
                        .endSpec()
                    .endTemplate()

                .endSpec()
            .build());

        OpenshiftClient.get().services().createOrReplace(new ServiceBuilder()
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
            .build());

        WaitUtils.waitFor(() -> {
            return servicePod() != null && this.isDeployed();
        }, "Waiting for pod ready");

    }

    @Override
    public boolean isDeployed() {
        Deployment deployment = OpenshiftClient.get().apps().deployments().withName(name()).get();
        return deployment != null && !deployment.isMarkedForDeletion();
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
    public String getLog() {
        return OpenshiftClient.get().getLogs(servicePod().get());
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
