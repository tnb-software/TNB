package software.tnb.tekton.service;

import software.tnb.common.account.NoAccount;
import software.tnb.common.deployment.OpenshiftDeployable;
import software.tnb.common.deployment.WithOperatorHub;
import software.tnb.common.openshift.OpenshiftClient;
import software.tnb.common.service.Service;
import software.tnb.common.utils.WaitUtils;
import software.tnb.tekton.validation.TektonValidation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.auto.service.AutoService;

import java.util.List;
import java.util.function.Predicate;

import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.client.dsl.base.CustomResourceDefinitionContext;
import io.fabric8.tekton.client.TektonClient;

@AutoService(Tekton.class)
public class Tekton extends Service<NoAccount, TektonClient, TektonValidation> implements OpenshiftDeployable, WithOperatorHub {
    private static final Logger LOG = LoggerFactory.getLogger(Tekton.class);

    public Tekton() {

    }

    protected TektonClient client() {
        if (OpenshiftClient.get().isAdaptable(TektonClient.class)) {
            return OpenshiftClient.get().adapt(TektonClient.class);
        }
        throw new IllegalArgumentException("Unable to adapt OpenshiftClient to TektonClient");
    }

    @Override
    public TektonValidation validation() {
        if (validation == null) {
            validation = new TektonValidation(client());
        }
        return validation;
    }

    public void create() {
        LOG.debug("Creating Tekton operator");
        createSubscription();
    }

    public void undeploy() {
        LOG.debug("Undeploy Tekton operator");
        CustomResourceDefinitionContext crd = new CustomResourceDefinitionContext.Builder()
            .withName("tektonconfigs.operator.tekton.dev")
            .withGroup("operator.tekton.dev")
            .withVersion("v1alpha1")
            .withPlural("tektonconfigs")
            .withScope("Cluster")
            .build();

        OpenshiftClient.get().genericKubernetesResources(crd).inNamespace("openshift-pipelines").delete();
        WaitUtils.waitFor(() ->
                OpenshiftClient.get().namespaces().withName("openshift-pipelines").get() == null,
            "Waiting until the openshift-pipelines namespace is removed");
        deleteSubscription(() -> OpenshiftClient.get().getLabeledPods("name", "openshift-pipelines-operator")
            .stream().noneMatch(p -> p.getMetadata().getName().contains("openshift-pipelines-operator")));
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
    public boolean isReady() {
        if (OpenshiftClient.get().namespaces().withName("openshift-pipelines").isReady()) {
            List<Deployment> deploys = OpenshiftClient.get().apps().deployments()
                .inNamespace("openshift-pipelines")
                .withLabel("operator.tekton.dev/operand-name")
                .list().getItems();

            return deploys.size() >= 12
                && deploys.stream().allMatch(d -> d.getStatus() != null)
                && deploys.stream().allMatch(d -> Integer.valueOf(1).equals(d.getStatus().getReadyReplicas()));
        } else {
            return false;
        }
    }

    public boolean isDeployed() {
        return !OpenshiftClient.get().pods().inNamespace(targetNamespace()).withLabel("name", "tekton-operator-webhook").list().getItems().isEmpty()
            && !OpenshiftClient.get().pods().inNamespace(targetNamespace()).withLabel("name", "openshift-pipelines-operator").list().getItems()
            .isEmpty();
    }

    @Override
    public Predicate<Pod> podSelector() {
        return null;
    }

    public String targetNamespace() {
        return "openshift-operators";
    }

    public String operatorName() {
        return "openshift-pipelines-operator-rh";
    }

    public String operatorChannel() {
        return "latest";
    }

    public boolean clusterWide() {
        return true;
    }
}
