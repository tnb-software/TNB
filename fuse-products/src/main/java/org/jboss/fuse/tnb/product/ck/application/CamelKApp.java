package org.jboss.fuse.tnb.product.ck.application;

import org.jboss.fuse.tnb.common.config.OpenshiftConfiguration;
import org.jboss.fuse.tnb.common.config.TestConfiguration;
import org.jboss.fuse.tnb.common.openshift.OpenshiftClient;
import org.jboss.fuse.tnb.common.utils.IOUtils;
import org.jboss.fuse.tnb.common.utils.MapUtils;
import org.jboss.fuse.tnb.common.utils.WaitUtils;
import org.jboss.fuse.tnb.product.application.App;
import org.jboss.fuse.tnb.product.ck.generated.Integration;
import org.jboss.fuse.tnb.product.ck.generated.IntegrationList;
import org.jboss.fuse.tnb.product.ck.generated.IntegrationSpec;
import org.jboss.fuse.tnb.product.ck.generated.KameletBinding;
import org.jboss.fuse.tnb.product.ck.generated.KameletBindingList;
import org.jboss.fuse.tnb.product.ck.utils.CamelKSettings;
import org.jboss.fuse.tnb.product.ck.utils.CamelKSupport;
import org.jboss.fuse.tnb.product.ck.utils.InlineCustomizer;
import org.jboss.fuse.tnb.product.integration.IntegrationBuilder;
import org.jboss.fuse.tnb.product.integration.IntegrationData;
import org.jboss.fuse.tnb.product.integration.IntegrationGenerator;
import org.jboss.fuse.tnb.product.log.OpenshiftLog;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import cz.xtf.core.openshift.helpers.ResourceFunctions;
import io.fabric8.kubernetes.api.model.DeletionPropagation;
import io.fabric8.kubernetes.client.dsl.NonNamespaceOperation;
import io.fabric8.kubernetes.client.dsl.Resource;
import io.fabric8.kubernetes.client.dsl.base.CustomResourceDefinitionContext;

public class CamelKApp extends App {
    private static final Logger LOG = LoggerFactory.getLogger(CamelKApp.class);
    private static final CustomResourceDefinitionContext INTEGRATIONS_CONTEXT =
        CamelKSupport.integrationCRDContext(CamelKSettings.API_VERSION_DEFAULT);

    private final NonNamespaceOperation<Integration, IntegrationList, Resource<Integration>> integrationClient =
        OpenshiftClient.get().customResources(INTEGRATIONS_CONTEXT, Integration.class, IntegrationList.class)
            .inNamespace(OpenshiftConfiguration.openshiftNamespace());
    private final IntegrationData integrationData;

    private static final OpenshiftClient client = OpenshiftClient.get();
    private static final CustomResourceDefinitionContext kameletBindingCtx =
        CamelKSupport.kameletBindingCRDContext(CamelKSettings.KAMELET_API_VERSION_DEFAULT);
    private static final NonNamespaceOperation<KameletBinding, KameletBindingList, Resource<KameletBinding>> kameletBindingClient =
        client.customResources(kameletBindingCtx, KameletBinding.class, KameletBindingList.class).inNamespace(client.getNamespace());

    private KameletBinding kameletBinding = null;

    public CamelKApp(IntegrationBuilder integrationBuilder) {
        super(integrationBuilder.getIntegrationName());
        integrationData = IntegrationGenerator.toIntegrationData(integrationBuilder);

        // If there are any properties set, create a config map with the same map as the integration
        if (!integrationData.getProperties().isEmpty()) {
            OpenshiftClient.createConfigMap(name, Map.of("application.properties", MapUtils.propertiesToString(integrationData.getProperties())));
        }
    }

    public CamelKApp(KameletBinding kameletBinding) {
        super(kameletBinding.getMetadata().getName());//name of created integration is same as name of kameletbinding
        this.kameletBinding = kameletBinding;
        integrationData = null;
    }

    @Override
    public void start() {
        if (kameletBinding == null) {
            integrationClient.create(createIntegrationResource(name, integrationData));
        } else {
            LOG.info("Create KameletBinding " + kameletBinding.getMetadata().getName());
            kameletBindingClient.createOrReplace(kameletBinding);
        }
        log = new OpenshiftLog(p -> p.getMetadata().getLabels().containsKey("camel.apache.org/integration")
            && name.equals(p.getMetadata().getLabels().get("camel.apache.org/integration")));
    }

    @Override
    public void stop() {
        LOG.info("Collecting logs of integration {}", name);
        if (getLog() != null) {
            IOUtils.writeFile(TestConfiguration.appLocation().resolve(name + ".log"), getLog().toString());
        }
        LOG.info("Removing integration {}", name);
        if (kameletBinding != null) {
            LOG.info("Delete KameletBinding " + kameletBinding.getMetadata().getName());
            kameletBindingClient.delete(kameletBinding);
        }
        integrationClient.withName(name).withPropagationPolicy(DeletionPropagation.BACKGROUND).delete();
        WaitUtils.waitFor(() -> ResourceFunctions.areExactlyNPodsRunning(0)
                .apply(OpenshiftClient.get().getLabeledPods("camel.apache.org/integration", name)),
            "Waiting until the integration " + name + " is undeployed");
    }

    @Override
    public boolean isReady() {
        try {
            return "running".equalsIgnoreCase(integrationClient.withName(name).get().getStatus().getPhase())
                && ResourceFunctions.areExactlyNPodsReady(1).apply(OpenshiftClient.get().getLabeledPods("camel.apache.org/integration", name))
                && isCamelStarted();
        } catch (Exception ignored) {
            return false;
        }
    }

    @Override
    public boolean isFailed() {
        try {
            return "error".equalsIgnoreCase(integrationClient.withName(name).get().getStatus().getPhase());
        } catch (Exception ignored) {
            return false;
        }
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
        List<String> dependencies = new ArrayList<>();
        dependencies.add("mvn:org.apache.camel.k:camel-k-runtime");
        dependencies.add("mvn:org.apache.camel.quarkus:camel-quarkus-java-joor-dsl");
        dependencies.addAll(integrationData.getDependencies().stream().map(c -> "camel:" + c).collect(Collectors.toList()));
        is.setDependencies(dependencies);

        // if there are any properties set, use the configmap in the integration's configuration
        if (!integrationData.getProperties().isEmpty()) {
            IntegrationSpec.Configuration isc = new IntegrationSpec.Configuration("configmap", name);
            is.setConfiguration(Collections.singletonList(isc));
        }

        return new Integration.Builder()
            .name(name)
            .build(is);
    }
}
