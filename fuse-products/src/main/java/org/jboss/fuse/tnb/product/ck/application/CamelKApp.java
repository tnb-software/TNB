package org.jboss.fuse.tnb.product.ck.application;

import org.jboss.fuse.tnb.common.config.OpenshiftConfiguration;
import org.jboss.fuse.tnb.common.openshift.OpenshiftClient;
import org.jboss.fuse.tnb.common.utils.MapUtils;
import org.jboss.fuse.tnb.product.application.App;
import org.jboss.fuse.tnb.product.ck.generated.Configuration;
import org.jboss.fuse.tnb.product.ck.generated.DoneableIntegration;
import org.jboss.fuse.tnb.product.ck.generated.Integration;
import org.jboss.fuse.tnb.product.ck.generated.IntegrationList;
import org.jboss.fuse.tnb.product.ck.generated.IntegrationSpec;
import org.jboss.fuse.tnb.product.ck.generated.IntegrationStatus;
import org.jboss.fuse.tnb.product.ck.generated.Source;
import org.jboss.fuse.tnb.product.integration.IntegrationBuilder;
import org.jboss.fuse.tnb.product.integration.IntegrationData;
import org.jboss.fuse.tnb.product.integration.IntegrationGenerator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Map;

import cz.xtf.core.openshift.helpers.ResourceFunctions;
import io.fabric8.kubernetes.api.model.DeletionPropagation;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.client.dsl.NonNamespaceOperation;
import io.fabric8.kubernetes.client.dsl.Resource;
import io.fabric8.kubernetes.client.dsl.base.CustomResourceDefinitionContext;

public class CamelKApp extends App {
    private static final Logger LOG = LoggerFactory.getLogger(CamelKApp.class);
    private static final CustomResourceDefinitionContext INTEGRATIONS_CONTEXT = new CustomResourceDefinitionContext.Builder()
        .withGroup("camel.apache.org")
        .withPlural("integrations")
        .withScope("Namespaced")
        .withVersion("v1")
        .build();

    private final NonNamespaceOperation<Integration, IntegrationList, DoneableIntegration, Resource<Integration, DoneableIntegration>> client =
        OpenshiftClient.get().customResources(INTEGRATIONS_CONTEXT, Integration.class, IntegrationList.class, DoneableIntegration.class)
            .inNamespace(OpenshiftConfiguration.openshiftNamespace());
    private final IntegrationData integrationData;

    public CamelKApp(IntegrationBuilder integrationBuilder) {
        super(integrationBuilder.getIntegrationName());
        integrationData = IntegrationGenerator.toIntegrationData(integrationBuilder);

        // If there are any properties set, create a config map with the same map as the integration
        if (!integrationData.getProperties().isEmpty()) {
            OpenshiftClient.createConfigMap(name, Map.of("application.properties", MapUtils.propertiesToString(integrationData.getProperties())));
        }
    }

    @Override
    public void start() {
        client.create(createIntegrationResource(name, integrationData));
    }

    @Override
    public void stop() {
        LOG.info("Removing integration {}", name);
        client.withName(name).withPropagationPolicy(DeletionPropagation.BACKGROUND).delete();
    }

    @Override
    public boolean isReady() {
        try {
            return "running".equalsIgnoreCase(client.withName(name).get().getStatus().getPhase())
                && ResourceFunctions.areExactlyNPodsReady(1).apply(OpenshiftClient.get().getLabeledPods("camel.apache.org/integration", name))
                && isCamelStarted();
        } catch (Exception ignored) {
            return false;
        }
    }

    @Override
    public boolean isFailed() {
        try {
            return "error".equalsIgnoreCase(client.withName(name).get().getStatus().getPhase());
        } catch (Exception ignored) {
            return false;
        }
    }

    @Override
    public String getLogs() {
        return OpenshiftClient.get().getPodLog(OpenshiftClient.get().getLabeledPods("camel.apache.org/integration", name).get(0));
    }

    /**
     * Creates an Integration object in OpenShift.
     *
     * @param name name of the object
     * @param integrationData integration data object
     * @return integration instance
     */
    private Integration createIntegrationResource(String name, IntegrationData integrationData) {
        ObjectMeta metadata = new ObjectMeta();
        metadata.setName(name);

        IntegrationSpec spec = new IntegrationSpec();

        // if there are any properties set, use the configmap in the integration's configuration
        if (!integrationData.getProperties().isEmpty()) {
            Configuration config = new Configuration("configmap", name);
            spec.setConfiguration(Collections.singletonList(config));
        }

        Source source = new Source();
        source.setName("MyRouteBuilder.java");
        source.setContent("// camel-k: language=java\n" + integrationData.getIntegration());
        spec.setSources(Collections.singletonList(source));

        return new Integration("camel.apache.org/v1", "Integration", metadata, spec, new IntegrationStatus());
    }
}
