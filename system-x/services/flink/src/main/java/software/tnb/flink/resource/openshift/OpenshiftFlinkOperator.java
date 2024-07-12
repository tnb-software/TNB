package software.tnb.flink.resource.openshift;

import software.tnb.common.deployment.OpenshiftDeployable;
import software.tnb.common.deployment.WithCustomResource;
import software.tnb.common.deployment.WithExternalHostname;
import software.tnb.common.deployment.WithName;
import software.tnb.common.deployment.WithOperatorHub;
import software.tnb.common.openshift.OpenshiftClient;
import software.tnb.common.utils.HTTPUtils;
import software.tnb.common.utils.WaitUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import io.fabric8.kubernetes.api.model.GenericKubernetesResource;
import io.fabric8.kubernetes.api.model.GenericKubernetesResourceBuilder;
import io.fabric8.kubernetes.api.model.IntOrString;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodList;
import io.fabric8.kubernetes.client.readiness.Readiness;
import io.fabric8.openshift.api.model.Route;
import io.fabric8.openshift.api.model.RouteBuilder;

public class OpenshiftFlinkOperator extends OpenshiftFlink implements OpenshiftDeployable, WithExternalHostname, WithOperatorHub, WithCustomResource,
    WithName {

    private static final Logger LOG = LoggerFactory.getLogger(OpenshiftFlink.class);

    private Route apiRoute;

    @Override
    public String operatorChannel() {
        return "alpha";
    }

    @Override
    public String operatorName() {
        return "flink-kubernetes-operator";
    }

    @Override
    public String operatorCatalog() {
        return "community-operators";
    }

    @Override
    public void undeploy() {
        LOG.info("Undeploying Flink resources");
        OpenshiftClient.get().genericKubernetesResources(apiVersion(), kind()).delete();
        WaitUtils.waitFor(() -> servicePod() == null, "Waiting until the pod is removed");
    }

    @Override
    public void openResources() {
        apiRoute = OpenshiftClient.get().routes().createOrReplace(new RouteBuilder()
            .editOrNewMetadata()
            .withName(name())
            .endMetadata()
            .editOrNewSpec()
            .withNewTo().withKind("Service").withName(host()).withWeight(100)
            .endTo()
            .withNewPort().withTargetPort(new IntOrString("rest")).endPort()
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
        LOG.debug("Creating Flink subscription");

        // Create subscription if needed
        createSubscription();

        WaitUtils.waitFor(() -> {
            final PodList pods = OpenshiftClient.get().pods().withLabel("app.kubernetes.io/name", "flink-kubernetes-operator").list();
            return pods.getItems().size() > 0 && pods.getItems().stream().allMatch(Readiness::isPodReady);
        }, "Wait for operator PODs are ready");

        OpenshiftClient.get().genericKubernetesResources(apiVersion(), kind()).resource(customResource()).create();
    }

    @Override
    public boolean isDeployed() {
        List<Pod> pods = OpenshiftClient.get().pods().withLabel("app.kubernetes.io/name", "flink-kubernetes-operator").list().getItems();
        return pods.size() == 1 && pods.stream().allMatch(Readiness::isPodReady)
            && servicePods().size() == 2;
    }

    @Override
    public Predicate<Pod> podSelector() {
        return p -> OpenshiftClient.get().hasLabels(p, Map.of("app", name()))
            && (OpenshiftClient.get().hasLabels(p, Map.of("component", "jobmanager"))
            || OpenshiftClient.get().hasLabels(p, Map.of("component", "taskmanager")));
    }

    @Override
    public String kind() {
        return "FlinkDeployment";
    }

    @Override
    public String apiVersion() {
        return "flink.apache.org/v1beta1";
    }

    @Override
    public GenericKubernetesResource customResource() {
        Map<String, Object> spec = new HashMap<>();
        Map<String, String> flinkConfiguration = Map.of("taskmanager.numberOfTaskSlots", "2");
        spec.put("flinkConfiguration", flinkConfiguration);
        spec.put("flinkVersion", "v1_18");
        spec.put("image", image());
        spec.put("mode", "standalone");
        spec.put("serviceAccount", "flink");
        Map<String, Object> resource = Map.of("cpu", 1, "memory", "2048m");
        Map<String, Object> jobManager = Map.of("replicas", 1, "resource", resource);
        Map<String, Object> taskManager = Map.of("resource", resource);
        spec.put("jobManager", jobManager);
        spec.put("taskManager", taskManager);

        return new GenericKubernetesResourceBuilder()
            .withKind(kind())
            .withApiVersion(apiVersion())
            .withNewMetadata()
            .withName(name())
            .endMetadata()
            .withAdditionalProperties(Map.of("spec", spec)).build();
    }

    @Override
    public String externalHostname() {
        return "http://" + apiRoute.getSpec().getHost();
    }

}
