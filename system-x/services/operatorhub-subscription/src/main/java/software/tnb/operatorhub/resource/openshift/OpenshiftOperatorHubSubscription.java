package software.tnb.operatorhub.resource.openshift;

import software.tnb.common.deployment.ReusableOpenshiftDeployable;
import software.tnb.common.deployment.WithOperatorHub;
import software.tnb.common.openshift.OpenshiftClient;
import software.tnb.operatorhub.service.OperatorHubSubscription;

import com.google.auto.service.AutoService;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import io.fabric8.kubernetes.api.model.EnvVar;
import io.fabric8.kubernetes.api.model.Pod;

@AutoService(OperatorHubSubscription.class)
public class OpenshiftOperatorHubSubscription extends OperatorHubSubscription implements ReusableOpenshiftDeployable, WithOperatorHub {

    @Override
    protected void defaultConfiguration() {
        getConfiguration().operatorCatalog(WithOperatorHub.super.operatorCatalog())
            .operatorCatalogNamespace(WithOperatorHub.super.operatorCatalogNamespace())
            .operatorChannel(WithOperatorHub.super.operatorChannel())
            .subscriptionName(WithOperatorHub.super.subscriptionName())
            .targetNamespace(WithOperatorHub.super.targetNamespace())
            .clusterWide(WithOperatorHub.super.clusterWide())
            .operatorEnvVariables(Optional.ofNullable(WithOperatorHub.super.getOperatorEnvVariables())
                .map(this::toMap).orElse(null))
            .podSelector(Map.of());
    }

    @Override
    public void cleanup() {

    }

    @Override
    public void create() {
        createSubscription();
    }

    @Override
    public boolean isReady() {
        return !isPodSelectorSet() || ReusableOpenshiftDeployable.super.isReady();
    }

    @Override
    public boolean isDeployed() {
        return OpenshiftClient.get().operatorHub().subscriptions().inNamespace(targetNamespace()).list()
            .getItems().stream().anyMatch(subscription -> subscriptionName().equals(subscription.getMetadata().getName()));
    }

    @Override
    public Predicate<Pod> podSelector() {
        return pod -> isPodSelectorSet() && OpenshiftClient.get().hasLabels(pod, getConfiguration().getPodSelector());
    }

    @Override
    public void undeploy() {
        deleteSubscription(() -> !isPodSelectorSet()
            || OpenshiftClient.get().getLabeledPods(getConfiguration().getPodSelector()).isEmpty());
    }

    @Override
    public void openResources() {

    }

    @Override
    public void closeResources() {

    }

    @Override
    public String operatorName() {
        return getConfiguration().getOperatorName();
    }

    @Override
    public String operatorCatalog() {
        return getConfiguration().getOperatorCatalog();
    }

    @Override
    public String operatorCatalogNamespace() {
        return getConfiguration().getOperatorCatalogNamespace();
    }

    @Override
    public String operatorChannel() {
        return getConfiguration().getOperatorChannel();
    }

    @Override
    public String subscriptionName() {
        return getConfiguration().getSubscriptionName();
    }

    @Override
    public String targetNamespace() {
        return getConfiguration().getTargetNamespace();
    }

    @Override
    public boolean clusterWide() {
        return WithOperatorHub.super.clusterWide();
    }

    @Override
    public List<EnvVar> getOperatorEnvVariables() {
        return toList(getConfiguration().getOperatorEnvVariables());
    }

    private boolean isPodSelectorSet() {
        return getConfiguration().getPodSelector() != null && !getConfiguration().getPodSelector().isEmpty();
    }

    private Map<String, String> toMap(List<EnvVar> envVars) {
        return Optional.ofNullable(envVars)
            .map(vars -> vars.stream().collect(Collectors.toMap(EnvVar::getName,
                EnvVar::getValue)))
            .orElse(null);
    }

    private List<EnvVar> toList(Map<String, String> envMap) {
        return Optional.ofNullable(envMap)
            .map(em -> em.entrySet().stream()
                .map(entry -> new EnvVar(entry.getKey(), entry.getValue(), null))
                .collect(Collectors.toList()))
            .orElse(null);
    }
}
