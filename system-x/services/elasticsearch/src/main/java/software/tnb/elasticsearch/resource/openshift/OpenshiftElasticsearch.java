package software.tnb.elasticsearch.resource.openshift;

import static org.junit.jupiter.api.Assertions.fail;

import software.tnb.common.account.AccountFactory;
import software.tnb.common.deployment.ReusableOpenshiftDeployable;
import software.tnb.common.deployment.WithExternalHostname;
import software.tnb.common.deployment.WithInClusterHostname;
import software.tnb.common.deployment.WithOperatorHub;
import software.tnb.common.openshift.OpenshiftClient;
import software.tnb.common.utils.WaitUtils;
import software.tnb.elasticsearch.account.ElasticsearchAccount;
import software.tnb.elasticsearch.service.Elasticsearch;

import org.apache.commons.io.IOUtils;

import com.google.auto.service.AutoService;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import cz.xtf.core.openshift.helpers.ResourceParsers;
import io.fabric8.kubernetes.api.model.IntOrString;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.dsl.base.CustomResourceDefinitionContext;
import io.fabric8.openshift.api.model.RouteBuilder;

@AutoService(Elasticsearch.class)
public class OpenshiftElasticsearch extends Elasticsearch implements ReusableOpenshiftDeployable, WithInClusterHostname, WithExternalHostname
    , WithOperatorHub {

    private static final CustomResourceDefinitionContext ELASTICSEARCH_CTX = new CustomResourceDefinitionContext.Builder()
        .withGroup("elasticsearch.k8s.elastic.co")
        .withVersion("v1")
        .withKind("Elasticsearch")
        .withScope("Namespaced")
        .withPlural("elasticsearches")
        .build();

    private final String serviceName = clusterName() + "-es-http";
    private final String routeName = clusterName() + "-route";

    @Override
    public void create() {
        createSubscription();

        try (InputStream is = this.getClass().getResourceAsStream("/cr.yaml")) {
            String content = IOUtils.toString(is, StandardCharsets.UTF_8)
                .replace("$VERSION$", version()).replace("$NAME$", clusterName());
            OpenshiftClient.get().customResource(ELASTICSEARCH_CTX).inNamespace(OpenshiftClient.get().getNamespace()).createOrReplace(content);
        } catch (IOException e) {
            fail("Unable to read elasticsearch CR: ", e);
        }

        // @formatter:off
        OpenshiftClient.get().routes().createOrReplace(new RouteBuilder()
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
            .build());
        // @formatter:on
    }

    @Override
    public void undeploy() {
        OpenshiftClient.get().routes().withName(routeName).delete();
        OpenshiftClient.get().customResource(ELASTICSEARCH_CTX).inNamespace(OpenshiftClient.get().getNamespace()).delete();
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
            && ((List) OpenshiftClient.get().customResource(ELASTICSEARCH_CTX).list().get("items")).size() == 1;
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
    public ElasticsearchAccount account() {
        if (account == null) {
            account = AccountFactory.create(ElasticsearchAccount.class);
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
    protected String clientHost() {
        return externalHostname();
    }

    @Override
    public String host() {
        return inClusterHostname() + ":" + PORT;
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
}
