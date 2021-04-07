package org.jboss.fuse.tnb.product.ck;

import org.jboss.fuse.tnb.common.config.OpenshiftConfiguration;
import org.jboss.fuse.tnb.common.deployment.OpenshiftDeployable;
import org.jboss.fuse.tnb.common.openshift.OpenshiftClient;
import org.jboss.fuse.tnb.common.utils.WaitUtils;
import org.jboss.fuse.tnb.product.Product;
import org.jboss.fuse.tnb.product.ck.generated.DoneableIntegration;
import org.jboss.fuse.tnb.product.ck.generated.Integration;
import org.jboss.fuse.tnb.product.ck.generated.IntegrationList;
import org.jboss.fuse.tnb.product.ck.generated.IntegrationSpec;
import org.jboss.fuse.tnb.product.ck.generated.IntegrationStatus;
import org.jboss.fuse.tnb.product.ck.generated.Source;
import org.jboss.fuse.tnb.product.util.RouteBuilderGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.CodeBlock;

import java.util.Collections;
import java.util.function.BooleanSupplier;

import cz.xtf.core.openshift.helpers.ResourceFunctions;
import io.fabric8.kubernetes.api.model.DeletionPropagation;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.client.dsl.NonNamespaceOperation;
import io.fabric8.kubernetes.client.dsl.Resource;
import io.fabric8.kubernetes.client.dsl.base.CustomResourceDefinitionContext;

@AutoService(Product.class)
public class OpenshiftCamelK extends Product implements OpenshiftDeployable {
    private static final Logger LOG = LoggerFactory.getLogger(OpenshiftCamelK.class);
    private static final CustomResourceDefinitionContext INTEGRATIONS_CONTEXT = new CustomResourceDefinitionContext.Builder()
        .withGroup("camel.apache.org")
        .withPlural("integrations")
        .withScope("Namespaced")
        .withVersion("v1")
        .build();
    private NonNamespaceOperation<Integration, IntegrationList, DoneableIntegration, Resource<Integration, DoneableIntegration>> client;

    @Override
    public void create() {
        LOG.info("Deploying Camel-K");
        OpenshiftClient.createSubscription("stable", "camel-k", "community-operators", "test-camel-k");
        OpenshiftClient.waitForCompletion("test-camel-k");
        client = OpenshiftClient.get().customResources(INTEGRATIONS_CONTEXT, Integration.class, IntegrationList.class, DoneableIntegration.class)
            .inNamespace(OpenshiftConfiguration.openshiftNamespace());
    }

    @Override
    public void undeploy() {
        OpenshiftClient.deleteSubscription("test-camel-k");
    }

    public void deployIntegration(String name, CodeBlock routeDefinition, String... camelComponents) {
        LOG.info("Deploying integration {}", name);
        client.create(createIntegrationResource(name, RouteBuilderGenerator.asString(routeDefinition)));
        waitForIntegration(name);
    }

    @Override
    public void waitForIntegration(String name) {
        BooleanSupplier success = () -> {
            Integration i = client.withName(name).get();
            try {
                return "running".equalsIgnoreCase(i.getStatus().getPhase());
            } catch (Exception ignored) {
                return false;
            }
        };
        BooleanSupplier fail = () -> {
            Integration i = client.withName(name).get();
            try {
                return "error".equalsIgnoreCase(i.getStatus().getPhase());
            } catch (Exception ignored) {
                return false;
            }
        };
        WaitUtils.waitFor(success, fail, 5000L, String.format("Waiting until the integration %s is built", name));

        WaitUtils.waitFor(
            () -> ResourceFunctions.areExactlyNPodsReady(1).apply(OpenshiftClient.get().getLabeledPods("camel.apache.org/integration", name)),
            24,
            5000L,
            String.format("Waiting until the pod with label %s is ready", "camel.apache.org/integration=" + name));
    }

    @Override
    public void undeployIntegration() {
        for (Integration item : client.list().getItems()) {
            LOG.info("Undeploying integration {}", item.getMetadata().getName());
            client.withName(item.getMetadata().getName()).withPropagationPolicy(DeletionPropagation.BACKGROUND).delete();
        }
    }

    /**
     * Creates an Integration object.
     * @param name name of the object
     * @param routeDefinition camel route definition
     * @return integration instance
     */
    private Integration createIntegrationResource(String name, String routeDefinition) {
        ObjectMeta metadata = new ObjectMeta();
        metadata.setName(name);
        IntegrationSpec spec = new IntegrationSpec();
        Source source = new Source();
        source.setName("MyRouteBuilder.java");
        source.setContent("// camel-k: language=java\n" + routeDefinition);
        spec.setSources(Collections.singletonList(source));
        return new Integration("camel.apache.org/v1", "Integration", metadata, spec, new IntegrationStatus());
    }

    @Override
    public boolean isReady() {
        return ResourceFunctions.areExactlyNPodsReady(1).apply(OpenshiftClient.get().getLabeledPods("name", "camel-k-operator"));
    }

    @Override
    public boolean isDeployed() {
        return OpenshiftClient.get().getLabeledPods("name", "camel-k-operator").size() != 0;
    }
}
