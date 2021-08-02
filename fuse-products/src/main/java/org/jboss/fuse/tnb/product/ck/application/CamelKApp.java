package org.jboss.fuse.tnb.product.ck.application;

import org.jboss.fuse.tnb.common.config.OpenshiftConfiguration;
import org.jboss.fuse.tnb.common.config.TestConfiguration;
import org.jboss.fuse.tnb.common.openshift.OpenshiftClient;
import org.jboss.fuse.tnb.common.utils.IOUtils;
import org.jboss.fuse.tnb.common.utils.PropertiesUtils;
import org.jboss.fuse.tnb.common.utils.WaitUtils;
import org.jboss.fuse.tnb.product.application.App;
import org.jboss.fuse.tnb.product.ck.generated.Integration;
import org.jboss.fuse.tnb.product.ck.generated.IntegrationList;
import org.jboss.fuse.tnb.product.ck.generated.IntegrationSpec;
import org.jboss.fuse.tnb.product.ck.generated.KameletBinding;
import org.jboss.fuse.tnb.product.ck.generated.KameletBindingList;
import org.jboss.fuse.tnb.product.ck.utils.CamelKSettings;
import org.jboss.fuse.tnb.product.ck.utils.CamelKSupport;
import org.jboss.fuse.tnb.product.ck.utils.OwnerReferenceSetter;
import org.jboss.fuse.tnb.product.integration.IntegrationBuilder;
import org.jboss.fuse.tnb.product.integration.IntegrationData;
import org.jboss.fuse.tnb.product.integration.IntegrationGenerator;
import org.jboss.fuse.tnb.product.integration.IntegrationSpecCustomizer;
import org.jboss.fuse.tnb.product.log.OpenshiftLog;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import cz.xtf.core.openshift.helpers.ResourceFunctions;
import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.api.model.DeletionPropagation;
import io.fabric8.kubernetes.client.dsl.NonNamespaceOperation;
import io.fabric8.kubernetes.client.dsl.Resource;
import io.fabric8.kubernetes.client.dsl.base.CustomResourceDefinitionContext;

public class CamelKApp extends App {
    private static final Logger LOG = LoggerFactory.getLogger(CamelKApp.class);
    private static final CustomResourceDefinitionContext INTEGRATIONS_CONTEXT =
        CamelKSupport.integrationCRDContext(CamelKSettings.API_VERSION_DEFAULT);
    private static final ExecutorService EXECUTOR_SERVICE = Executors.newSingleThreadExecutor();

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
    private IntegrationBuilder integrationBuilder = null;

    public CamelKApp(IntegrationBuilder integrationBuilder) {
        this(integrationBuilder.getIntegrationName(), IntegrationGenerator.toIntegrationData(integrationBuilder));
        this.integrationBuilder = integrationBuilder;
    }

    public CamelKApp(String name, IntegrationData data) {
        super(name);
        integrationData = data;

        // If there are any properties set, create a config map with the same map as the integration
        // Set the later created integration object as the owner of the configmap, so that the configmap is deleted together with the integration
        if (!integrationData.getProperties().isEmpty()) {
            ConfigMap integrationProperties = OpenshiftClient.get()
                .createConfigMap(name, Map.of("application.properties", PropertiesUtils.toString(integrationData.getProperties())));
            EXECUTOR_SERVICE.submit(new OwnerReferenceSetter(integrationProperties, name));
        }
    }

    public CamelKApp(KameletBinding kameletBinding) {
        //name of created integration is same as name of kameletbinding
        super(kameletBinding.getMetadata().getName());
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
            && name.equals(p.getMetadata().getLabels().get("camel.apache.org/integration"))
            // find such pod where all containers are ready - sometimes in case of knative integrations it's possible that it gets the pod
            // that is in terminating state
            && "True".equals(p.getStatus().getConditions().stream().filter(c -> "ContainersReady".equals(c.getType())).findFirst().get().getStatus())
        );
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

        IntegrationSpec is = new IntegrationSpec();
        if (integrationData.getSourceName().endsWith(".yaml")) {
            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            try {
                is.setFlows(mapper.readValue(integrationData.getIntegration(), List.class));
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Can't parse yaml flow from source file.");
            }
        } else {
            IntegrationSpec.Source issrc = new IntegrationSpec.Source();
            issrc.setName(integrationData.getSourceName());
            issrc.setContent(integrationData.getIntegration());

            is.setSources(Collections.singletonList(issrc));
        }

        // TODO(anyone): remove if clause when 1.4 camel-k is officially released
        if (OpenshiftClient.get().podShell(OpenshiftClient.get().getAnyPod("name", "camel-k-operator")).executeWithBash("kamel version")
            .getOutput().contains("1.3")) {
            LOG.warn("Camel-K 1.3 detected, not setting dependencies into Integration object due to changes between 1.3 and 1.4");
        } else {
            String modelinePreffix = integrationData.getSourceName().endsWith(".yaml") ? "# camel-k: " : "// camel-k: ";
            List<String> modelines = integrationData.getIntegration().lines()
                .filter(l -> l.trim().startsWith(modelinePreffix))
                .map(l -> l.replaceAll(modelinePreffix, ""))
                .flatMap(l -> Stream.of(l.split("\\s")))
                .collect(Collectors.toList());

            is.setDependencies(modelines.stream()
                .filter(modeline -> modeline.contains("dependency"))
                .map(modeline -> modeline.split("=")[1])
                // unify dependency format
                .map(dependency -> dependency.replaceAll("^camel-", "camel:")).collect(
                    Collectors.toList()));
        }

        is.setConfiguration(new ArrayList<>());
        // if there are any properties set, use the configmap in the integration's configuration
        if (!integrationData.getProperties().isEmpty()) {
            IntegrationSpec.Configuration isc = new IntegrationSpec.Configuration("configmap", name);
            is.getConfiguration().add(isc);
        }

        // add the named secret to configuration
        if (integrationData.getSecretName() != null) {
            IntegrationSpec.Configuration iss = new IntegrationSpec.Configuration("secret", integrationData.getSecretName());
            is.getConfiguration().add(iss);
        }

        if (integrationBuilder != null) {
            integrationBuilder.getCustomizers().stream()
                .filter(IntegrationSpecCustomizer.class::isInstance)
                .map(IntegrationSpecCustomizer.class::cast)
                .forEach(i -> i.customizeIntegration(is));
        }

        return new Integration.Builder()
            .name(name)
            .build(is);
    }
}
