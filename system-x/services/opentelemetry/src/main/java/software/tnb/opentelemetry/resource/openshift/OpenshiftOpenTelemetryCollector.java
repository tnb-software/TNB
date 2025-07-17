package software.tnb.opentelemetry.resource.openshift;

import software.tnb.common.deployment.OpenshiftDeployable;
import software.tnb.common.deployment.WithCustomResource;
import software.tnb.common.deployment.WithOperatorHub;
import software.tnb.common.openshift.OpenshiftClient;
import software.tnb.common.utils.StringUtils;
import software.tnb.common.utils.WaitUtils;
import software.tnb.opentelemetry.service.OpenTelemetryCollector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.auto.service.AutoService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import io.fabric8.kubernetes.api.model.GenericKubernetesResource;
import io.fabric8.kubernetes.api.model.GenericKubernetesResourceBuilder;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.rbac.ClusterRoleBindingBuilder;
import io.fabric8.kubernetes.api.model.rbac.RoleRef;
import io.fabric8.kubernetes.api.model.rbac.Subject;

@AutoService(OpenTelemetryCollector.class)
public class OpenshiftOpenTelemetryCollector extends OpenTelemetryCollector implements OpenshiftDeployable, WithOperatorHub, WithCustomResource {

    private static final Logger LOG = LoggerFactory.getLogger(OpenshiftOpenTelemetryCollector.class);
    private static final String OTEL_INSTANCE_NAME = "otel-tnb";
    private final String saName = "otel-collector";
    private final String roleBindingName = "tempostack-traces-" + StringUtils.getRandomAlphanumStringOfLength(4);

    @Override
    public void undeploy() {

        OpenshiftClient.get().deleteCustomResource(apiVersion().split("/")[0], apiVersion().split("/")[1], kind(), OTEL_INSTANCE_NAME);
        WaitUtils.waitFor(() -> servicePods().isEmpty(), "wait until collector pods are terminated");

        if (getConfiguration().isTempostack()) {
            //remove the cluster role binding
            OpenshiftClient.get().rbac().clusterRoleBindings().withName(roleBindingName).delete();
            //remove service account
            OpenshiftClient.get().deleteServiceAccount(saName);
        }
    }

    @Override
    public void openResources() {

    }

    @Override
    public String targetNamespace() {
        return "openshift-opentelemetry-operator";
    }

    @Override
    public void closeResources() {

    }

    @Override
    public void create() {
        LOG.debug("Creating OpenTelemetryCollector subscription");
        //create target namespace if not exists
        OpenshiftClient.get().createNamespace(targetNamespace());

        // Create subscription if needed
        createSubscription();

        if (getConfiguration().isTempostack()) {
            // create serviceAccount
            createAndConfigureSA();
        }

        OpenshiftClient.get().genericKubernetesResources(apiVersion(), kind()).resource(customResource()).create();
    }

    private void createAndConfigureSA() {
        OpenshiftClient.get().createServiceAccount(saName);

        List<Subject> subjects = new ArrayList<>();
        Subject subject = new Subject();
        subject.setKind("ServiceAccount");
        subject.setName(saName);
        subject.setNamespace(OpenshiftClient.get().getNamespace());
        subjects.add(subject);
        RoleRef roleRef = new RoleRef();
        roleRef.setApiGroup("rbac.authorization.k8s.io");
        roleRef.setKind("ClusterRole");
        roleRef.setName("tempostack-traces-write"); //created in tempostack
        OpenshiftClient.get().rbac().clusterRoleBindings().resource(new ClusterRoleBindingBuilder()
                .withNewMetadata().withName(roleBindingName).endMetadata()
                .withRoleRef(roleRef)
                .addAllToSubjects(subjects)
                .build())
            .serverSideApply();
    }

    @Override
    public boolean isDeployed() {
        return OpenshiftClient.get().apps().deployments()
            .inNamespace(targetNamespace()).list().getItems().stream()
            .anyMatch(deployment -> OTEL_INSTANCE_NAME.equals(deployment.getMetadata().getName()));
    }

    @Override
    public Predicate<Pod> podSelector() {
        return p -> OpenshiftClient.get().hasLabels(p, Map.of("app.kubernetes.io/component", "opentelemetry-collector"
            , "app.kubernetes.io/name", OTEL_INSTANCE_NAME + "-collector"));
    }

    @Override
    public String operatorName() {
        return "opentelemetry-product";
    }

    @Override
    public String getLog() {
        return OpenshiftClient.get().getPodLog(servicePod().get());
    }

    @Override
    public String getGrpcEndpoint() {
        return getHeadlessEndpoint() + ":" + getConfiguration().getGrpcReceiverPort();
    }

    @Override
    public String getHttpEndpoint() {
        return getHeadlessEndpoint() + ":" + getConfiguration().getHttpReceiverPort();
    }

    private String getHeadlessEndpoint() {
        return String.format("http://%s-collector-headless", OTEL_INSTANCE_NAME);
    }

    @Override
    public boolean clusterWide() {
        return true;
    }

    @Override
    public String kind() {
        return "OpenTelemetryCollector";
    }

    @Override
    public String apiVersion() {
        return "opentelemetry.io/v1beta1";
    }

    @Override
    public GenericKubernetesResource customResource() {
        Map<String, Object> spec = new HashMap<>();
        spec.put("config", getConfiguration().getCollectorConfigurationAsMap());
        if (!getConfiguration().getPorts().isEmpty()) {
            spec.put("ports", getConfiguration().getPorts());
        }
        spec.put("replicas", getConfiguration().getReplicas());
        if (getConfiguration().isTempostack()) {
            spec.put("serviceAccount", saName);
        }

        return new GenericKubernetesResourceBuilder()
            .withKind(kind())
            .withApiVersion(apiVersion())
            .withNewMetadata()
            .withName(OTEL_INSTANCE_NAME)
            .endMetadata()
            .withAdditionalProperties(Map.of("spec", spec)).build();
    }
}
