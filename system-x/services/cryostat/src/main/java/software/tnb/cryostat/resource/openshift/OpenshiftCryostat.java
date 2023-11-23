package software.tnb.cryostat.resource.openshift;

import software.tnb.common.deployment.ReusableOpenshiftDeployable;
import software.tnb.common.deployment.WithCustomResource;
import software.tnb.common.deployment.WithOperatorHub;
import software.tnb.common.openshift.OpenshiftClient;
import software.tnb.common.utils.HTTPUtils;
import software.tnb.common.utils.WaitUtils;
import software.tnb.cryostat.client.CryostatClient;
import software.tnb.cryostat.client.openshift.OpenshiftCryostatClient;
import software.tnb.cryostat.service.Cryostat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.auto.service.AutoService;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import cz.xtf.core.openshift.helpers.ResourceParsers;
import io.fabric8.kubernetes.api.model.GenericKubernetesResource;
import io.fabric8.kubernetes.api.model.GenericKubernetesResourceBuilder;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.dsl.PodResource;
import okhttp3.Request;

@AutoService(Cryostat.class)
public class OpenshiftCryostat extends Cryostat implements ReusableOpenshiftDeployable, WithOperatorHub, WithCustomResource {

    private static final Logger LOG = LoggerFactory.getLogger(OpenshiftCryostat.class);
    private static final String APP_NAME = "cryostat-" + OpenshiftClient.get().getNamespace();

    @Override
    public void undeploy() {
        OpenshiftClient.get().genericKubernetesResources(apiVersion(), kind()).delete();
        WaitUtils.waitFor(() -> servicePod() == null, "Waiting until the pod is removed");
        deleteSubscription(() -> OpenshiftClient.get().getLabeledPods("control-plane", "controller-manager")
            .stream().noneMatch(p -> p.getMetadata().getName().contains("cryostat")));
    }

    /**
     * Open all resources needed after the service is deployed - initialize clients and stuff.
     */
    @Override
    public void openResources() {
        validation().init();
    }

    /**
     * Close all resources used after before the service is undeployed.
     */
    @Override
    public void closeResources() {

    }

    @Override
    public void create() {
        LOG.debug("Creating Cryostat instance");
        // Create subscription
        createSubscription();

        OpenshiftClient.get().genericKubernetesResources(apiVersion(), kind()).resource(customResource()).create();
    }

    @Override
    public boolean isReady() {
        final PodResource pod = servicePod();
        try {
            return pod != null && pod.isReady()
                && HTTPUtils.trustAllSslClient().newCall(new Request.Builder().get().url(String.format("https://%s/health"
                , OpenshiftClient.get().getRoute(APP_NAME).getSpec().getHost())).build()).execute().isSuccessful();
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public boolean isDeployed() {
        // Cryostat / Hyperfoil operators do not have any unique label, so the pod name is used as well
        List<Pod> pods = OpenshiftClient.get().pods().withLabel("control-plane", "controller-manager").list().getItems().stream()
            .filter(p -> p.getMetadata().getName().contains("cryostat")).toList();
        return pods.size() == 1 && ResourceParsers.isPodReady(pods.get(0))
            && OpenshiftClient.get().genericKubernetesResources(apiVersion(), kind()).list().getItems().size() == 1;
    }

    @Override
    public Predicate<Pod> podSelector() {
        return p -> OpenshiftClient.get().hasLabels(p, Map.of("kind", "cryostat", "app", APP_NAME));
    }

    @Override
    public String connectionUrl() {
        return String.format("https://%s", OpenshiftClient.get().getRoute(APP_NAME).getSpec().getHost());
    }

    @Override
    public CryostatClient client() {
        return new OpenshiftCryostatClient(connectionUrl());
    }

    @Override
    public int getPortMapping(int port) {
        return 8181;
    }

    @Override
    public void cleanup() {
        //all recording should be removed from the tests
    }

    @Override
    public String operatorName() {
        return "cryostat-operator";
    }

    @Override
    public String kind() {
        return "Cryostat";
    }

    @Override
    public String apiVersion() {
        return "operator.cryostat.io/v1beta1";
    }

    @Override
    public GenericKubernetesResource customResource() {
        return new GenericKubernetesResourceBuilder()
            .withKind(kind())
            .withApiVersion(apiVersion())
            .withNewMetadata()
            .withName(APP_NAME)
            .withNamespace(OpenshiftClient.get().getNamespace())
            .endMetadata()
            .withAdditionalProperties(Map.of("spec", Map.of(
                "enableCertManager", false
                , "minimal", true
            )))
            .build();
    }
}
