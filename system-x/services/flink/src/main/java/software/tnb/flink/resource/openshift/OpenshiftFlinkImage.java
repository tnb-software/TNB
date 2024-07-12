package software.tnb.flink.resource.openshift;

import software.tnb.common.config.OpenshiftConfiguration;
import software.tnb.common.deployment.OpenshiftDeployable;
import software.tnb.common.deployment.WithExternalHostname;
import software.tnb.common.deployment.WithName;
import software.tnb.common.openshift.OpenshiftClient;
import software.tnb.common.utils.HTTPUtils;
import software.tnb.common.utils.WaitUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import io.fabric8.kubernetes.api.model.EnvVar;
import io.fabric8.kubernetes.api.model.IntOrString;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.ServiceAccountBuilder;
import io.fabric8.kubernetes.api.model.ServiceBuilder;
import io.fabric8.kubernetes.api.model.apps.DeploymentBuilder;
import io.fabric8.kubernetes.client.KubernetesClientException;
import io.fabric8.kubernetes.client.dsl.PodResource;
import io.fabric8.kubernetes.client.dsl.Resource;
import io.fabric8.openshift.api.model.Route;
import io.fabric8.openshift.api.model.RouteBuilder;

public class OpenshiftFlinkImage extends OpenshiftFlink implements OpenshiftDeployable, WithExternalHostname, WithName {

    private static final Logger LOG = LoggerFactory.getLogger(OpenshiftFlinkImage.class);

    private Route apiRoute;

    private String sccName;
    private String serviceAccountName;

    @Override
    public void undeploy() {
        LOG.info("Undeploying Flink server");
        OpenshiftClient.get().securityContextConstraints().withName(sccName).delete();
        OpenshiftClient.get().serviceAccounts().withName(serviceAccountName).delete();
        OpenshiftClient.get().apps().deployments().withName(host()).delete();
        OpenshiftClient.get().apps().deployments().withName(nameTaskManager()).delete();
        OpenshiftClient.get().services().withLabel(OpenshiftConfiguration.openshiftDeploymentLabel(), name()).delete();
        WaitUtils.waitFor(() -> Objects.requireNonNull(filterPods(host())).isEmpty(), "Waiting until the pod is removed");
        WaitUtils.waitFor(() -> Objects.requireNonNull(filterPods(nameTaskManager())).isEmpty(), "Waiting until the pod is removed");
    }

    @Override
    public void openResources() {
        apiRoute = OpenshiftClient.get().routes().createOrReplace(new RouteBuilder()
            .editOrNewMetadata()
               .withName(name())
            .endMetadata()
            .editOrNewSpec()
               .withNewTo().withKind("Service").withName("jobmanager-rest").withWeight(100)
               .endTo()
               .withNewPort().withTargetPort(new IntOrString(targetPort())).endPort()
            .endSpec()
        .build());

        WaitUtils.waitFor(() -> HTTPUtils.getInstance().get(externalHostname(), false).isSuccessful()
            , 5, 5000L
            , "Waiting until the Flink API route is ready");
    }

    @Override
    public void closeResources() {
        if (apiRoute != null) {
            OpenshiftClient.get().routes().resource(apiRoute).delete();
        }
    }

    @Override
    public void create() {
        sccName = "tnb-flink-" + OpenshiftClient.get().getNamespace();

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

        LOG.info("Deploying Flink server");

        createDeployments(host(), "jobmanager");

        createJobManagerService();

        createRestService();

        WaitUtils.waitFor(() -> {
            return filterPods(host()) != null && this.isReady(host());
        }, "Waiting for pod ready");

        createDeployments(nameTaskManager(), "taskmanager");

        createTMQueryStateService();

        WaitUtils.waitFor(() -> {
            return filterPods(nameTaskManager()) != null && this.isDeployed();
        }, "Waiting for pod ready");

    }

    private void createDeployments(String containerName, String command) {
        OpenshiftClient.get().apps().deployments().createOrReplace(
            new DeploymentBuilder()
                .withNewMetadata()
                    .withName(containerName)
                    .addToLabels(OpenshiftConfiguration.openshiftDeploymentLabel(), name())
                    .addToLabels("component", containerName)
                    .addToAnnotations("openshift.io/scc", sccName)
                .endMetadata()
                .editOrNewSpec()
                .editOrNewSelector()
                    .addToMatchLabels(OpenshiftConfiguration.openshiftDeploymentLabel(), name())
                    .addToMatchLabels("component", containerName)
                .endSelector()
                .withReplicas(1)
                .editOrNewTemplate()
                    .editOrNewMetadata()
                        .addToLabels(OpenshiftConfiguration.openshiftDeploymentLabel(), name())
                        .addToLabels("component", containerName)
                    .endMetadata()
                    .editOrNewSpec()
                        .withServiceAccount(serviceAccountName)
                        .addNewContainer()
                            .withName(containerName)
                            .withImage(image())
                            .addAllToEnv(containerEnvironment().entrySet().stream().map(e -> new EnvVar(e.getKey(), e.getValue(), null))
                                .collect(Collectors.toList()))
                            .withCommand("/docker-entrypoint.sh")
                            .withArgs(command)
                            .addNewPort()
                                .withContainerPort(6123)
                                .withName("rpc")
                            .endPort()
                            .addNewPort()
                                .withContainerPort(6124)
                                .withName("blob-server")
                            .endPort()
                            .addNewPort()
                                .withContainerPort(8081)
                                .withName("webui")
                            .endPort()
                            .editOrNewSecurityContext()
                                .editOrNewCapabilities()
                                    .addToAdd("SYS_CHROOT")
                                .endCapabilities()
                            .endSecurityContext()
                        .endContainer()
                    .endSpec()
                .endTemplate()
            .endSpec()
        .build());
    }

    private void createRestService() {
        OpenshiftClient.get().services().createOrReplace(new ServiceBuilder()
            .editOrNewMetadata()
                .withName("jobmanager-rest")
                .addToLabels(OpenshiftConfiguration.openshiftDeploymentLabel(), name())
            .endMetadata()
            .editOrNewSpec()
                .withType("NodePort")
                .addNewPort()
                    .withName("rest")
                    .withPort(8081)
                    .withTargetPort(new IntOrString(8081))
                    .withNodePort(30081)
                    .endPort()
                .addToSelector(OpenshiftConfiguration.openshiftDeploymentLabel(), name())
                .addToSelector("component", "jobmanager")
            .endSpec()
        .build());
    }

    private void createJobManagerService() {
        OpenshiftClient.get().services().createOrReplace(new ServiceBuilder()
            .editOrNewMetadata()
                .withName("jobmanager")
                .addToLabels(OpenshiftConfiguration.openshiftDeploymentLabel(), name())
            .endMetadata()
            .editOrNewSpec()
                .withType("ClusterIP")
                .addNewPort()
                    .withName("rpc")
                    .withPort(6123)
                .endPort()
                .addNewPort()
                    .withName("blob-server")
                    .withPort(6124)
                .endPort()
                .addNewPort()
                    .withName("webui")
                    .withPort(8081)
                .endPort()
                .addToSelector(OpenshiftConfiguration.openshiftDeploymentLabel(), name())
                .addToSelector("component", host())
            .endSpec()
        .build());
    }

    private void createTMQueryStateService() {
        OpenshiftClient.get().services().createOrReplace(new ServiceBuilder()
            .editOrNewMetadata()
                .withName("taskmanager-query-state")
                .addToLabels(OpenshiftConfiguration.openshiftDeploymentLabel(), name())
            .endMetadata()
            .editOrNewSpec()
                .withType("NodePort")
                .addNewPort()
                    .withName("query-state")
                    .withPort(6125)
                    .withTargetPort(new IntOrString(6125))
                    .withNodePort(30025)
                .endPort()
                .addToSelector(OpenshiftConfiguration.openshiftDeploymentLabel(), name())
                .addToSelector("component", nameTaskManager())
            .endSpec()
        .build());
    }

    @Override
    public boolean isDeployed() {
        return !OpenshiftClient.get().apps().deployments().withLabel(OpenshiftConfiguration.openshiftDeploymentLabel(), name()).list()
            .getItems().isEmpty();
    }

    public String nameTaskManager() {
        return "taskmanager";
    }

    @Override
    public Predicate<Pod> podSelector() {
        return podSelector(nameTaskManager());
    }

    @Override
    public String externalHostname() {
        return "http://" + apiRoute.getSpec().getHost();
    }

    public int targetPort() {
        return port();
    }

    private Map<String, String> containerEnvironment() {
        return Map.of(
            "FLINK_PROPERTIES", "jobmanager.rpc.address: jobmanager"
        );
    }

    private List<PodResource> filterPods(String pod) {
        try {
            return OpenshiftClient.get().pods().list().getItems().stream()
                .filter(podSelector(pod))
                .map(p -> OpenshiftClient.get().pods().withName(p.getMetadata().getName()))
                .collect(Collectors.toList());
        } catch (KubernetesClientException kce) {
            // Just in case of some transient error
            return null;
        }
    }

    private Predicate<Pod> podSelector(String selector) {
        return p -> OpenshiftClient.get().hasLabels(p, Map.of("component", selector));
    }

    private boolean isReady(String pod) {
        final List<PodResource> servicePods = filterPods(pod);
        return servicePods != null && !servicePods.isEmpty() && servicePods.stream().allMatch(Resource::isReady);
    }
}
