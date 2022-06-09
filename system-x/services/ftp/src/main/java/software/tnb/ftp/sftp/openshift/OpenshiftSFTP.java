package software.tnb.ftp.sftp.openshift;

import software.tnb.common.config.OpenshiftConfiguration;
import software.tnb.common.deployment.OpenshiftDeployable;
import software.tnb.common.deployment.WithExternalHostname;
import software.tnb.common.deployment.WithInClusterHostname;
import software.tnb.common.deployment.WithName;
import software.tnb.common.openshift.OpenshiftClient;
import software.tnb.common.utils.IOUtils;
import software.tnb.common.utils.WaitUtils;
import software.tnb.ftp.sftp.service.SFTP;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.auto.service.AutoService;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import cz.xtf.core.openshift.OpenShiftWaiters;
import cz.xtf.core.openshift.helpers.ResourceFunctions;
import io.fabric8.kubernetes.api.model.ContainerPort;
import io.fabric8.kubernetes.api.model.ContainerPortBuilder;
import io.fabric8.kubernetes.api.model.EnvVar;
import io.fabric8.kubernetes.api.model.IntOrString;
import io.fabric8.kubernetes.api.model.ServiceAccountBuilder;
import io.fabric8.kubernetes.api.model.ServiceBuilder;
import io.fabric8.kubernetes.api.model.ServicePortBuilder;
import io.fabric8.kubernetes.api.model.ServiceSpecBuilder;
import io.fabric8.kubernetes.api.model.apps.DeploymentBuilder;
import io.fabric8.kubernetes.client.PortForward;
import io.fabric8.openshift.api.model.SecurityContextConstraints;
import io.fabric8.openshift.api.model.SecurityContextConstraintsBuilder;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.sftp.SFTPClient;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;

@AutoService(SFTP.class)
public class OpenshiftSFTP extends SFTP implements OpenshiftDeployable, WithName, WithInClusterHostname, WithExternalHostname {
    private static final Logger LOG = LoggerFactory.getLogger(OpenshiftSFTP.class);

    private static final String SCC_NAME = "tnb-sftp";
    public static final int LOCAL_PORT = 3322;

    private SFTPClient client;
    private PortForward portForward;
    private String serviceAccountName;

    @Override
    public void create() {

        List<ContainerPort> ports = new LinkedList<>();
        ports.add(new ContainerPortBuilder()
            .withName("sftp")
            .withContainerPort(port())
            .withProtocol("TCP").build());

        serviceAccountName = name() + "-sa";

        OpenshiftClient.get().serviceAccounts()
            .createOrReplace(new ServiceAccountBuilder()
                .withNewMetadata()
                .withName(serviceAccountName)
                .endMetadata()
                .build()
            );

        SecurityContextConstraints scc = OpenshiftClient.get().securityContextConstraints().withName(SCC_NAME).get();
        if (scc == null) {
            // if our scc does not exist, copy it from the anyuid and add SYS_CHROOT
            SecurityContextConstraints anyuid = OpenshiftClient.get().securityContextConstraints().withName("anyuid").get();
            scc = OpenshiftClient.get().securityContextConstraints().create(
                new SecurityContextConstraintsBuilder(anyuid)
                    .withNewMetadata() // new metadata to override the existing annotations
                    .withName(SCC_NAME)
                    .endMetadata()
                    .addToDefaultAddCapabilities("SYS_CHROOT")
                    .build());
        }

        final String user = "system:serviceaccount:" + OpenshiftConfiguration.openshiftNamespace() + ":" + serviceAccountName;
        if (!scc.getUsers().contains(user)) {
            scc.getUsers().add(user);
            OpenshiftClient.get().securityContextConstraints().withName(SCC_NAME).patch(scc);
        }

        OpenshiftClient.get().apps().deployments().createOrReplace(
            new DeploymentBuilder()
                .editOrNewMetadata()
                .withName(name())
                .addToLabels(OpenshiftConfiguration.openshiftDeploymentLabel(), name())
                .addToAnnotations("openshift.io/scc", SCC_NAME)
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
                .withName(name()).withImage(sftpImage()).addAllToPorts(ports)
                .withImagePullPolicy("IfNotPresent")
                .withEnv(new EnvVar("SFTP_USERS", containerEnvironment().get("SFTP_USERS"), null))
                .editOrNewSecurityContext()
                .editOrNewCapabilities()
                .addNewAdd("SYS_CHROOT")
                .endCapabilities()
                .endSecurityContext()
                .endContainer()
                .endSpec()
                .endTemplate()
                .endSpec()
                .build()
        );

        ServiceSpecBuilder serviceSpecBuilder = new ServiceSpecBuilder().addToSelector(OpenshiftConfiguration.openshiftDeploymentLabel(), name());

        serviceSpecBuilder.addToPorts(new ServicePortBuilder()
            .withName("sftp")
            .withPort(port())
            .withTargetPort(new IntOrString(port()))
            .build());

        OpenshiftClient.get().services().createOrReplace(
            new ServiceBuilder()
                .editOrNewMetadata()
                .withName(name())
                .addToLabels(OpenshiftConfiguration.openshiftDeploymentLabel(), name())
                .endMetadata()
                .editOrNewSpecLike(serviceSpecBuilder.build())
                .endSpec()
                .build()
        );
    }

    @Override
    public void undeploy() {
        LOG.info("Undeploying OpenShift sftp");
        SecurityContextConstraints scc = OpenshiftClient.get().securityContextConstraints().withName(SCC_NAME).edit();
        scc.getUsers().remove("system:serviceaccount:" + OpenshiftConfiguration.openshiftNamespace() + ":" + serviceAccountName);
        OpenshiftClient.get().securityContextConstraints().withName(SCC_NAME).patch(scc);
        OpenshiftClient.get().services().withName(name()).delete();
        OpenshiftClient.get().apps().deployments().withName(name()).delete();
        OpenShiftWaiters.get(OpenshiftClient.get(), () -> false).areNoPodsPresent(OpenshiftConfiguration.openshiftDeploymentLabel(), name())
            .timeout(120_000).waitFor();
    }

    @Override
    public void openResources() {
        portForward = OpenshiftClient.get().services().withName(name()).portForward(port(), LOCAL_PORT);
        WaitUtils.sleep(1000);
        makeClient();
    }

    @Override
    public void closeResources() {
        IOUtils.closeQuietly(client);
        IOUtils.closeQuietly(portForward);
    }

    @Override
    public boolean isReady() {
        return ResourceFunctions.areExactlyNPodsReady(1)
            .apply(OpenshiftClient.get().getLabeledPods(OpenshiftConfiguration.openshiftDeploymentLabel(), name()))
            && OpenshiftClient.get().getLogs(OpenshiftClient.get().getAnyPod(OpenshiftConfiguration.openshiftDeploymentLabel(), name()))
            .contains("Server listening on");
    }

    @Override
    public boolean isDeployed() {
        return OpenshiftClient.get().getLabeledPods(OpenshiftConfiguration.openshiftDeploymentLabel(), name()).size() != 0;
    }

    @Override
    public String name() {
        return "sftp";
    }

    @Override
    public SFTPClient client() {
        return client;
    }

    private void makeClient() {
        try {
            LOG.debug("Creating new SFTPClient instance");
            SSHClient sshClient = new SSHClient();
            sshClient.addHostKeyVerifier(new PromiscuousVerifier());
            sshClient.connect(externalHostname(), LOCAL_PORT);
            sshClient.authPassword(account().username(), account().password());
            client = sshClient.newSFTPClient();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String host() {
        return inClusterHostname();
    }

    @Override
    public String externalHostname() {
        return "localhost";
    }
}
