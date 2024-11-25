package software.tnb.opentelemetry.resource.openshift;

import software.tnb.common.deployment.OpenshiftDeployable;
import software.tnb.common.deployment.WithCustomResource;
import software.tnb.common.deployment.WithOperatorHub;
import software.tnb.common.openshift.OpenshiftClient;
import software.tnb.opentelemetry.service.OpenTelemetryCollector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.auto.service.AutoService;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

import io.fabric8.kubernetes.api.model.GenericKubernetesResource;
import io.fabric8.kubernetes.api.model.GenericKubernetesResourceBuilder;
import io.fabric8.kubernetes.api.model.Pod;

@AutoService(OpenTelemetryCollector.class)
public class OpenshiftOpenTelemetryCollector extends OpenTelemetryCollector implements OpenshiftDeployable, WithOperatorHub, WithCustomResource {

    private static final Logger LOG = LoggerFactory.getLogger(OpenshiftOpenTelemetryCollector.class);
    private static final String OTEL_INSTANCE_NAME = "otel-tnb";

    @Override
    public void undeploy() {
        //cluster wide operator can be in use
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

        OpenshiftClient.get().genericKubernetesResources(apiVersion(), kind()).resource(customResource()).create();
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

        return new GenericKubernetesResourceBuilder()
            .withKind(kind())
            .withApiVersion(apiVersion())
            .withNewMetadata()
            .withName(OTEL_INSTANCE_NAME)
            .endMetadata()
            .withAdditionalProperties(Map.of("spec", spec)).build();
    }
}
