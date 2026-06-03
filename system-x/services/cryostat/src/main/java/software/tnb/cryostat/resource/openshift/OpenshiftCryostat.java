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
import java.util.Map;
import java.util.function.Predicate;

import io.fabric8.kubernetes.api.model.GenericKubernetesResource;
import io.fabric8.kubernetes.api.model.GenericKubernetesResourceBuilder;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.dsl.PodResource;
import okhttp3.Request;

@AutoService(Cryostat.class)
public class OpenshiftCryostat extends Cryostat implements ReusableOpenshiftDeployable, WithOperatorHub, WithCustomResource {

    private static final Logger LOG = LoggerFactory.getLogger(OpenshiftCryostat.class);

    private static String appName() {
        return "cryostat-" + OpenshiftClient.get().getNamespace();
    }

    @Override
    public void undeploy() {
        OpenshiftClient.get().genericKubernetesResources(apiVersion(), kind())
            .inNamespace(OpenshiftClient.get().getNamespace()).delete();
        WaitUtils.waitFor(() -> servicePod() == null, "Waiting until the pod is removed");
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
        if (OpenshiftClient.get().apps().deployments().inNamespace(targetNamespace())
            .list().getItems().stream().noneMatch(d -> d.getMetadata().getName().contains("cryostat"))) {
            LOG.debug("Cryostat operator not found in {}, creating subscription", targetNamespace());
            createSubscription();
        } else {
            LOG.debug("Cryostat operator already installed in {}, skipping subscription", targetNamespace());
        }

        LOG.debug("Creating Cryostat custom resource in namespace {}", OpenshiftClient.get().getNamespace());
        OpenshiftClient.get().genericKubernetesResources(apiVersion(), kind())
            .inNamespace(OpenshiftClient.get().getNamespace()).resource(customResource()).create();
    }

    @Override
    public boolean isReady() {
        final PodResource pod = servicePod();
        if (pod == null || !pod.isReady()) {
            return false;
        }
        try {
            var route = OpenshiftClient.get().getRoute(appName());
            if (route == null) {
                return false;
            }
            int code = HTTPUtils.trustAllSslClient().newCall(new Request.Builder().get().url(String.format("https://%s/health/liveness"
                , route.getSpec().getHost())).build()).execute().code();
            return code == 200 || code == 204;
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public boolean isDeployed() {
        return OpenshiftClient.get().apps().deployments().inNamespace(targetNamespace())
            .list().getItems().stream().anyMatch(d -> d.getMetadata().getName().contains("cryostat"))
            && OpenshiftClient.get().genericKubernetesResources(apiVersion(), kind())
            .inNamespace(OpenshiftClient.get().getNamespace()).list().getItems().size() == 1;
    }

    @Override
    public Predicate<Pod> podSelector() {
        return p -> OpenshiftClient.get().hasLabels(p, Map.of("kind", "cryostat", "app", appName()));
    }

    @Override
    public String connectionUrl() {
        return String.format("https://%s", OpenshiftClient.get().getRoute(appName()).getSpec().getHost());
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
    public boolean clusterWide() {
        return true;
    }

    @Override
    public String targetNamespace() {
        return "openshift-operators";
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
        return "operator.cryostat.io/v1beta2";
    }

    @Override
    public GenericKubernetesResource customResource() {
        return new GenericKubernetesResourceBuilder()
            .withKind(kind())
            .withApiVersion(apiVersion())
            .withNewMetadata()
            .withName(appName())
            .withNamespace(OpenshiftClient.get().getNamespace())
            .endMetadata()
            .withAdditionalProperties(Map.of("spec", Map.of(
                "enableCertManager", false
                , "targetNamespaces", java.util.List.of(OpenshiftClient.get().getNamespace())
                , "reportOptions", Map.of("replicas", 1)
            )))
            .build();
    }
}
