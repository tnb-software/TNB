package software.tnb.knative.service;

import static org.junit.jupiter.api.Assertions.fail;

import software.tnb.knative.validation.KnativeValidation;
import software.tnb.common.deployment.ReusableOpenshiftDeployable;
import software.tnb.common.openshift.OpenshiftClient;
import software.tnb.common.service.Service;
import software.tnb.common.utils.WaitUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.auto.service.AutoService;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.xtf.core.openshift.helpers.ResourceParsers;
import io.fabric8.knative.client.KnativeClient;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.dsl.base.CustomResourceDefinitionContext;

@AutoService(Knative.class)
public class Knative implements Service, ReusableOpenshiftDeployable {


    public static final String OPENSHIFT_SERVERLESS_NAMESPACE = "openshift.serverless.namespace";

    private static final String CHANNEL = "stable";
    private static final String OPERATOR_NAME = "serverless-operator";
    private static final String SOURCE = "redhat-operators";
    private static final String SUBSCRIPTION_NAME = "tnb-knative";
    private static final String SUBSCRIPTION_NAMESPACE = "openshift-marketplace";
    private static final String TARGET_NAMESPACE = System.getProperty(OPENSHIFT_SERVERLESS_NAMESPACE, "openshift-serverless");

    private static final String EVENTING_NAMESPACE = "knative-eventing";
    private static final String EVENTING_CR_NAME = "knative-eventing";

    private static final String SERVING_NAMESPACE = "knative-serving";
    private static final String SERVING_CR_NAME = "knative-serving";

    private static final String CRD_GROUP = "operator.knative.dev";
    private static final String CRD_VERSION = "v1alpha1";

    private KnativeValidation validation;

    private boolean installedByDefault = true; //serverless operator installed by default shouldn't be removed

    private static final Logger LOG = LoggerFactory.getLogger(Knative.class);

    private static final CustomResourceDefinitionContext EVENTING_CTX = new CustomResourceDefinitionContext.Builder()
        .withName("KnativeEventing")
        .withGroup(CRD_GROUP)
        .withVersion(CRD_VERSION)
        .withPlural("knativeeventings")
        .withScope("Namespaced")
        .build();

    private static final CustomResourceDefinitionContext SERVING_CTX = new CustomResourceDefinitionContext.Builder()
        .withName("KnativeServing")
        .withGroup(CRD_GROUP)
        .withVersion(CRD_VERSION)
        .withPlural("knativeservings")
        .withScope("Namespaced")
        .build();

    @Override
    public void undeploy() {
        // Delete created K-Native resources
        validation().deleteCreatedResources();

        if (!installedByDefault) {
            LOG.debug("Undeploying serverless operator");
            // Remove eventing / serving CR
            OpenshiftClient.get().customResource(EVENTING_CTX).delete(EVENTING_NAMESPACE);
            OpenshiftClient.get().customResource(SERVING_CTX).delete(SERVING_NAMESPACE);

            // Wait until the pods are terminated
            WaitUtils.waitFor(() -> OpenshiftClient.get().pods().inNamespace(EVENTING_NAMESPACE).list().getItems().size() == 0,
                "Waiting until all eventing pods are terminated");
            WaitUtils.waitFor(
                () -> OpenshiftClient.get().pods().inNamespace(SERVING_NAMESPACE).list().getItems().size() == 0 || OpenshiftClient.get().pods()
                    .inNamespace(SERVING_NAMESPACE).list().getItems().stream()
                    .allMatch(p -> "succeeded".equalsIgnoreCase(p.getStatus().getPhase())),
                "Waiting until all serving pods are terminated");

            // Remove serverless operator subscription
            OpenshiftClient.get().deleteSubscription(SUBSCRIPTION_NAME, TARGET_NAMESPACE);
            WaitUtils.waitFor(() -> OpenshiftClient.get().pods().inNamespace(TARGET_NAMESPACE).list().getItems().size() == 0,
                "Waiting until all serverless operator pods are terminated");
        } else {
            LOG.debug("Not undeploying default serverless operator");
        }
    }

    @Override
    public void openResources() {
        // no-op
    }

    @Override
    public void closeResources() {
        // no-op
    }

    @Override
    public void create() {
        LOG.debug("Creating serverless operator");
        installedByDefault = false;

        OpenshiftClient.get().createNamespace(TARGET_NAMESPACE);
        // Create subscription for serverless operator
        OpenshiftClient.get().createSubscription(CHANNEL, OPERATOR_NAME, SOURCE, SUBSCRIPTION_NAME, SUBSCRIPTION_NAMESPACE, TARGET_NAMESPACE,
            true);

        // The serverless operator also creates eventing and serving namespaces if they are not present
        WaitUtils.waitFor(() -> OpenshiftClient.get().namespaces().withName(EVENTING_NAMESPACE).get() != null,
            "Waiting until the eventing namespace is created");
        WaitUtils.waitFor(() -> OpenshiftClient.get().namespaces().withName(SERVING_NAMESPACE).get() != null,
            "Waiting until the serving namespace is created");

        // Create eventing and serving custom resource
        try {
            OpenshiftClient.get().customResource(EVENTING_CTX)
                .createOrReplace(EVENTING_NAMESPACE, createCr("KnativeEventing", EVENTING_CR_NAME, EVENTING_NAMESPACE));
            OpenshiftClient.get().customResource(SERVING_CTX)
                .createOrReplace(SERVING_NAMESPACE, createCr("KnativeServing", SERVING_CR_NAME, SERVING_NAMESPACE));
        } catch (IOException e) {
            fail("Unable to create custom resources: ", e);
        }
    }

    @Override
    public boolean isReady() {
        return OpenshiftClient.get().apps().deployments().inNamespace(TARGET_NAMESPACE).list().getItems().stream()
            .allMatch(d -> d.getSpec().getReplicas() == 0 || d.getSpec().getReplicas().equals(d.getStatus().getAvailableReplicas()))
            && OpenshiftClient.get().apps().deployments().inNamespace(EVENTING_NAMESPACE).list().getItems().stream()
            .allMatch(d -> d.getSpec().getReplicas() == 0 || d.getSpec().getReplicas().equals(d.getStatus().getAvailableReplicas()))
            && OpenshiftClient.get().apps().deployments().inNamespace(SERVING_NAMESPACE).list().getItems().stream()
            .allMatch(d -> d.getSpec().getReplicas() == 0 || d.getSpec().getReplicas().equals(d.getStatus().getAvailableReplicas()));
    }

    @Override
    public boolean isDeployed() {
        List<Pod> pods = OpenshiftClient.get().pods().inNamespace(TARGET_NAMESPACE).withLabel("name", "knative-operator").list().getItems();
        return pods.size() == 1 && ResourceParsers.isPodReady(pods.get(0))
            && ((List) OpenshiftClient.get().customResource(EVENTING_CTX).list(EVENTING_NAMESPACE).get("items")).size() == 1
            && ((List) OpenshiftClient.get().customResource(SERVING_CTX).list(SERVING_NAMESPACE).get("items")).size() == 1;
    }

    protected KnativeClient client() {
        if (OpenshiftClient.get().isAdaptable(KnativeClient.class)) {
            return OpenshiftClient.get().adapt(KnativeClient.class);
        }
        throw new IllegalArgumentException("Unable to adapt OpenshiftClient to KnativeClient");
    }

    public KnativeValidation validation() {
        if (validation == null) {
            validation = new KnativeValidation(client());
        }
        return validation;
    }

    private Map<String, Object> createCr(String kind, String name, String namespace) {
        // Too short to bother with CR classes
        Map<String, Object> cr = new HashMap<>();
        cr.put("apiVersion", CRD_GROUP + "/" + CRD_VERSION);
        cr.put("kind", kind);
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("name", name);
        metadata.put("namespace", namespace);
        cr.put("metadata", metadata);
        return cr;
    }

    @Override
    public void cleanup() {
        if (validation != null) {
            validation.deleteCreatedResources();
        }
    }
}
