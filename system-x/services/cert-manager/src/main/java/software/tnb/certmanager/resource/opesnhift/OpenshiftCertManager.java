package software.tnb.certmanager.resource.opesnhift;

import software.tnb.certmanager.service.CertManager;
import software.tnb.common.deployment.ReusableOpenshiftDeployable;
import software.tnb.common.deployment.WithCustomResource;
import software.tnb.common.deployment.WithOperatorHub;
import software.tnb.common.openshift.OpenshiftClient;
import software.tnb.common.utils.WaitUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.auto.service.AutoService;

import java.util.Map;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;

import cz.xtf.core.openshift.helpers.ResourceParsers;
import io.fabric8.kubernetes.api.model.GenericKubernetesResource;
import io.fabric8.kubernetes.api.model.Pod;

@AutoService(CertManager.class)
public class OpenshiftCertManager extends CertManager implements ReusableOpenshiftDeployable, WithOperatorHub, WithCustomResource {

    private static final Logger LOG = LoggerFactory.getLogger(OpenshiftCertManager.class);

    @Override
    public void undeploy() {
        OpenshiftClient.get().genericKubernetesResources(apiVersion(), kind()).delete();
        WaitUtils.waitFor(() -> servicePod() == null, "Waiting until the pod is removed");
        deleteSubscription(() -> OpenshiftClient.get().getLabeledPods("name", "cert-manager-operator").isEmpty());
    }

    /**
     * Open all resources needed after the service is deployed - initialize clients and stuff.
     */
    @Override
    public void openResources() {
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
        LOG.debug("Creating Cert Manager instance");
        // Create subscription
        createSubscription();

        WaitUtils.waitFor(() -> !OpenshiftClient.get()
                .pods().inNamespace(targetNamespace())
                .withLabel("name", "cert-manager-operator").list().getItems().isEmpty()
            , "Wait for the operator has been installed");

    }

    @Override
    public boolean isReady() {
        return ResourceParsers.isPodReady(OpenshiftClient.get()
                .pods().inNamespace(targetNamespace())
                .withLabel("name", "cert-manager-operator").list().getItems().get(0));
    }

    @Override
    public boolean isDeployed() {
        return isReady();
    }

    @Override
    public Predicate<Pod> podSelector() {
        return p -> OpenshiftClient.get().hasLabels(p, Map.of("name", "cert-manager-operator"));
    }

    @Override
    public void cleanup() {
        //do nothing
    }

    @Override
    public String operatorChannel() {
        return "stable-v1";
    }

    @Override
    public String operatorName() {
        return "openshift-cert-manager-operator";
    }

    @Override
    public String kind() {
        return "CertManager";
    }

    @Override
    public String apiVersion() {
        return "operator.openshift.io/v1alpha1";
    }

    @Override
    public GenericKubernetesResource customResource() {
        return null;
    }

    @Override
    public String targetNamespace() {
        return "cert-manager-operator";
    }

    @Override
    public String subscriptionName() {
        return "openshift-cert-manager-operator";
    }

    @Override
    public void deleteSubscription(BooleanSupplier waitCondition) {
        //want to keep the operator
    }
}
