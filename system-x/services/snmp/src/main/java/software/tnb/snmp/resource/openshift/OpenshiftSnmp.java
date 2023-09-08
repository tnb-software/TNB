package software.tnb.snmp.resource.openshift;

import software.tnb.common.config.OpenshiftConfiguration;
import software.tnb.common.deployment.ReusableOpenshiftDeployable;
import software.tnb.common.deployment.WithName;
import software.tnb.common.openshift.OpenshiftClient;
import software.tnb.common.utils.WaitUtils;
import software.tnb.snmp.service.SnmpServer;

import org.junit.jupiter.api.extension.ExtensionContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.auto.service.AutoService;

import java.io.IOException;
import java.util.function.Predicate;

import io.fabric8.kubernetes.api.model.IntOrString;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.ServiceAccountBuilder;
import io.fabric8.kubernetes.api.model.ServiceBuilder;
import io.fabric8.openshift.api.model.DeploymentConfig;
import io.fabric8.openshift.api.model.DeploymentConfigBuilder;

@AutoService(SnmpServer.class)
public class OpenshiftSnmp extends SnmpServer implements ReusableOpenshiftDeployable, WithName {

    private static final Logger LOG = LoggerFactory.getLogger(OpenshiftSnmp.class);
    private String sccName;
    private String serviceAccountName;

    @Override
    public void undeploy() {
        LOG.info("Undeploying SNMP server");
        OpenshiftClient.get().deploymentConfigs().withName(name()).delete();
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

        sccName = "tnb-snmp-" + OpenshiftClient.get().getNamespace();

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

        LOG.info("Deploying SNMP server");

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
                                    .withContainerPort(port())
                                    .withProtocol("UDP")
                                    .withName(name() + "-get")
                                .endPort()
                                .addNewPort()
                                    .withContainerPort(trapPort())
                                    .withProtocol("UDP")
                                    .withName(name() + "-trap")
                                .endPort()
                                .editOrNewSecurityContext()
                                    .editOrNewCapabilities()
                                        .addNewAdd("SYS_CHROOT")
                                    .endCapabilities()
                                .endSecurityContext()
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
                .addNewPort()
                    .withName(name() + "-get")
                    .withProtocol("UDP")
                    .withPort(port())
                    .withTargetPort(new IntOrString(port()))
                .endPort()
                .addNewPort()
                    .withName(name() + "-trap")
                    .withProtocol("UDP")
                    .withPort(trapPort())
                    .withTargetPort(new IntOrString(trapPort()))
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
        final DeploymentConfig dc = OpenshiftClient.get().deploymentConfigs().withName(name()).get();
        return dc != null && !dc.isMarkedForDeletion();
    }

    @Override
    public Predicate<Pod> podSelector() {
        return WithName.super.podSelector();
    }

    public String execInContainer(String... commands) {
        try {
            return new String(servicePod().redirectingOutput().exec(commands).getOutput().readAllBytes());
        } catch (IOException e) {
            throw new RuntimeException("Unable to read command output: " + e);
        }
    }

    @Override
    public String name() {
        return "snmp";
    }

    @Override
    public String host() {
        return name();
    }

    @Override
    public int port() {
        return SNMPD_LISTENING_PORT;
    }

    @Override
    public String getLog() {
        return OpenshiftClient.get().getLogs(servicePod().get());
    }

    @Override
    public String trapHost() {
        return "0.0.0.0";
    }

    @Override
    public int trapPort() {
        return SNMPTRAPD_LISTENING_PORT;
    }

    @Override
    public void cleanup() {

    }

    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {
        ReusableOpenshiftDeployable.super.beforeAll(extensionContext);
    }

    @Override
    public void afterAll(ExtensionContext extensionContext) throws Exception {
        ReusableOpenshiftDeployable.super.afterAll(extensionContext);
    }
}
