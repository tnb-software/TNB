package software.tnb.hyperfoil.resource.openshift;

import software.tnb.common.deployment.ReusableOpenshiftDeployable;
import software.tnb.common.deployment.WithCustomResource;
import software.tnb.common.deployment.WithExternalHostname;
import software.tnb.common.deployment.WithOperatorHub;
import software.tnb.common.openshift.OpenshiftClient;
import software.tnb.common.utils.WaitUtils;
import software.tnb.hyperfoil.service.Hyperfoil;
import software.tnb.hyperfoil.service.HyperfoilConfiguration;
import software.tnb.hyperfoil.validation.generated.ApiException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.auto.service.AutoService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import cz.xtf.core.openshift.helpers.ResourceParsers;
import io.fabric8.kubernetes.api.model.GenericKubernetesResource;
import io.fabric8.kubernetes.api.model.GenericKubernetesResourceBuilder;
import io.fabric8.kubernetes.api.model.Pod;

@AutoService(Hyperfoil.class)
public class OpenshiftHyperfoil extends Hyperfoil implements ReusableOpenshiftDeployable, WithExternalHostname, WithOperatorHub, WithCustomResource {

    private static final Logger LOG = LoggerFactory.getLogger(OpenshiftHyperfoil.class);

    private static final String APP_NAME = "hyperfoil-" + OpenshiftClient.get().getNamespace();

    @Override
    public void undeploy() {
        if (!HyperfoilConfiguration.keepRunning()) {
            OpenshiftClient.get().genericKubernetesResources(apiVersion(), kind()).delete();
            WaitUtils.waitFor(() -> servicePod() == null, "Waiting until the pod is removed");
            deleteSubscription(() -> OpenshiftClient.get().getLabeledPods("control-plane", "controller-manager")
                .stream().noneMatch(p -> p.getMetadata().getName().contains("hyperfoil")));
        }
    }

    @Override
    public void openResources() {
        WaitUtils.waitFor(() -> {
            try {
                return validation().getDefaultApi().openApiWithHttpInfo().getStatusCode() == 200;
            } catch (ApiException e) {
                return false;
            }
        }, "Waiting for responding api endpoint");
    }

    @Override
    public void closeResources() {
    }

    @Override
    public void create() {
        LOG.debug("Creating Hyperfoil instance");
        createSubscription();
        if (HyperfoilConfiguration.agentLogConf().isPresent()) {
            createAgentLogConfigMap(HyperfoilConfiguration.agentLogConf().get());
        }
        OpenshiftClient.get().genericKubernetesResources(apiVersion(), kind()).resource(customResource()).create();
    }

    @Override
    public boolean isDeployed() {
        List<Pod> pods = OpenshiftClient.get().pods().withLabel("control-plane", "controller-manager").list().getItems()
            .stream()
            .filter(p -> p.getMetadata().getName().contains("hyperfoil")).toList();
        return pods.size() == 1 && ResourceParsers.isPodReady(pods.get(0))
            && OpenshiftClient.get().genericKubernetesResources(apiVersion(), kind()).list().getItems().size() == 1;
    }

    @Override
    public Predicate<Pod> podSelector() {
        return p -> OpenshiftClient.get().hasLabels(p, Map.of("app", APP_NAME));
    }

    @Override
    public String connection() {
        return "https://" + hyperfoilUrl() + ":" + getPortMapping(443);
    }

    @Override
    public String hyperfoilUrl() {
        return externalHostname();
    }

    @Override
    public int getPortMapping(int port) {
        return port;
    }

    private void createAgentLogConfigMap(String agentLogConfPath) {
        try {
            OpenshiftClient.get().createConfigMap(HyperfoilConfiguration.agentLogMapConfig(),
                Map.of(HyperfoilConfiguration.agentLogFileName(), Files.readString(Path.of(agentLogConfPath))));
        } catch (IOException e) {
            LOG.error("Error on Hyperfoil creation", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void cleanup() {
        // all benchmarks should be managed by the tests
    }

    @Override
    public String externalHostname() {
        return OpenshiftClient.get().getRoute(APP_NAME).getSpec().getHost();
    }

    @Override
    public String operatorCatalog() {
        return "community-operators";
    }

    @Override
    public String operatorChannel() {
        return "alpha";
    }

    @Override
    public String operatorName() {
        return "hyperfoil-bundle";
    }

    @Override
    public String kind() {
        return "Hyperfoil";
    }

    @Override
    public String apiVersion() {
        return "hyperfoil.io/v1alpha2";
    }

    @Override
    public GenericKubernetesResource customResource() {
        LOG.info("Hyperfoil version is set to: " + HyperfoilConfiguration.getHyperfoilVersion());
        Map<String, Object> spec = Map.of("agentDeployTimeout", 120000, "version", HyperfoilConfiguration.getHyperfoilVersion(), "route",
            Map.of("host", OpenshiftClient.get().generateHostname("hyperfoil")) // "hyperfoil.apps.mycloud.example.com"
        );
        if (HyperfoilConfiguration.agentLogConf().isPresent()) {
            spec = new HashMap<>(spec);
            spec.put("log", HyperfoilConfiguration.agentLogMapConfig() + "/" + HyperfoilConfiguration.agentLogFileName());
        }
        return new GenericKubernetesResourceBuilder()
            .withNewMetadata()
            .withName(APP_NAME)
            .withNamespace(OpenshiftClient.get().getNamespace())
            .endMetadata()
            .withAdditionalProperties(Map.of("spec", spec)).build();
    }
}
