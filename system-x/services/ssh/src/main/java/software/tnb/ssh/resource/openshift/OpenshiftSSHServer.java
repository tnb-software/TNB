package software.tnb.ssh.resource.openshift;

import software.tnb.common.config.OpenshiftConfiguration;
import software.tnb.common.deployment.OpenshiftDeployable;
import software.tnb.common.deployment.WithExternalHostname;
import software.tnb.common.deployment.WithInClusterHostname;
import software.tnb.common.deployment.WithName;
import software.tnb.common.openshift.OpenshiftClient;
import software.tnb.common.utils.IOUtils;
import software.tnb.common.utils.NetworkUtils;
import software.tnb.common.utils.WaitUtils;
import software.tnb.common.utils.waiter.Waiter;
import software.tnb.ssh.service.SSHServer;

import org.junit.jupiter.api.extension.ExtensionContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.auto.service.AutoService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import io.fabric8.kubernetes.api.model.ConfigMapBuilder;
import io.fabric8.kubernetes.api.model.ConfigMapVolumeSourceBuilder;
import io.fabric8.kubernetes.api.model.ContainerPort;
import io.fabric8.kubernetes.api.model.ContainerPortBuilder;
import io.fabric8.kubernetes.api.model.IntOrString;
import io.fabric8.kubernetes.api.model.KeyToPathBuilder;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.ServiceAccountBuilder;
import io.fabric8.kubernetes.api.model.ServiceBuilder;
import io.fabric8.kubernetes.api.model.Volume;
import io.fabric8.kubernetes.api.model.VolumeBuilder;
import io.fabric8.kubernetes.api.model.VolumeMount;
import io.fabric8.kubernetes.api.model.VolumeMountBuilder;
import io.fabric8.kubernetes.client.PortForward;

@AutoService(SSHServer.class)
public class OpenshiftSSHServer extends SSHServer implements OpenshiftDeployable, WithName, WithInClusterHostname, WithExternalHostname {

    private static final Logger LOG = LoggerFactory.getLogger(OpenshiftSSHServer.class);
    private String sccName;
    private String serviceAccountName;
    private PortForward portForward;
    private int localPort;

    @Override
    public void undeploy() {
        LOG.info("Undeploying SSH server");
        OpenshiftClient.get().configMaps().withLabel(OpenshiftConfiguration.openshiftDeploymentLabel(), name()).delete();
        OpenshiftClient.get().securityContextConstraints().withName(sccName).delete();
        OpenshiftClient.get().serviceAccounts().withName(serviceAccountName).delete();
        OpenshiftClient.get().apps().deployments().withName(name()).delete();
        OpenshiftClient.get().services().withLabel(OpenshiftConfiguration.openshiftDeploymentLabel(), name()).delete();
        WaitUtils.waitFor(new Waiter(() -> servicePod() == null, "Waiting until the pod is removed"));
    }

    @Override
    protected String clientHostname() {
        return externalHostname();
    }

    @Override
    protected int clientPort() {
        return localPort;
    }

    public void openResources() {
        localPort = NetworkUtils.getFreePort();
        LOG.debug("Creating port-forward to {} for port {}", name(), SSHD_LISTENING_PORT);
        portForward = OpenshiftClient.get().services().withName(name()).portForward(SSHD_LISTENING_PORT, localPort);
        super.openResources();
    }

    public void closeResources() {
        super.closeResources();
        if (portForward != null && portForward.isAlive()) {
            LOG.debug("Closing port-forward");
            IOUtils.closeQuietly(portForward);
        }
        NetworkUtils.releasePort(localPort);
    }

    private void createPublicKeyConfigMap() {
        Path keyPath = getConfiguration().publicKeyPath();
        if (keyPath == null) {
            return;
        }

        try {
            String keyContent = Files.readString(keyPath);
            String configMapName = name() + "-pubkey";

            LOG.debug("Creating ConfigMap {} for SSH public key", configMapName);
            OpenshiftClient.get().configMaps().resource(new ConfigMapBuilder()
                .withNewMetadata()
                    .withName(configMapName)
                    .addToLabels(OpenshiftConfiguration.openshiftDeploymentLabel(), name())
                .endMetadata()
                .withData(Map.of("authorized_key", keyContent))
                .build()
            ).serverSideApply();
        } catch (IOException e) {
            throw new RuntimeException("Failed to read SSH public key: " + keyPath, e);
        }
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

        // Create ConfigMap for public key if configured
        createPublicKeyConfigMap();

        // Setup volumes and volume mounts
        List<Volume> volumes = new ArrayList<>();
        List<VolumeMount> volumeMounts = new ArrayList<>();

        if (getConfiguration().publicKeyPath() != null) {
            String configMapName = name() + "-pubkey";
            volumes.add(new VolumeBuilder()
                .withName("ssh-pubkey")
                .withConfigMap(new ConfigMapVolumeSourceBuilder()
                    .withName(configMapName)
                    .withItems(new KeyToPathBuilder()
                        .withKey("authorized_key")
                        .withPath("tnb_authorized_key")
                        .build())
                    .build())
                .build());

            volumeMounts.add(new VolumeMountBuilder()
                .withName("ssh-pubkey")
                .withMountPath("/etc/ssh/tnb_authorized_key")
                .withSubPath("tnb_authorized_key")
                .build());
        }

        OpenshiftClient.get().createDeployment(Map.of(
            "name", name(),
            "image", image(),
            "serviceAccount", serviceAccountName,
            "scc", sccName,
            "ports", ports,
            "capabilities", List.of("SYS_CHROOT"),
            "env", containerEnvironment(),
            "volumes", volumes,
            "volumeMounts", volumeMounts
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
        return inClusterHostname();
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

    @Override
    public String externalHostname() {
        return "localhost";
    }
}
