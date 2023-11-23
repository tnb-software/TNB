package software.tnb.mail.resource.openshift;

import software.tnb.common.config.OpenshiftConfiguration;
import software.tnb.common.deployment.ReusableOpenshiftDeployable;
import software.tnb.common.deployment.WithExternalHostname;
import software.tnb.common.deployment.WithInClusterHostname;
import software.tnb.common.deployment.WithName;
import software.tnb.common.openshift.OpenshiftClient;
import software.tnb.common.utils.IOUtils;
import software.tnb.common.utils.NetworkUtils;
import software.tnb.common.utils.WaitUtils;
import software.tnb.mail.service.MailServer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.auto.service.AutoService;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import io.fabric8.kubernetes.api.model.ContainerPort;
import io.fabric8.kubernetes.api.model.ContainerPortBuilder;
import io.fabric8.kubernetes.api.model.IntOrString;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.ServiceAccountBuilder;
import io.fabric8.kubernetes.api.model.ServiceBuilder;
import io.fabric8.kubernetes.api.model.ServicePortBuilder;
import io.fabric8.kubernetes.api.model.ServiceSpecBuilder;
import io.fabric8.kubernetes.client.PortForward;
import io.fabric8.kubernetes.client.dsl.PodResource;
import io.fabric8.openshift.api.model.DeploymentConfigBuilder;
import io.fabric8.openshift.api.model.RouteBuilder;
import io.fabric8.openshift.api.model.RoutePortBuilder;
import io.fabric8.openshift.api.model.RouteTargetReferenceBuilder;

@AutoService(MailServer.class)
public class OpenshiftMailServer extends MailServer implements ReusableOpenshiftDeployable, WithName, WithInClusterHostname, WithExternalHostname {
    private static final Logger LOG = LoggerFactory.getLogger(OpenshiftMailServer.class);
    private String sccName;
    private String serviceAccountName;
    private PortForward smtpPortForward;
    private int smtpLocalPort;

    private final Map<String, Integer> services = Map.of("smtp", SMTP_PORT, "http", HTTP_PORT, "imap", IMAP_PORT, "pop3", POP3_PORT);

    @Override
    public void create() {
        LOG.info("Deploying OpenShift JamesServer");
        sccName = "tnb-james-" + OpenshiftClient.get().getNamespace();

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

        List<ContainerPort> ports = new LinkedList<>();
        services.forEach((name, port) -> {
            ports.add(new ContainerPortBuilder()
                .withName(name() + "-" + name)
                .withContainerPort(port)
                .withProtocol("TCP").build());
            ServiceSpecBuilder serviceSpecBuilder = new ServiceSpecBuilder().addToSelector(OpenshiftConfiguration.openshiftDeploymentLabel(), name());
            serviceSpecBuilder.addToPorts(new ServicePortBuilder()
                .withName(name() + "-" + name)
                .withPort(port)
                .withTargetPort(new IntOrString(port))
                .build());
            LOG.debug("Creating service {}", name() + "-" + name);
            OpenshiftClient.get().services().createOrReplace(
                new ServiceBuilder()
                    .editOrNewMetadata()
                    .withName(name() + "-" + name)
                    .addToLabels(OpenshiftConfiguration.openshiftDeploymentLabel(), name())
                    .endMetadata()
                    .editOrNewSpecLike(serviceSpecBuilder.build())
                    .endSpec()
                    .build()
            );
        });

        // @formatter:off
        LOG.debug("Creating deploymentconfig {}", name());
        OpenshiftClient.get().deploymentConfigs().createOrReplace(
          new DeploymentConfigBuilder()
            .editOrNewMetadata()
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
                            .withImage(image())
                            .addAllToPorts(ports)
                            .editOrNewSecurityContext()
                                .editOrNewCapabilities()
                                    .addToAdd("SYS_CHROOT")
                                .endCapabilities()
                            .endSecurityContext()
                        .endContainer()
                    .endSpec()
                .endTemplate()
                .addNewTrigger()
                    .withType("ConfigChange")
                .endTrigger()
            .endSpec()
            .build()
        );
        OpenshiftClient.get().routes().createOrReplace(new RouteBuilder()
            .withNewMetadata()
                .withName(name() + "-http")
                .addToLabels(OpenshiftConfiguration.openshiftDeploymentLabel(), name())
            .endMetadata()
            .withNewSpec()
                .withPort(new RoutePortBuilder().withNewTargetPort(HTTP_PORT).build())
                .withTo(new RouteTargetReferenceBuilder().withKind("Service").withName(name() + "-http").build())
            .endSpec()
            .build()
        );
        // @formatter:on
    }

    @Override
    public void undeploy() {
        LOG.info("Undeploying OpenShift Mail");
        OpenshiftClient.get().securityContextConstraints().withName(sccName).delete();
        OpenshiftClient.get().serviceAccounts().withName(serviceAccountName).delete();

        services.forEach((name, port) -> {
            LOG.debug("Deleting service {}", name());
            OpenshiftClient.get().services().withName(name() + "-" + name).delete();
        });

        LOG.debug("Deleting deploymentconfig {}", name());
        OpenshiftClient.get().deploymentConfigs().withName(name()).delete();
        WaitUtils.waitFor(() -> servicePod() == null, "Waiting until the pod is removed");
    }

    @Override
    public void openResources() {
        LOG.debug("Creating port-forward to {} for port {}", name(), SMTP_PORT);
        smtpLocalPort = NetworkUtils.getFreePort();
        smtpPortForward = OpenshiftClient.get().services().withName(name() + "-smtp").portForward(SMTP_PORT, smtpLocalPort);
    }

    @Override
    public boolean isReady() {
        final PodResource pod = servicePod();
        return pod != null && pod.isReady() && OpenshiftClient.get().getLogs(pod.get()).contains("AddUser command executed sucessfully in");
    }

    @Override
    public boolean isDeployed() {
        return !OpenshiftClient.get().deploymentConfigs().withLabel(OpenshiftConfiguration.openshiftDeploymentLabel(), name()).list()
                .getItems().isEmpty();
    }

    @Override
    public Predicate<Pod> podSelector() {
        return WithName.super.podSelector();
    }

    @Override
    public String name() {
        return "james";
    }

    @Override
    public void cleanup() {
    }

    @Override
    public void closeResources() {
        validation = null;
        if (smtpPortForward != null && smtpPortForward.isAlive()) {
            LOG.debug("Closing port-forward");
            IOUtils.closeQuietly(smtpPortForward);
        }
        NetworkUtils.releasePort(smtpLocalPort);
    }

    @Override
    public String externalHostname() {
        return OpenshiftClient.get().getRoute(name() + "-http").getSpec().getHost();
    }

    @Override
    public String smtpHostname() {
        return OpenshiftClient.get().getClusterHostname(name() + "-smtp");
    }

    @Override
    public String imapHostname() {
        return OpenshiftClient.get().getClusterHostname(name() + "-imap");
    }

    @Override
    public String pop3Hostname() {
        return OpenshiftClient.get().getClusterHostname(name() + "-pop3");
    }

    @Override
    public String httpHostname() {
        return externalHostname();
    }

    @Override
    public String smtpValidationHostname() {
        return "localhost:" + smtpLocalPort;
    }
}
