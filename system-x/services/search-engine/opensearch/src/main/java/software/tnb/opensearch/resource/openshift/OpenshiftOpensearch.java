package software.tnb.opensearch.resource.openshift;

import software.tnb.common.config.OpenshiftConfiguration;
import software.tnb.common.deployment.ReusableOpenshiftDeployable;
import software.tnb.common.deployment.WithExternalHostname;
import software.tnb.common.deployment.WithName;
import software.tnb.common.openshift.OpenshiftClient;
import software.tnb.common.utils.MapUtils;
import software.tnb.common.utils.WaitUtils;
import software.tnb.opensearch.service.Opensearch;

import org.junit.jupiter.api.extension.ExtensionContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.auto.service.AutoService;

import java.util.List;
import java.util.function.Predicate;

import io.fabric8.kubernetes.api.model.IntOrString;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.ServiceAccountBuilder;
import io.fabric8.kubernetes.api.model.ServiceBuilder;
import io.fabric8.kubernetes.client.dsl.PodResource;
import io.fabric8.openshift.api.model.DeploymentConfig;
import io.fabric8.openshift.api.model.DeploymentConfigBuilder;
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
        OpenshiftClient.get().deploymentConfigs().withName(name()).delete();
        OpenshiftClient.get().services().withLabel(OpenshiftConfiguration.openshiftDeploymentLabel(), name()).delete();
        WaitUtils.waitFor(() -> servicePod() == null, "Waiting until the pod is removed");
    }

    @Override
    public void create() {

        sccName = "tnb-opensearch-" + OpenshiftClient.get().getNamespace();

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

        LOG.info("Deploying Opensearch server");

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
                                .withImage(image())
                                .withEnv(MapUtils.toEnvVars(containerEnv()))
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
                .withType("ClusterIP")
                .addNewPort()
                    .withProtocol("TCP")
                    .withPort(80)
                    .withTargetPort(new IntOrString(port()))
                .endPort()
                .withInternalTrafficPolicy("Cluster")
                //.withIpFamilies("SingleStack")
                .withSessionAffinity("None")
                .addToSelector(OpenshiftConfiguration.openshiftDeploymentLabel(), name())
            .endSpec()
            .build());

        WaitUtils.waitFor(() -> {
            return servicePod() != null && this.isDeployed();
        }, "Waiting for pod ready");

        OpenshiftClient.get().routes().createOrReplace(new RouteBuilder()
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
        );

    }

    @Override
    public boolean isDeployed() {
        final DeploymentConfig dc = OpenshiftClient.get().deploymentConfigs().withName(name()).get();
        return dc != null && !dc.isMarkedForDeletion();
    }

    @Override
    public boolean isReady() {
        final PodResource pod = servicePod();
        return pod != null && pod.isReady() && OpenshiftClient.get().getLogs(pod.get()).contains("ML configuration initialized successfully");
    }

    @Override
    public Predicate<Pod> podSelector() {
        return super.podSelector();
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
