package software.tnb.cyberark.cyberarkvault.resource.openshift;

import software.tnb.common.config.OpenshiftConfiguration;
import software.tnb.common.deployment.OpenshiftDeployable;
import software.tnb.common.deployment.WithExternalHostname;
import software.tnb.common.deployment.WithName;
import software.tnb.common.openshift.OpenshiftClient;
import software.tnb.common.service.ServiceFactory;
import software.tnb.common.utils.WaitUtils;
import software.tnb.common.utils.waiter.Waiter;
import software.tnb.cyberark.cyberarkvault.service.CyberarkVault;
import software.tnb.db.postgres.service.PostgreSQL;

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
import io.fabric8.kubernetes.api.model.Probe;
import io.fabric8.kubernetes.api.model.ProbeBuilder;
import io.fabric8.kubernetes.api.model.ServiceBuilder;
import io.fabric8.openshift.api.model.Route;
import io.fabric8.openshift.api.model.RouteBuilder;
import io.fabric8.openshift.api.model.RoutePortBuilder;

@AutoService(CyberarkVault.class)
public class OpenshiftCyberarkVault extends CyberarkVault implements OpenshiftDeployable, WithName, WithExternalHostname {
    private static final Logger LOG = LoggerFactory.getLogger(OpenshiftCyberarkVault.class);
    private static final int PORT = 8080;

    private final PostgreSQL postgres = ServiceFactory.create(PostgreSQL.class);
    private final String serviceAccountName = name() + "-sa";

    @Override
    public void deploy() {
        LOG.info("Deploying CyberarkVault's PostgreSQL dependency");
        try {
            postgres.beforeAll(null);
        } catch (Exception e) {
            throw new RuntimeException("Unable to deploy the PostgreSQL dependency", e);
        }
        OpenshiftDeployable.super.deploy();
    }

    @Override
    public void create() {
        OpenshiftClient.get().createServiceAccount(serviceAccountName);

        // The cyberark/conjur image expects to run as root, so grant its service account the anyuid SCC
        OpenshiftClient.get().addUsersToSecurityContext(
            OpenshiftClient.get().createSecurityContext(sccName(), "anyuid"),
            OpenshiftClient.get().getServiceAccountRef(serviceAccountName));

        List<ContainerPort> ports = List.of(new ContainerPortBuilder().withName(name()).withContainerPort(PORT).build());

        //@formatter:off
        Probe readinessProbe = new ProbeBuilder()
            .withNewHttpGet()
                .withPath("/")
                .withPort(new IntOrString(PORT))
            .endHttpGet()
            .withInitialDelaySeconds(15)
            .withPeriodSeconds(10)
            .withTimeoutSeconds(5)
            .withFailureThreshold(30)
            .build();

        OpenshiftClient.get().createDeployment(Map.of(
            "name", name(),
            "image", image(),
            "env", containerEnvironment(databaseUrl(), PORT),
            "ports", ports,
            "args", List.of("server"),
            "readinessProbe", readinessProbe,
            "serviceAccount", serviceAccountName
        ));

        OpenshiftClient.get().services().resource(
            new ServiceBuilder()
                .withNewMetadata()
                    .withName(name())
                    .addToLabels(OpenshiftConfiguration.openshiftDeploymentLabel(), name())
                .endMetadata()
                .withNewSpec()
                    .addToSelector(OpenshiftConfiguration.openshiftDeploymentLabel(), name())
                    .addNewPort()
                        .withName(name())
                        .withPort(PORT)
                        .withTargetPort(new IntOrString(PORT))
                    .endPort()
                .endSpec()
            .build()
        ).serverSideApply();

        OpenshiftClient.get().routes().resource(new RouteBuilder()
            .withNewMetadata()
                .withName(name())
                .addToLabels(OpenshiftConfiguration.openshiftDeploymentLabel(), name())
            .endMetadata()
            .withNewSpec()
                .withPort(new RoutePortBuilder().withNewTargetPort(PORT).build())
                .withNewTo()
                    .withKind("Service")
                    .withName(name())
                    .withWeight(100)
                .endTo()
                .withNewTls()
                    .withTermination("edge")
                    .withInsecureEdgeTerminationPolicy("Redirect")
                .endTls()
            .endSpec()
            .build()
        ).serverSideApply();
        //@formatter:on
    }

    @Override
    public void undeploy() {
        OpenshiftClient.get().securityContextConstraints().withName(sccName()).delete();
        OpenshiftClient.get().services().withName(name()).delete();
        OpenshiftClient.get().apps().deployments().withName(name()).delete();
        WaitUtils.waitFor(new Waiter(() -> servicePod() == null, "Waiting until the CyberarkVault pod is removed"));
        try {
            postgres.afterAll(null);
        } catch (Exception e) {
            LOG.warn("Unable to undeploy the PostgreSQL dependency", e);
        }
    }

    @Override
    public void openResources() {
        account().setApiKey(createAccount());
    }

    @Override
    public void closeResources() {
        validation = null;
    }

    private String createAccount() {
        LOG.info("Creating CyberarkVault account '{}'", account().accountName());
        return parseApiKey(OpenshiftClient.get().podShell(servicePod().get())
            .executeWithBash("conjurctl account create " + account().accountName()).getOutput());
    }

    private String databaseUrl() {
        return String.format("postgres://%s:%s@%s:%d/%s",
            postgres.account().username(), postgres.account().password(), postgres.host(), postgres.port(), postgres.account().database());
    }

    private String sccName() {
        return "tnb-cyberarkvault-" + OpenshiftClient.get().getNamespace();
    }

    @Override
    public String url() {
        return externalHostname();
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
        return "cyberarkvault";
    }

    @Override
    public String externalHostname() {
        final List<Route> routes = OpenshiftClient.get().routes()
            .withLabel(OpenshiftConfiguration.openshiftDeploymentLabel(), name()).list().getItems();

        if (routes.size() != 1) {
            throw new RuntimeException("Expected single route to be present but was " + routes.size());
        }

        return "https://" + routes.get(0).getSpec().getHost();
    }
}
