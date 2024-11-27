package software.tnb.opensearch.resource.openshift;

import software.tnb.common.config.OpenshiftConfiguration;
import software.tnb.common.deployment.ReusableOpenshiftDeployable;
import software.tnb.common.deployment.WithExternalHostname;
import software.tnb.common.deployment.WithName;
import software.tnb.common.openshift.OpenshiftClient;
import software.tnb.common.utils.WaitUtils;
import software.tnb.opensearch.service.Opensearch;

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
import io.fabric8.kubernetes.client.dsl.PodResource;
import io.fabric8.openshift.api.model.Route;
import io.fabric8.openshift.api.model.RouteBuilder;
import io.fabric8.openshift.api.model.RoutePortBuilder;

@AutoService(Opensearch.class)
public class OpenshiftOpensearch extends Opensearch implements ReusableOpenshiftDeployable, WithName, WithExternalHostname {

    private static final Logger LOG = LoggerFactory.getLogger(OpenshiftOpensearch.class);
    private String sccName;
    private String serviceAccountName;

    @Override
    public void undeploy() {
        LOG.info("Undeploying Opensearch server");
        OpenshiftClient.get().securityContextConstraints().withName(sccName).delete();
        OpenshiftClient.get().serviceAccounts().withName(serviceAccountName).delete();
        OpenshiftClient.get().apps().deployments().withName(name()).delete();
        OpenshiftClient.get().services().withLabel(OpenshiftConfiguration.openshiftDeploymentLabel(), name()).delete();
        WaitUtils.waitFor(() -> servicePod() == null, "Waiting until the pod is removed");
    }

    @Override
    public void create() {
        sccName = "tnb-opensearch-" + OpenshiftClient.get().getNamespace();

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
            new ContainerPortBuilder().withName(name()).withContainerPort(port()).build()
        );

        LOG.info("Deploying Opensearch server");
        OpenshiftClient.get().createDeployment(Map.of(
            "name", name(),
            "image", image(),
            "env", containerEnv(),
            "ports", ports,
            "scc", sccName,
            "serviceAccount", serviceAccountName,
            "capabilities", List.of("SYS_CHROOT")
        ));

        // @formatter:off
        OpenshiftClient.get().services().resource(new ServiceBuilder()
            .editOrNewMetadata()
                .withName(name())
                .addToLabels(OpenshiftConfiguration.openshiftDeploymentLabel(), name())
            .endMetadata()
            .editOrNewSpec()
                .withType("ClusterIP")
                .addNewPort()
                    .withProtocol("TCP")
                    .withPort(80)
                    .withTargetPort(new IntOrString(port()))
                .endPort()
                .withInternalTrafficPolicy("Cluster")
                .withSessionAffinity("None")
                .addToSelector(OpenshiftConfiguration.openshiftDeploymentLabel(), name())
            .endSpec()
            .build())
            .serverSideApply();

        OpenshiftClient.get().routes().resource(new RouteBuilder()
            .withNewMetadata()
                .withName(name())
                .addToLabels(OpenshiftConfiguration.openshiftDeploymentLabel(), name())
            .endMetadata()
            .withNewSpec()
                .withPort(new RoutePortBuilder().withNewTargetPort(port()).build())
                .withNewTo()
                    .withKind("Service")
                    .withName(name())
                    .withWeight(100)
                .endTo()
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
    public boolean isReady() {
        final PodResource pod = servicePod();
        return pod != null && pod.isReady() && OpenshiftClient.get().getLogs(pod.get()).contains("ML configuration initialized successfully");
    }

    @Override
    public Predicate<Pod> podSelector() {
        return WithName.super.podSelector();
    }

    @Override
    public String name() {
        return "opensearch";
    }

    @Override
    public String host() {
        return externalHostname();
    }

    @Override
    public String url() {
        return host() + ":80";
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

    @Override
    public String externalHostname() {
        final List<Route> routes = OpenshiftClient.get().routes()
            .withLabel(OpenshiftConfiguration.openshiftDeploymentLabel(), name()).list().getItems();

        if (routes.size() != 1) {
            throw new RuntimeException("Expected single route to be present but was " + routes.size());
        }

        return routes.get(0).getSpec().getHost();
    }
}
