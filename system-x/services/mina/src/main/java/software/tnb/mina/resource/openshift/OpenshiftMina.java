package software.tnb.mina.resource.openshift;

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
import software.tnb.mina.service.Mina;

import org.junit.jupiter.api.extension.ExtensionContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.auto.service.AutoService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import io.fabric8.kubernetes.api.model.ConfigMapBuilder;
import io.fabric8.kubernetes.api.model.ConfigMapVolumeSourceBuilder;
import io.fabric8.kubernetes.api.model.ContainerPort;
import io.fabric8.kubernetes.api.model.ContainerPortBuilder;
import io.fabric8.kubernetes.api.model.IntOrString;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.ServiceAccountBuilder;
import io.fabric8.kubernetes.api.model.ServiceBuilder;
import io.fabric8.kubernetes.api.model.Volume;
import io.fabric8.kubernetes.api.model.VolumeBuilder;
import io.fabric8.kubernetes.api.model.VolumeMount;
import io.fabric8.kubernetes.api.model.VolumeMountBuilder;
import io.fabric8.kubernetes.client.PortForward;

@AutoService(Mina.class)
public class OpenshiftMina extends Mina implements OpenshiftDeployable, WithName, WithInClusterHostname, WithExternalHostname {

    private static final Logger LOG = LoggerFactory.getLogger(OpenshiftMina.class);
    private static final String PUBKEY_CONFIGMAP_NAME = "mina-authorized-keys";
    private String sccName;
    private String serviceAccountName;
    private PortForward portForward;
    private int localPort;

    @Override
    public void undeploy() {
        LOG.info("Undeploying Mina SSHD server");
        OpenshiftClient.get().configMaps().withLabel(OpenshiftConfiguration.openshiftDeploymentLabel(), name()).delete();
        OpenshiftClient.get().configMaps().withName(PUBKEY_CONFIGMAP_NAME).delete();
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

    @Override
    public void create() {
        LOG.info("Deploying Mina SSHD server");

        sccName = "tnb-mina-" + OpenshiftClient.get().getNamespace();

        serviceAccountName = name() + "-sa";

        OpenshiftClient.get().serviceAccounts().resource(new ServiceAccountBuilder()
            .withNewMetadata()
            .withName(serviceAccountName)
            .endMetadata()
            .build()
        ).serverSideApply();

        OpenshiftClient.get().addUsersToSecurityContext(
            OpenshiftClient.get().createSecurityContext(sccName, "anyuid"),
            OpenshiftClient.get().getServiceAccountRef(serviceAccountName));

        List<ContainerPort> ports = List.of(
            new ContainerPortBuilder().withName(name()).withProtocol("TCP").withContainerPort(port()).build()
        );

        Map<String, Object> deploymentMap = new HashMap<>();
        deploymentMap.put("name", name());
        deploymentMap.put("image", image());
        deploymentMap.put("serviceAccount", serviceAccountName);
        deploymentMap.put("scc", sccName);
        deploymentMap.put("ports", ports);

        Path publicKeyPath = getConfiguration().publicKeyPath();
        if (publicKeyPath != null) {
            createPublicKeyConfigMap(publicKeyPath);

            List<Volume> volumes = List.of(
                new VolumeBuilder().withName(PUBKEY_CONFIGMAP_NAME)
                    .withConfigMap(new ConfigMapVolumeSourceBuilder()
                        .withName(PUBKEY_CONFIGMAP_NAME)
                        .build()
                    ).build()
            );
            List<VolumeMount> volumeMounts = List.of(
                new VolumeMountBuilder()
                    .withName(PUBKEY_CONFIGMAP_NAME)
                    .withMountPath("/home/test/.ssh")
                    .build()
            );
            deploymentMap.put("volumes", volumes);
            deploymentMap.put("volumeMounts", volumeMounts);
        }

        OpenshiftClient.get().createDeployment(deploymentMap);

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

    private void createPublicKeyConfigMap(Path publicKeyPath) {
        try {
            String publicKeyContent = Files.readString(publicKeyPath);
            OpenshiftClient.get().configMaps().resource(new ConfigMapBuilder()
                .withNewMetadata()
                .withName(PUBKEY_CONFIGMAP_NAME)
                .addToLabels(OpenshiftConfiguration.openshiftDeploymentLabel(), name())
                .endMetadata()
                .addToData("authorized_keys", publicKeyContent)
                .build()
            ).serverSideApply();
        } catch (IOException e) {
            throw new RuntimeException("Failed to read public key file: " + publicKeyPath, e);
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
    public String name() {
        return "mina";
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
