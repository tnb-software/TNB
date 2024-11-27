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
import io.fabric8.openshift.api.model.RouteBuilder;
import io.fabric8.openshift.api.model.RoutePortBuilder;
import io.fabric8.openshift.api.model.RouteTargetReferenceBuilder;

@AutoService(MailServer.class)
public class OpenshiftMailServer extends MailServer implements ReusableOpenshiftDeployable, WithName, WithInClusterHostname, WithExternalHostname {
    private static final Logger LOG = LoggerFactory.getLogger(OpenshiftMailServer.class);
    private final String serviceAccountName = name() + "-sa";
    private static String sccName;
    private PortForward smtpPortForward;
    private int smtpLocalPort;
    private PortForward imapPortForward;
    private int imapLocalPort;

    private final Map<String, Integer> services = Map.of("smtp", SMTP_PORT, "http", HTTP_PORT, "imap", IMAP_PORT, "pop3", POP3_PORT);

    @Override
    public void create() {
        LOG.info("Deploying OpenShift JamesServer");
        sccName = "tnb-james-" + OpenshiftClient.get().getNamespace();

        OpenshiftClient.get().serviceAccounts().resource(new ServiceAccountBuilder()
                .withNewMetadata()
                .withName(serviceAccountName)
                .endMetadata()
                .build()
            )
            .serverSideApply();

        OpenshiftClient.get().addUsersToSecurityContext(
            OpenshiftClient.get().createSecurityContext(sccName, "anyuid", "SYS_CHROOT"),
            OpenshiftClient.get().getServiceAccountRef(serviceAccountName));

        // @formatter:off
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
            OpenshiftClient.get().services().resource(
                new ServiceBuilder()
                    .editOrNewMetadata()
                        .withName(name() + "-" + name)
                        .addToLabels(OpenshiftConfiguration.openshiftDeploymentLabel(), name())
                    .endMetadata()
                    .editOrNewSpecLike(serviceSpecBuilder.build())
                    .endSpec()
                    .build()
            ).serverSideApply();
        });

        OpenshiftClient.get().createDeployment(Map.of(
            "name", name(),
            "image", image(),
            "ports", ports,
            "scc", sccName,
            "serviceAccount", serviceAccountName,
            "capabilities", List.of("SYS_CHROOT")
        ));

        // @formatter:off
        OpenshiftClient.get().routes().resource(new RouteBuilder()
            .withNewMetadata()
                .withName(name() + "-http")
                .addToLabels(OpenshiftConfiguration.openshiftDeploymentLabel(), name())
            .endMetadata()
            .withNewSpec()
                .withPort(new RoutePortBuilder().withNewTargetPort(HTTP_PORT).build())
                .withTo(new RouteTargetReferenceBuilder().withKind("Service").withName(name() + "-http").build())
            .endSpec()
            .build()
        ).serverSideApply();
        // @formatter:on
    }

    @Override
    public void undeploy() {
        LOG.info("Undeploying OpenShift Mail");
        OpenshiftClient.get().securityContextConstraints().withName(sccName).delete();
        OpenshiftClient.get().serviceAccounts().withName(serviceAccountName).delete();

        services.forEach((name, port) -> {
            LOG.debug("Deleting service {}", name() + "-" + name);
            OpenshiftClient.get().services().withName(name() + "-" + name).delete();
        });

        LOG.debug("Deleting deployment {}", name());
        OpenshiftClient.get().apps().deployments().withName(name()).delete();
        WaitUtils.waitFor(() -> servicePod() == null, "Waiting until the pod is removed");
    }

    @Override
    public void openResources() {
        LOG.debug("Creating port-forward to {} for port {}", name(), SMTP_PORT);
        smtpLocalPort = NetworkUtils.getFreePort();
        smtpPortForward = OpenshiftClient.get().services().withName(name() + "-smtp").portForward(SMTP_PORT, smtpLocalPort);
        imapLocalPort = NetworkUtils.getFreePort();
        imapPortForward = OpenshiftClient.get().services().withName(name() + "-imap").portForward(IMAP_PORT, imapLocalPort);
    }

    @Override
    public boolean isReady() {
        final PodResource pod = servicePod();
        return pod != null && pod.isReady() && OpenshiftClient.get().getLogs(pod.get()).contains("AddUser command executed sucessfully in");
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
        if (imapPortForward != null && imapPortForward.isAlive()) {
            LOG.debug("Closing port-forward");
            IOUtils.closeQuietly(imapPortForward);
        }
        NetworkUtils.releasePort(imapLocalPort);
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
    protected String imapExternalHostname() {
        return "localhost:" + imapLocalPort;
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
