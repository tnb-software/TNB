package software.tnb.hawtio.resource.opesnhift;

import software.tnb.common.deployment.ReusableOpenshiftDeployable;
import software.tnb.common.deployment.WithCustomResource;
import software.tnb.common.deployment.WithOperatorHub;
import software.tnb.common.openshift.OpenshiftClient;
import software.tnb.common.utils.WaitUtils;
import software.tnb.hawtio.client.openshift.OpenshiftHawtioClient;
import software.tnb.hawtio.service.Hawtio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.auto.service.AutoService;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import cz.xtf.core.openshift.helpers.ResourceParsers;
import io.fabric8.kubernetes.api.model.GenericKubernetesResource;
import io.fabric8.kubernetes.api.model.GenericKubernetesResourceBuilder;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.dsl.Gettable;
import io.fabric8.kubernetes.client.dsl.PodResource;

@AutoService(Hawtio.class)
public class OpenshiftHawtio extends Hawtio implements ReusableOpenshiftDeployable, WithOperatorHub, WithCustomResource {

    private static final Logger LOG = LoggerFactory.getLogger(OpenshiftHawtio.class);
    private static final String APP_NAME = "hawtio-online";

    @Override
    public void undeploy() {
        OpenshiftClient.get().genericKubernetesResources(apiVersion(), kind()).delete();
        WaitUtils.waitFor(() -> servicePod() == null, "Waiting until the pod is removed");
        deleteSubscription(() -> OpenshiftClient.get().getLabeledPods("name", "hawtio-operator").isEmpty());
    }

    /**
     * Open all resources needed after the service is deployed - initialize clients and stuff.
     */
    @Override
    public void openResources() {
        this.client = new OpenshiftHawtioClient(getHawtioUrl());
    }

    /**
     * Close all resources used after before the service is undeployed.
     */
    @Override
    public void closeResources() {
        //do nothing
    }

    @Override
    public void create() {
        LOG.debug("Creating Hawtio instance");
        // Create subscription
        createSubscription();

        WaitUtils.waitFor(() -> !OpenshiftClient.get().getLabeledPods("name", "hawtio-operator").isEmpty()
            , "Wait for the operator has been installed");

        WaitUtils.waitFor(() -> ResourceParsers.isPodReady(OpenshiftClient.get().getLabeledPods("name", "hawtio-operator").get(0))
            , "Wait for the operator pod is ready");

        OpenshiftClient.get().genericKubernetesResources(apiVersion(), kind()).resource(customResource()).create();
    }

    @Override
    public boolean isReady() {
        final PodResource pod = servicePod();
        return pod != null && pod.isReady();
    }

    @Override
    public boolean isDeployed() {
        List<Pod> pods = servicePods().stream().map(Gettable::get).toList();
        return pods.size() == 1 && ResourceParsers.isPodReady(pods.get(0))
            && OpenshiftClient.get().genericKubernetesResources(apiVersion(), kind()).list().getItems().size() == 1;
    }

    @Override
    public Predicate<Pod> podSelector() {
        return p -> OpenshiftClient.get().hasLabels(p, Map.of("app", "hawtio", "deployment", APP_NAME));
    }

    @Override
    public void cleanup() {
        //do nothing
    }

    @Override
    public String getHawtioUrl() {
        return String.format("https://%s", OpenshiftClient.get().getRoute(APP_NAME).getSpec().getHost());
    }

    @Override
    public String operatorChannel() {
        return "stable-v1.0";
    }

    @Override
    public String operatorName() {
        return "red-hat-hawtio-operator";
    }

    @Override
    public String kind() {
        return "Hawtio";
    }

    @Override
    public String apiVersion() {
        return "hawt.io/v1";
    }

    @Override
    public GenericKubernetesResource customResource() {

        Map<String, Object> authMap = Map.of("clientCertCheckSchedule", "* */12 * * *"
            , "clientCertExpirationPeriod", 24);
        Map<String, Object> resourcesMap = Map.of("limits", Map.of("cpu", "1", "memory", "200Mi")
            , "requests", Map.of("cpu", "200m", "memory", "32Mi"));

        return new GenericKubernetesResourceBuilder()
            .withKind(kind())
            .withApiVersion(apiVersion())
            .withNewMetadata()
            .withName(APP_NAME)
            .withNamespace(OpenshiftClient.get().getNamespace())
            .endMetadata()
            .withAdditionalProperties(Map.of("spec", Map.of(
                "type", "Namespace"
                , "replicas", 1
                , "auth", authMap
                , "resources", resourcesMap
            )))
            .build();
    }
}
