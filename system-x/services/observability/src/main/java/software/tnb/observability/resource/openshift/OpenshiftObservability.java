package software.tnb.observability.resource.openshift;

import software.tnb.common.deployment.OpenshiftDeployable;
import software.tnb.common.deployment.WithOperatorHub;
import software.tnb.common.openshift.OpenshiftClient;
import software.tnb.common.utils.WaitUtils;
import software.tnb.observability.service.Observability;
import software.tnb.observability.validation.ObservabilityValidation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.auto.service.AutoService;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import io.fabric8.kubernetes.api.model.GenericKubernetesResource;
import io.fabric8.kubernetes.api.model.GenericKubernetesResourceBuilder;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.KubernetesClientException;
import io.fabric8.kubernetes.client.dsl.PodResource;

@AutoService(Observability.class)
public class OpenshiftObservability extends Observability implements OpenshiftDeployable, WithOperatorHub {

    private static final Logger LOG = LoggerFactory.getLogger(OpenshiftObservability.class);

    @Override
    public ObservabilityValidation validation() {
        validation = Optional.ofNullable(validation)
            .orElseGet(() -> new ObservabilityValidation(getConfiguration().getTempoStackName()));
        return validation;
    }

    @Override
    public void undeploy() {
        //cluster wide operator can be in use
    }

    @Override
    public void openResources() {
        if (getConfiguration().isDeployTracesUiPlugin()) {
            //check if the traces endpoint is reachable
            WaitUtils.waitFor(() -> validation().isTraceEndpointAvailable(), 10, 10000
                , "Wait until the trace endpoint is available on the OCP console");
        }
    }

    @Override
    public String targetNamespace() {
        return "openshift-cluster-observability-operator";
    }

    @Override
    public void closeResources() {

    }

    @Override
    public String subscriptionName() {
        return operatorName();
    }

    @Override
    public void create() {
        LOG.debug("Creating cluster observability subscription");
        //create target namespace if not exists
        OpenshiftClient.get().createNamespace(targetNamespace());

        // Create subscription if needed
        createSubscription();

        createAccordingToConfiguration();
    }

    private void createAccordingToConfiguration() {
        if (getConfiguration().isDeployTracesUiPlugin()) {
            LOG.debug("creating tracing UIPlugin");
            final String apiVersion = "observability.openshift.io/v1alpha1";
            final String kind = "UIPlugin";
            final GenericKubernetesResource cr = new GenericKubernetesResourceBuilder()
                .withKind(kind)
                .withApiVersion(apiVersion)
                .withNewMetadata()
                .withName("distributed-tracing")
                .endMetadata()
                .withAdditionalProperties(Map.of("spec", Map.of("type", "DistributedTracing"))).build();
            OpenshiftClient.get().genericKubernetesResources(apiVersion, kind).resource(cr).create();
        }
    }

    @Override
    public boolean isDeployed() {

        if (OpenshiftClient.get().operatorHub().subscriptions().inNamespace(targetNamespace()).withName(subscriptionName()).get() == null) {
            return false;
        }

        boolean operatorReady = OpenshiftClient.get().arePodsWithLabelReady(targetNamespace()
            , "app.kubernetes.io/name", "observability-operator");
        //check pods according to configuration
        boolean traceUIPluginReady = true;
        if (getConfiguration().isDeployTracesUiPlugin()) {
            traceUIPluginReady = OpenshiftClient.get().arePodsWithLabelReady(targetNamespace()
                , "app.kubernetes.io/instance", "distributed-tracing");

        }

        return operatorReady && traceUIPluginReady;
    }

    @Override
    public Predicate<Pod> podSelector() {
        return p -> OpenshiftClient.get().hasLabels(p, Map.of("app.kubernetes.io/managed-by", "observability-operator"));
    }

    @Override
    public List<PodResource> servicePods() {
        try {
            return OpenshiftClient.get().inNamespace(targetNamespace()).pods().list().getItems().stream()
                .filter(podSelector())
                .map(p -> OpenshiftClient.get().pods().inNamespace(targetNamespace()).withName(p.getMetadata().getName()))
                .collect(Collectors.toList());
        } catch (KubernetesClientException kce) {
            // Just in case of some transient error
            return null;
        }
    }

    @Override
    public boolean clusterWide() {
        return true;
    }

    @Override
    public String operatorName() {
        return "cluster-observability-operator";
    }
}
