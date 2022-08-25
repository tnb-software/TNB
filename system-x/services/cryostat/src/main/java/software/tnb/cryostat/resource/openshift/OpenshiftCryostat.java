package software.tnb.cryostat.resource.openshift;

import software.tnb.common.deployment.ReusableOpenshiftDeployable;
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
import java.util.stream.Collectors;

import cz.xtf.core.openshift.helpers.ResourceParsers;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.dsl.base.CustomResourceDefinitionContext;
import io.fabric8.openshift.client.internal.readiness.OpenShiftReadiness;
import okhttp3.Request;

@AutoService(Cryostat.class)
public class OpenshiftCryostat extends Cryostat implements ReusableOpenshiftDeployable {

    private static final Logger LOG = LoggerFactory.getLogger(OpenshiftCryostat.class);

    private static final String CHANNEL = "stable";
    private static final String OPERATOR_NAME = "cryostat-operator";
    private static final String SOURCE = "redhat-operators";
    private static final String SUBSCRIPTION_NAME = "tnb-cryostat";
    private static final String SUBSCRIPTION_NAMESPACE = "openshift-marketplace";

    private static final String APP_NAME = "cryostat-" + OpenshiftClient.get().getNamespace();

    private static final CustomResourceDefinitionContext CRYOSTAT_CTX = new CustomResourceDefinitionContext.Builder()
        .withName("Cryostat")
        .withGroup("operator.cryostat.io")
        .withVersion("v1beta1")
        .withPlural("cryostats")
        .withScope("Namespaced")
        .build();

    @Override
    public void undeploy() {
        try {
            OpenshiftClient.get().customResource(CRYOSTAT_CTX).delete(OpenshiftClient.get().getNamespace(), APP_NAME, true);
            WaitUtils.waitFor(() -> OpenshiftClient.get().getLabeledPods(Map.of("kind", "cryostat", "app", APP_NAME))
                .isEmpty(), "Waiting until Cryostat pods are deleted");
            OpenshiftClient.get().deleteSubscription(SUBSCRIPTION_NAME);
        } catch (IOException e) {
            LOG.error("Error on Cryostat deletetion", e);
            throw new RuntimeException(e);
        }
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
        OpenshiftClient.get().createSubscription(CHANNEL, OPERATOR_NAME, SOURCE, SUBSCRIPTION_NAME, SUBSCRIPTION_NAMESPACE);
        OpenshiftClient.get().waitForInstallPlanToComplete(SUBSCRIPTION_NAME);

        try {
            OpenshiftClient.get().customResource(CRYOSTAT_CTX).createOrReplace(getCryostatDefinition());
        } catch (IOException e) {
            LOG.error("Error on Cryostat creation", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isReady() {
        final List<Pod> labeledPods = OpenshiftClient.get().getLabeledPods(Map.of("kind", "cryostat", "app", APP_NAME));
        try {
            return labeledPods.size() > 0
                && labeledPods.stream().allMatch(OpenShiftReadiness::isPodReady)
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
            .filter(p -> p.getMetadata().getName().contains("cryostat")).collect(Collectors.toList());
        return pods.size() == 1 && ResourceParsers.isPodReady(pods.get(0))
            && ((List) OpenshiftClient.get().customResource(CRYOSTAT_CTX).list().get("items")).size() == 1;
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

    private Map<String, Object> getCryostatDefinition() {
        Map<String, Object> metadata = Map.of("name", APP_NAME
            , "namespace", OpenshiftClient.get().getNamespace());
        Map<String, Object> spec = Map.of("enableCertManager", false
            , "minimal", true);
        return Map.of("kind", CRYOSTAT_CTX.getName()
            , "apiVersion", String.format("%s/%s", CRYOSTAT_CTX.getGroup(), CRYOSTAT_CTX.getVersion())
            , "metadata", metadata
            , "spec", spec);
    }

    @Override
    public void cleanup() {
        //all recording should be removed from the tests
    }
}
