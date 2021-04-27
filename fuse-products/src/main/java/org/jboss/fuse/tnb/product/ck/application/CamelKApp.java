package org.jboss.fuse.tnb.product.ck.application;

import org.jboss.fuse.tnb.common.config.OpenshiftConfiguration;
import org.jboss.fuse.tnb.common.openshift.OpenshiftClient;
import org.jboss.fuse.tnb.common.utils.MapUtils;
import org.jboss.fuse.tnb.product.application.App;
import org.jboss.fuse.tnb.product.ck.generated.Integration;
import org.jboss.fuse.tnb.product.ck.generated.IntegrationList;
import org.jboss.fuse.tnb.product.ck.generated.IntegrationSpec;
import org.jboss.fuse.tnb.product.ck.utils.CamelKSettings;
import org.jboss.fuse.tnb.product.ck.utils.CamelKSupport;
import org.jboss.fuse.tnb.product.integration.IntegrationBuilder;
import org.jboss.fuse.tnb.product.integration.IntegrationData;
import org.jboss.fuse.tnb.product.integration.IntegrationGenerator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Reader;
import java.util.Collections;
import java.util.Map;

import cz.xtf.core.openshift.helpers.ResourceFunctions;
import io.fabric8.kubernetes.api.model.DeletionPropagation;
import io.fabric8.kubernetes.client.dsl.NonNamespaceOperation;
import io.fabric8.kubernetes.client.dsl.Resource;
import io.fabric8.kubernetes.client.dsl.base.CustomResourceDefinitionContext;

public class CamelKApp extends App {
    private static final Logger LOG = LoggerFactory.getLogger(CamelKApp.class);
    private static final CustomResourceDefinitionContext INTEGRATIONS_CONTEXT =
        CamelKSupport.integrationCRDContext(CamelKSettings.API_VERSION_DEFAULT);

    private final NonNamespaceOperation<Integration, IntegrationList, Resource<Integration>> client =
        OpenshiftClient.get().customResources(INTEGRATIONS_CONTEXT, Integration.class, IntegrationList.class)
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

    /**
     * Integration built from kameletbinding
     *
     * @param integrationName name of kameletbinding
     */
    public CamelKApp(String integrationName) {
        super(integrationName);
        integrationData = null;
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
    public Reader getLogs() {
        return OpenshiftClient.get().getPodLogReader(OpenshiftClient.get().getLabeledPods("camel.apache.org/integration", name).get(0));
    }

    /**
     * Creates an Integration object in OpenShift.
     *
     * @param name name of the object
     * @param integrationData integration data object
     * @return integration instance
     */
    private Integration createIntegrationResource(String name, IntegrationData integrationData) {
        IntegrationSpec.Source issrc = new IntegrationSpec.Source();
        issrc.setName("MyRouteBuilder.java");
        issrc.setContent("// camel-k: language=java\n" + integrationData.getIntegration());

        IntegrationSpec is = new IntegrationSpec();
        is.setSources(Collections.singletonList(issrc));

        // if there are any properties set, use the configmap in the integration's configuration
        if (!integrationData.getProperties().isEmpty()) {
            IntegrationSpec.Configuration isc = new IntegrationSpec.Configuration("configmap", name);
            is.setConfiguration(Collections.singletonList(isc));
        }

        Integration integration = new Integration.Builder()
            .name(name)
            .build(is);

        return integration;
    }
}
