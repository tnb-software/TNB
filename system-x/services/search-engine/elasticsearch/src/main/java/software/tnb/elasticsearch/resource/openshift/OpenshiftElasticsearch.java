package software.tnb.elasticsearch.resource.openshift;

import software.tnb.common.deployment.ReusableOpenshiftDeployable;
import software.tnb.common.deployment.WithCustomResource;
import software.tnb.common.deployment.WithExternalHostname;
import software.tnb.common.deployment.WithInClusterHostname;
import software.tnb.common.deployment.WithOperatorHub;
import software.tnb.common.openshift.OpenshiftClient;
import software.tnb.common.utils.WaitUtils;
import software.tnb.elasticsearch.service.Elasticsearch;
import software.tnb.searchengine.common.account.SearchAccount;

import com.google.auto.service.AutoService;

import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import cz.xtf.core.openshift.helpers.ResourceParsers;
import io.fabric8.kubernetes.api.model.GenericKubernetesResource;
import io.fabric8.kubernetes.api.model.GenericKubernetesResourceBuilder;
import io.fabric8.kubernetes.api.model.IntOrString;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.openshift.api.model.RouteBuilder;

@AutoService(Elasticsearch.class)
public class OpenshiftElasticsearch extends Elasticsearch implements ReusableOpenshiftDeployable, WithInClusterHostname, WithExternalHostname
    , WithOperatorHub, WithCustomResource {
    private final String serviceName = clusterName() + "-es-http";
    private final String routeName = clusterName() + "-route";

    @Override
    public void create() {

        createSubscription();

        OpenshiftClient.get().genericKubernetesResources(apiVersion(), kind()).resource(customResource()).create();

        if (OpenshiftClient.get().routes().withName(routeName).get() == null) {
            // @formatter:off
            OpenshiftClient.get().routes().resource(new RouteBuilder()
                .editOrNewMetadata()
                    .withName(routeName)
                .endMetadata()
                .editOrNewSpec()
                    .withNewTo()
                        .withKind("Service")
                        .withName(serviceName)
                        .withWeight(100)
                    .endTo()
                    .editOrNewPort()
                        .withTargetPort(new IntOrString("http"))
                    .endPort()
                .endSpec()
                .build()).create();
            // @formatter:on
        }
    }

    @Override
    public void undeploy() {
        OpenshiftClient.get().routes().withName(routeName).delete();
        OpenshiftClient.get().genericKubernetesResources(apiVersion(), kind()).withName(clusterName()).delete();
        WaitUtils.waitFor(() -> servicePod() == null, "Waiting until the pod is removed");
        deleteSubscription(() -> OpenshiftClient.get().getLabeledPods("control-plane", "elastic-operator").isEmpty());
    }

    @Override
    public void cleanup() {
        validation().getIndices().forEach(i -> validation().deleteIndex(i));
    }

    @Override
    public boolean isDeployed() {
        List<Pod> pods = OpenshiftClient.get().getLabeledPods("control-plane", "elastic-operator");
        return pods.size() == 1 && ResourceParsers.isPodReady(pods.get(0))
            && OpenshiftClient.get().genericKubernetesResources(apiVersion(), kind()).list().getItems().size() == 1;
    }

    @Override
    public Predicate<Pod> podSelector() {
        return p -> OpenshiftClient.get().hasLabels(p, Map.of("elasticsearch.k8s.elastic.co/cluster-name", clusterName()));
    }

    @Override
    public String externalHostname() {
        return OpenshiftClient.get().routes().withName(routeName).get().getSpec().getHost();
    }

    @Override
    public SearchAccount account() {
        if (account == null) {
            super.account();
            account.setPassword(new String(
                Base64.getDecoder().decode(OpenshiftClient.get().getSecret(clusterName() + "-es-elastic-user").getData().get("elastic"))));
        }
        return account;
    }

    // TODO(anyone): If you need this, then the related PV must be cleaned
    @Override
    public void restart() {
        ReusableOpenshiftDeployable.super.restart();
    }

    @Override
    public String url() {
        return externalHostname();
    }

    @Override
    public String host() {
        return inClusterHostname();
    }

    @Override
    public String containerStartRegex() {
        return ".*(\"message\":\\s?\"started[\\s?|\"].*|] started\n$)";
    }

    @Override
    public String inClusterHostname() {
        return OpenshiftClient.get().getClusterHostname(clusterName() + "-es-http");
    }

    @Override
    public String operatorCatalog() {
        return "certified-operators";
    }

    @Override
    public String operatorName() {
        return "elasticsearch-eck-operator-certified";
    }

    @Override
    public String kind() {
        return "Elasticsearch";
    }

    @Override
    public String apiVersion() {
        return "elasticsearch.k8s.elastic.co/v1";
    }

    @Override
    public GenericKubernetesResource customResource() {
        return new GenericKubernetesResourceBuilder()
            .withApiVersion(apiVersion())
            .withKind(kind())
            .withNewMetadata()
            .withName(clusterName())
            .endMetadata()
            .addToAdditionalProperties("spec", Map.of(
                "version", version(),
                "http", Map.of(
                    "tls", Map.of(
                        "selfSignedCertificate", Map.of(
                            "disabled", true
                        )
                    )
                ),
                "nodeSets", List.of(Map.of(
                        "name", "default",
                        "config", Map.of(
                            "node.roles", List.of("master", "data"),
                            "node.attr.attr_name", "attr_value",
                            "node.store.allow_mmap", false
                        ),
                        "podTemplate", Map.of(
                            "spec", Map.of(
                                "containers", List.of(Map.of(
                                    "name", "elasticsearch",
                                    "resources", Map.of(
                                        "requests", Map.of(
                                            "memory", "4Gi",
                                            "cpu", 1
                                        ),
                                        "limits", Map.of(
                                            "memory", "4Gi",
                                            "cpu", 2
                                        )
                                    )
                                ))
                            )
                        ),
                        "count", 1
                    )
                )
            )).build();
    }
}
