package org.jboss.fuse.tnb.hyperfoil.service.openshift;

import org.jboss.fuse.tnb.common.deployment.ReusableOpenshiftDeployable;
import org.jboss.fuse.tnb.common.deployment.WithExternalHostname;
import org.jboss.fuse.tnb.common.openshift.OpenshiftClient;
import org.jboss.fuse.tnb.common.utils.WaitUtils;
import org.jboss.fuse.tnb.hyperfoil.service.Hyperfoil;
import org.jboss.fuse.tnb.hyperfoil.validation.generated.ApiException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.auto.service.AutoService;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.dsl.base.CustomResourceDefinitionContext;
import io.fabric8.openshift.client.internal.readiness.OpenShiftReadiness;

@AutoService(Hyperfoil.class)
public class OpenshiftHyperfoil extends Hyperfoil implements ReusableOpenshiftDeployable, WithExternalHostname {

    private static final Logger LOG = LoggerFactory.getLogger(OpenshiftHyperfoil.class);

    private static final String CHANNEL = "alpha";
    private static final String OPERATOR_NAME = "hyperfoil-bundle";
    private static final String SOURCE = "community-operators";
    private static final String SUBSCRIPTION_NAME = "tnb-hyperfoil";
    private static final String SUBSCRIPTION_NAMESPACE = "openshift-marketplace";

    private static final String APP_NAME = "hyperfoil-" + OpenshiftClient.get().getNamespace();

    private static final CustomResourceDefinitionContext HYPERFOIL_CTX = new CustomResourceDefinitionContext.Builder()
        .withName("Hyperfoil")
        .withGroup("hyperfoil.io")
        .withVersion("v1alpha2")
        .withPlural("hyperfoils")
        .withScope("Namespaced")
        .build();

    @Override
    public void undeploy() {
        try {
            OpenshiftClient.get().customResource(HYPERFOIL_CTX).delete(OpenshiftClient.get().getNamespace(), APP_NAME, true);
            WaitUtils.waitFor(() -> OpenshiftClient.get().getLabeledPods(Map.of("kind", HYPERFOIL_CTX.getName(), "app", APP_NAME))
                .isEmpty(), "waiting for hyperfoil pods are deleted");
            OpenshiftClient.get().deleteSubscription(SUBSCRIPTION_NAME);
        } catch (IOException e) {
            LOG.error("error on Cryostat deletetion", e);
            throw new RuntimeException(e);
        }

    }

    @Override
    public void openResources() {
        WaitUtils.waitFor(() -> {
            try {
                return getValidation().getDefaultApi().openApiWithHttpInfo().getStatusCode() == 200;
            } catch (ApiException e) {
                return false;
            }
        }, "wait for responding api endpoint");
    }

    @Override
    public void closeResources() {
    }

    @Override
    public void create() {
        if (isReady()) {
            LOG.debug("Hyperfoil operator already installed");
        } else {
            LOG.debug("Creating Hyperfoil instance");
            // Create subscription
            OpenshiftClient.get()
                .createSubscription(CHANNEL, OPERATOR_NAME, SOURCE, SUBSCRIPTION_NAME, SUBSCRIPTION_NAMESPACE);
            OpenshiftClient.get().waitForInstallPlanToComplete(SUBSCRIPTION_NAME);

            try {
                OpenshiftClient.get().customResource(HYPERFOIL_CTX).create(getHyperfoilDefinition());
            } catch (IOException e) {
                LOG.error("error on Hyperfoil creation", e);
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public boolean isReady() {
        List<Pod> pods = OpenshiftClient.get().getLabeledPods("app", APP_NAME);
        return pods.size() > 0 && pods.stream().allMatch(OpenShiftReadiness::isPodReady);
    }

    @Override
    public boolean isDeployed() {
        return isReady();
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

    private Map<String, Object> getHyperfoilDefinition() {
        Map<String, Object> metadata = Map.of("name", APP_NAME
            , "namespace", OpenshiftClient.get().getNamespace());
        Map<String, Object> spec = Map.of("agentDeployTimeout", 120000
            , "version", "latest"
            , "route", Map.of("host", OpenshiftClient.get().generateHostname("hyperfoil")) //"hyperfoil.apps.mycloud.example.com"
        );
        return Map.of("kind", HYPERFOIL_CTX.getName()
            , "apiVersion", String.format("%s/%s", HYPERFOIL_CTX.getGroup(), HYPERFOIL_CTX.getVersion())
            , "metadata", metadata
            , "spec", spec);
    }

    @Override
    public void cleanup() {
        //all benchmarks should be managed by the tests
    }

    @Override
    public String externalHostname() {
        return OpenshiftClient.get().getRoute(APP_NAME).getSpec().getHost();
    }
}
