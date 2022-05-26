package org.jboss.fuse.tnb.elasticsearch.resource.openshift;

import static org.junit.jupiter.api.Assertions.fail;

import org.jboss.fuse.tnb.common.account.Accounts;
import org.jboss.fuse.tnb.common.config.OpenshiftConfiguration;
import org.jboss.fuse.tnb.common.deployment.ReusableOpenshiftDeployable;
import org.jboss.fuse.tnb.common.deployment.WithExternalHostname;
import org.jboss.fuse.tnb.common.deployment.WithInClusterHostname;
import org.jboss.fuse.tnb.common.openshift.OpenshiftClient;
import org.jboss.fuse.tnb.elasticsearch.account.ElasticsearchAccount;
import org.jboss.fuse.tnb.elasticsearch.service.Elasticsearch;

import org.apache.commons.io.IOUtils;

import com.google.auto.service.AutoService;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

import cz.xtf.core.openshift.OpenShiftWaiters;
import cz.xtf.core.openshift.helpers.ResourceParsers;
import io.fabric8.kubernetes.api.model.IntOrString;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.dsl.base.CustomResourceDefinitionContext;
import io.fabric8.openshift.api.model.RouteBuilder;

@AutoService(Elasticsearch.class)
public class OpenshiftElasticsearch extends Elasticsearch implements ReusableOpenshiftDeployable, WithInClusterHostname, WithExternalHostname {
    private static final String CHANNEL = "stable";
    private static final String OPERATOR_NAME = "elasticsearch-eck-operator-certified";
    private static final String CATALOGSOURCE_NAME = "certified-operators";
    private static final String SUBSCRIPTION_NAME = "tnb-elasticsearch";

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
        OpenshiftClient.get().createSubscription(CHANNEL, OPERATOR_NAME, CATALOGSOURCE_NAME, SUBSCRIPTION_NAME);

        OpenShiftWaiters.get(OpenshiftClient.get(), () -> false).areExactlyNPodsReady(1, "control-plane", "elastic-operator")
            .timeout(300_000).waitFor();

        try (InputStream is = this.getClass().getResourceAsStream("/cr.yaml")) {
            String content = IOUtils.toString(is, StandardCharsets.UTF_8)
                .replace("$VERSION$", version()).replace("$NAME$", clusterName());
            OpenshiftClient.get().customResource(ELASTICSEARCH_CTX).createOrReplace(OpenshiftConfiguration.openshiftNamespace(), content);
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
        OpenshiftClient.get().customResource(ELASTICSEARCH_CTX).delete(OpenshiftConfiguration.openshiftNamespace());
        OpenShiftWaiters.get(OpenshiftClient.get(), () -> false)
            .areExactlyNPodsRunning(0, "elasticsearch.k8s.elastic.co/cluster-name", clusterName())
            .timeout(120_000).waitFor();
        OpenshiftClient.get().deleteSubscription(SUBSCRIPTION_NAME);
        OpenShiftWaiters.get(OpenshiftClient.get(), () -> false).areExactlyNPodsRunning(0, "control-plane", "elastic-operator")
            .timeout(120_000).waitFor();
    }

    @Override
    public void cleanup() {
        validation().getIndices().forEach(i -> validation().deleteIndex(i));
    }

    @Override
    public boolean isReady() {
        try {
            return ResourceParsers.isPodReady(OpenshiftClient.get().getAnyPod("elasticsearch.k8s.elastic.co/cluster-name", clusterName()));
        } catch (Exception ignored) {
            return false;
        }
    }

    @Override
    public boolean isDeployed() {
        List<Pod> pods = OpenshiftClient.get().pods().withLabel("control-plane", "elastic-operator").list().getItems();
        return pods.size() == 1 && ResourceParsers.isPodReady(pods.get(0))
            && ((List) OpenshiftClient.get().customResource(ELASTICSEARCH_CTX).list().get("items")).size() == 1;
    }

    @Override
    public String externalHostname() {
        return OpenshiftClient.get().routes().withName(routeName).get().getSpec().getHost();
    }

    @Override
    public ElasticsearchAccount account() {
        if (account == null) {
            account = Accounts.get(ElasticsearchAccount.class);
            account.setPassword(new String(
                Base64.getDecoder().decode(OpenshiftClient.get().getSecret(clusterName() + "-es-elastic-user").getData().get("elastic"))));
        }
        return account;
    }

    @Override
    protected String clientHost() {
        return externalHostname();
    }

    @Override
    public String host() {
        return inClusterHostname();
    }
}
