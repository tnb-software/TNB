package software.tnb.hyperfoil.resource.openshift.deployment;

import software.tnb.common.deployment.ReusableOpenshiftDeployable;
import software.tnb.common.deployment.WithExternalHostname;
import software.tnb.common.openshift.OpenshiftClient;
import software.tnb.common.utils.WaitUtils;
import software.tnb.hyperfoil.service.Hyperfoil;
import software.tnb.hyperfoil.service.HyperfoilConfiguration;
import software.tnb.hyperfoil.validation.generated.ApiException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.auto.service.AutoService;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.function.Predicate;

import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.apps.Deployment;

@AutoService(Hyperfoil.class)
public class DeploymentHyperfoil extends Hyperfoil implements ReusableOpenshiftDeployable, WithExternalHostname {

    private static final Logger LOG = LoggerFactory.getLogger(DeploymentHyperfoil.class);
    private static final String DEPLOYMENT_NAME = "controller";
    private static final String ROUTE_NAME = "hyperfoil";

    @Override
    public int priority() {
        return 2; //since the operator is broken
    }

    @Override
    public void create() {
        LOG.debug("Creating Hyperfoil instance via deployment");
        String yamlContent = loadAndPatchYaml();
        try (InputStream is = new ByteArrayInputStream(yamlContent.getBytes(StandardCharsets.UTF_8))) {
            OpenshiftClient.get().load(is).serverSideApply();
        } catch (IOException e) {
            throw new RuntimeException("Failed to apply Hyperfoil deployment YAML", e);
        }
    }

    @Override
    public void undeploy() {
        if (!HyperfoilConfiguration.keepRunning()) {
            String yamlContent = loadAndPatchYaml();
            try (InputStream is = new ByteArrayInputStream(yamlContent.getBytes(StandardCharsets.UTF_8))) {
                OpenshiftClient.get().load(is).delete();
            } catch (IOException e) {
                LOG.warn("Failed to delete Hyperfoil deployment resources", e);
            }
            WaitUtils.waitFor(() -> servicePod() == null, "Waiting until the pod is removed");
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
    public void cleanup() {
    }

    @Override
    public boolean isDeployed() {
        final Deployment deployment = OpenshiftClient.get().apps().deployments().withName(DEPLOYMENT_NAME).get();
        return deployment != null && !deployment.isMarkedForDeletion();
    }

    @Override
    public Predicate<Pod> podSelector() {
        return p -> OpenshiftClient.get().hasLabels(p, Map.of("app", "hyperfoil"));
    }

    @Override
    public String externalHostname() {
        return OpenshiftClient.get().getRoute(ROUTE_NAME).getSpec().getHost();
    }

    @Override
    public String connection() {
        return "http://" + hyperfoilUrl();
    }

    @Override
    public String hyperfoilUrl() {
        return externalHostname();
    }

    @Override
    public int getPortMapping(int port) {
        return port;
    }

    private String loadAndPatchYaml() {
        String url = getConfiguration().getDeploymentYamlUrl();
        LOG.debug("Downloading Hyperfoil deployment YAML from {}", url);
        try (InputStream is = new URL(url).openStream()) {
            String content = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            return content.replace("namespace: hyperfoil", "namespace: " + OpenshiftClient.get().getNamespace());
        } catch (IOException e) {
            throw new RuntimeException("Failed to download Hyperfoil deployment YAML from " + url, e);
        }
    }
}
