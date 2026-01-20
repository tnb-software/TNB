package software.tnb.ftp.sftp.resource.openshift;

import software.tnb.common.config.OpenshiftConfiguration;
import software.tnb.common.deployment.OpenshiftDeployable;
import software.tnb.common.deployment.WithExternalHostname;
import software.tnb.common.deployment.WithInClusterHostname;
import software.tnb.common.deployment.WithName;
import software.tnb.common.openshift.OpenshiftClient;
import software.tnb.common.utils.IOUtils;
import software.tnb.common.utils.NetworkUtils;
import software.tnb.common.utils.WaitUtils;
import software.tnb.ftp.sftp.service.SFTP;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.auto.service.AutoService;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Predicate;

import io.fabric8.kubernetes.api.model.ContainerPort;
import io.fabric8.kubernetes.api.model.ContainerPortBuilder;
import io.fabric8.kubernetes.api.model.IntOrString;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.ServiceAccountBuilder;
import io.fabric8.kubernetes.api.model.ServiceBuilder;
import io.fabric8.kubernetes.api.model.ServicePortBuilder;
import io.fabric8.kubernetes.api.model.ServiceSpecBuilder;
import io.fabric8.kubernetes.api.model.Volume;
import io.fabric8.kubernetes.api.model.VolumeBuilder;
import io.fabric8.kubernetes.api.model.VolumeMount;
import io.fabric8.kubernetes.api.model.VolumeMountBuilder;
import io.fabric8.kubernetes.client.PortForward;
import io.fabric8.kubernetes.client.dsl.PodResource;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.sftp.SFTPClient;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;

@AutoService(SFTP.class)
public class OpenshiftSFTP extends SFTP implements OpenshiftDeployable, WithName, WithInClusterHostname, WithExternalHostname {
    private static final Logger LOG = LoggerFactory.getLogger(OpenshiftSFTP.class);

    private final ExecutorService executor = Executors.newFixedThreadPool(1);

    private String sccName;

    private PortForward portForward;
    private int localPort;
    private final String serviceAccountName = name() + "-sa";

    @Override
    public int port() {
        return 2222;
    }

    @Override
    public void create() {
        LOG.info("Deploying OpenShift SFTP");

        // Auto-configure trusted CA keys from resources if not already set
        configureTrustedCAKeys();

        sccName = "tnb-sftp-" + OpenshiftClient.get().getNamespace();
        List<ContainerPort> ports = new LinkedList<>();
        ports.add(new ContainerPortBuilder()
            .withName("sftp")
            .withContainerPort(port())
            .withProtocol("TCP").build());

        OpenshiftClient.get().serviceAccounts()
            .createOrReplace(new ServiceAccountBuilder()
                .withNewMetadata()
                .withName(serviceAccountName)
                .endMetadata()
                .build()
            );

        OpenshiftClient.get().addUsersToSecurityContext(
            OpenshiftClient.get().createSecurityContext(sccName, "anyuid", "SYS_CHROOT", "AUDIT_WRITE"),
            OpenshiftClient.get().getServiceAccountRef(serviceAccountName));

        OpenshiftClient.get().createDeployment(Map.of(
            "name", name(),
            "image", image(),
            "env", containerEnvironment(),
            "ports", ports,
            "serviceAccount", serviceAccountName,
            "capabilities", List.of("SYS_CHROOT", "AUDIT_WRITE"),
            "volumes", volumes(),
            "volumeMounts", volumeMounts()
        ));

        ServiceSpecBuilder serviceSpecBuilder = new ServiceSpecBuilder().addToSelector(OpenshiftConfiguration.openshiftDeploymentLabel(), name());

        serviceSpecBuilder.addToPorts(new ServicePortBuilder()
            .withName("sftp")
            .withPort(port())
            .withTargetPort(new IntOrString(port()))
            .build());

        // @formatter:off
        OpenshiftClient.get().services().resource(
            new ServiceBuilder()
                .editOrNewMetadata()
                    .withName(name())
                    .addToLabels(OpenshiftConfiguration.openshiftDeploymentLabel(), name())
                .endMetadata()
                .editOrNewSpec()
                    .addToSelector(OpenshiftConfiguration.openshiftDeploymentLabel(), name())
                    .addToPorts(new ServicePortBuilder()
                        .withName("sftp")
                        .withPort(port())
                        .withTargetPort(new IntOrString(port()))
                        .build())
                    .endSpec()
                .build()
        ).serverSideApply();
        // @formatter:on
    }

    @Override
    public void undeploy() {
        LOG.info("Undeploying OpenShift SFTP");
        OpenshiftClient.get().securityContextConstraints().withName(sccName).delete();
        OpenshiftClient.get().serviceAccounts().withName(serviceAccountName).delete();
        OpenshiftClient.get().services().withName(name()).delete();
        OpenshiftClient.get().apps().deployments().withName(name()).delete();
        WaitUtils.waitFor(() -> servicePod() == null, "Waiting until the pod is removed");
    }

    @Override
    public void openResources() {
        localPort = NetworkUtils.getFreePort();
        portForward = OpenshiftClient.get().services().withName(name()).portForward(port(), localPort);
    }

    @Override
    public void closeResources() {
        executor.shutdownNow();
        IOUtils.closeQuietly(client);
        IOUtils.closeQuietly(portForward);
        NetworkUtils.releasePort(localPort);
    }

    @Override
    public boolean isReady() {
        return Optional.ofNullable(servicePod())
            .filter(PodResource::isReady)
            .map(pod -> OpenshiftClient.get().getLogs(pod.get()).contains("Server listening on"))
            .orElse(false);
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
        return "sftp";
    }

    @Override
    protected SFTPClient makeClient() {
        try {
            SSHClient sshClient = new SSHClient();
            sshClient.addHostKeyVerifier(new PromiscuousVerifier());
            sshClient.connect(externalHostname(), localPort);
            sshClient.authPassword(account().username(), account().password());
            return sshClient.newSFTPClient();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String host() {
        return inClusterHostname();
    }

    @Override
    public String hostForActiveConnection() {
        return servicePod().get().getStatus().getPodIP();
    }

    @Override
    public String externalHostname() {
        return "localhost";
    }

    /**
     * Automatically configures trusted CA keys from classpath resources if certificate authentication is enabled.
     * Looks for ssh-certs/ca_key.pub in the classpath and sets it in the account.
     * Only runs if getConfiguration().isCertificateAuthEnabled() returns true.
     */
    private void configureTrustedCAKeys() {
        // Skip if certificate authentication is not enabled
        if (!getConfiguration().isCertificateAuthEnabled()) {
            LOG.debug("Certificate authentication disabled - skipping CA key configuration");
            return;
        }

        // Only auto-configure if not already explicitly set
        if (account().trustedUserCAKeys() != null && !account().trustedUserCAKeys().isEmpty()) {
            LOG.debug("Trusted CA keys already configured: {}", account().trustedUserCAKeys().substring(0,
                Math.min(50, account().trustedUserCAKeys().length())) + "...");
            return;
        }

        try {
            java.net.URL caKeyUrl = getClass().getClassLoader().getResource("ssh-certs/ca_key.pub");
            if (caKeyUrl != null) {
                String caPublicKey = new String(java.nio.file.Files.readAllBytes(
                    java.nio.file.Paths.get(caKeyUrl.toURI())));
                account().setTrustedUserCAKeys(caPublicKey.trim());
                LOG.info("Auto-configured trusted CA key from classpath: ssh-certs/ca_key.pub");
            } else {
                LOG.warn("Certificate authentication enabled but CA key not found at ssh-certs/ca_key.pub");
            }
        } catch (Exception e) {
            LOG.warn("Failed to auto-configure trusted CA keys from resources: {}", e.getMessage());
        }
    }

    private List<Volume> volumes() {
        // Create volumes for SSH certificates ConfigMap
        return List.of(
            new VolumeBuilder()
                .withNewConfigMap()
                    .withName("sftp-ssh-certs")
                .endConfigMap()
                .withName("ssh-certs")
                .build()
        );
    }

    private List<VolumeMount> volumeMounts() {
        // Create volume mounts
        return List.of(
            new VolumeMountBuilder()
                .withMountPath("/ssh-certs")
                .withName("ssh-certs")
                .withReadOnly(true)
                .build()
        );
    }
}
