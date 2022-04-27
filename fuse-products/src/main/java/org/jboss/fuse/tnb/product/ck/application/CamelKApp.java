package org.jboss.fuse.tnb.product.ck.application;

import org.jboss.fuse.tnb.common.config.TestConfiguration;
import org.jboss.fuse.tnb.common.openshift.OpenshiftClient;
import org.jboss.fuse.tnb.common.utils.PropertiesUtils;
import org.jboss.fuse.tnb.common.utils.WaitUtils;
import org.jboss.fuse.tnb.product.application.App;
import org.jboss.fuse.tnb.product.ck.customizer.IntegrationSpecCustomizer;
import org.jboss.fuse.tnb.product.ck.integration.builder.CamelKIntegrationBuilder;
import org.jboss.fuse.tnb.product.ck.integration.resource.CamelKResource;
import org.jboss.fuse.tnb.product.ck.integration.resource.ResourceType;
import org.jboss.fuse.tnb.product.ck.log.IntegrationKitBuildLogHandler;
import org.jboss.fuse.tnb.product.ck.log.MavenBuildLogHandler;
import org.jboss.fuse.tnb.product.ck.utils.CamelKSettings;
import org.jboss.fuse.tnb.product.ck.utils.CamelKSupport;
import org.jboss.fuse.tnb.product.ck.utils.OwnerReferenceSetter;
import org.jboss.fuse.tnb.product.endpoint.Endpoint;
import org.jboss.fuse.tnb.product.integration.builder.AbstractIntegrationBuilder;
import org.jboss.fuse.tnb.product.integration.generator.IntegrationGenerator;
import org.jboss.fuse.tnb.product.log.OpenshiftLog;
import org.jboss.fuse.tnb.product.log.stream.LogStream;
import org.jboss.fuse.tnb.product.log.stream.OpenshiftLogStream;
import org.jboss.fuse.tnb.product.util.executor.Executor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import cz.xtf.core.openshift.helpers.ResourceFunctions;
import io.fabric8.camelk.client.CamelKClient;
import io.fabric8.camelk.v1.ConfigurationSpecBuilder;
import io.fabric8.camelk.v1.IntegrationSpecBuilder;
import io.fabric8.camelk.v1.ResourceSpec;
import io.fabric8.camelk.v1.ResourceSpecBuilder;
import io.fabric8.camelk.v1.SourceSpecBuilder;
import io.fabric8.camelk.v1.TraitSpec;
import io.fabric8.camelk.v1alpha1.KameletBinding;
import io.fabric8.camelk.v1alpha1.KameletBindingList;
import io.fabric8.knative.client.KnativeClient;
import io.fabric8.knative.eventing.v1.Trigger;
import io.fabric8.knative.serving.v1.Service;
import io.fabric8.kubernetes.api.model.Condition;
import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.api.model.DeletionPropagation;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.KubernetesClientException;
import io.fabric8.kubernetes.client.dsl.NonNamespaceOperation;
import io.fabric8.kubernetes.client.dsl.Resource;
import io.fabric8.kubernetes.client.dsl.base.CustomResourceDefinitionContext;
import io.fabric8.kubernetes.client.utils.Serialization;

public class CamelKApp extends App {
    private static final Logger LOG = LoggerFactory.getLogger(CamelKApp.class);

    private static final CustomResourceDefinitionContext kameletBindingCtx =
        CamelKSupport.kameletBindingCRDContext(CamelKSettings.KAMELET_API_VERSION_DEFAULT);
    private final NonNamespaceOperation<KameletBinding, KameletBindingList, Resource<KameletBinding>> kameletBindingClient;
    private final CamelKClient camelKClient;

    private Object integrationSource;
    private final List<Future<?>> logStreams = new ArrayList<>();

    private CamelKApp(String name) {
        super(name);

        if (name.length() > 63) {
            throw new RuntimeException("Camel-K integration name " + name + " must be shorter than 63 characters");
        }

        // https://github.com/fabric8io/kubernetes-client/issues/3854
        OpenshiftClient client = OpenshiftClient.get();
        kameletBindingClient = client.customResources(kameletBindingCtx, KameletBinding.class, KameletBindingList.class)
            .inNamespace(client.getNamespace());
        camelKClient = client.adapt(CamelKClient.class);
    }

    public CamelKApp(AbstractIntegrationBuilder<?> integrationBuilder) {
        this(integrationBuilder.getIntegrationName());
        this.integrationSource = integrationBuilder;
    }

    public CamelKApp(KameletBinding kameletBinding) {
        // name of created integration is same as name of kameletbinding
        this(kameletBinding.getMetadata().getName());
        this.integrationSource = kameletBinding;
    }

    @Override
    public void start() {
        if (integrationSource instanceof KameletBinding) {
            LOG.info("Creating KameletBinding {}", name);
            kameletBindingClient.createOrReplace((KameletBinding) integrationSource);
        } else {
            createIntegrationResources((AbstractIntegrationBuilder<?>) integrationSource);
        }

        endpoint = new Endpoint(() -> OpenshiftClient.get().adapt(KnativeClient.class).routes()
            .withName(name).get().getStatus().getUrl());

        Predicate<Pod> podSelector = p -> p.getMetadata().getLabels().containsKey("camel.apache.org/integration")
            && name.equals(p.getMetadata().getLabels().get("camel.apache.org/integration"));
        log = new OpenshiftLog(podSelector, getName());

        if (TestConfiguration.streamLogs()) {
            logStreams.add(Executor.get().submit(new MavenBuildLogHandler(getName())));
            logStreams.add(Executor.get().submit(new IntegrationKitBuildLogHandler(getName())));
            logStream = new OpenshiftLogStream(podSelector, LogStream.marker(name));
        }
    }

    @Override
    public void stop() {
        logStreams.forEach(f -> f.cancel(true));
        logStreams.clear();
        if (logStream != null) {
            logStream.stop();
        }
        if (getLog() != null) {
            ((OpenshiftLog) getLog()).save(started);
        }

        LOG.info("Removing integration {}", name);
        if (integrationSource instanceof KameletBinding) {
            LOG.info("Deleting KameletBinding {}", name);
            kameletBindingClient.withName(name).withPropagationPolicy(DeletionPropagation.BACKGROUND).delete();
        } else {
            camelKClient.v1().integrations().withName(name).withPropagationPolicy(DeletionPropagation.BACKGROUND).delete();
        }
        WaitUtils.waitFor(() -> ResourceFunctions.areExactlyNPodsRunning(0)
                .apply(OpenshiftClient.get().getLabeledPods("camel.apache.org/integration", name)),
            "Waiting until the integration " + name + " is undeployed");
    }

    @Override
    public boolean isReady() {
        try {
            return "running".equalsIgnoreCase(camelKClient.v1().integrations().withName(name).get().getStatus().getPhase())
                && ResourceFunctions.areExactlyNPodsReady(1).apply(OpenshiftClient.get().getLabeledPods("camel.apache.org/integration", name));
        } catch (Exception ignored) {
            return false;
        }
    }

    @Override
    public boolean isFailed() {
        try {
            return "error".equalsIgnoreCase(camelKClient.v1().integrations().withName(name).get().getStatus().getPhase());
        } catch (Exception ignored) {
            return false;
        }
    }

    @Override
    public void waitUntilReady() {
        super.waitUntilReady();
        KnativeClient knClient = OpenshiftClient.get().adapt(KnativeClient.class);
        Service svc = null;
        try {
            svc = knClient.services().withName(name).get();
        } catch (KubernetesClientException ex) {
            // If knative is not installed, ignore the exception
            if (!ex.getMessage().contains("Not Found")) {
                throw ex;
            }
        }
        if (svc != null) {
            waitForKnativeResource(() -> knClient.services().withName(name).get());
        }
        List<Trigger> triggers = new ArrayList<>();
        try {
            triggers = knClient.triggers().withLabel("camel.apache.org/integration", name).list().getItems();
        } catch (KubernetesClientException ex) {
            // If knative is not installed, ignore the exception
            if (!ex.getMessage().contains("Not Found")) {
                throw ex;
            }
        }
        if (triggers.size() > 0) {
            Trigger trigger = knClient.triggers().withName(triggers.get(0).getMetadata().getName()).get();
            waitForKnativeResource(() -> knClient.triggers().withName(trigger.getMetadata().getName()).get());
            waitForKnativeResource(() -> knClient.subscriptions().withLabel("eventing.knative.dev/trigger", trigger.getMetadata().getName())
                .list().getItems().get(0));
        }
    }

    /**
     * Creates all integration related objects needed.
     *
     * @param integrationBuilder integrationbuilder instance
     */
    private void createIntegrationResources(AbstractIntegrationBuilder<?> integrationBuilder) {
        ObjectMapper jsonMapper = new ObjectMapper();

        io.fabric8.camelk.v1.IntegrationBuilder integration = new io.fabric8.camelk.v1.IntegrationBuilder()
            .withNewMetadata()
            .withName(integrationBuilder.getIntegrationName())
            .endMetadata();

        IntegrationSpecBuilder specBuilder = new IntegrationSpecBuilder();

        String integrationSourceCode = IntegrationGenerator.toString(integrationBuilder);

        if (integrationBuilder.getFileName().endsWith(".yaml")) {
            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            try {
                specBuilder.withFlows(mapper.readValue(integrationSourceCode, JsonNode[].class));
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Can't parse yaml flow from source file.");
            }
        } else {
            specBuilder.addToSources(new SourceSpecBuilder()
                .withName(integrationBuilder.getFileName())
                .withContent(integrationSourceCode)
                .build()
            );
        }

        String modelinePrefix = integrationBuilder.getFileName().endsWith(".yaml") ? "# camel-k: " : "// camel-k: ";
        List<String> modelines = integrationSourceCode.lines()
            .filter(l -> l.trim().startsWith(modelinePrefix))
            .map(l -> l.replaceAll(modelinePrefix, ""))
            .flatMap(l -> Stream.of(l.split("\\s")))
            .collect(Collectors.toList());

        specBuilder.withDependencies(modelines.stream()
            .filter(modeline -> modeline.contains("dependency"))
            .map(modeline -> modeline.split("=")[1])
            // unify dependency format
            .map(dependency -> dependency.replaceAll("^camel-quarkus-|^camel-", "camel:")).collect(
                Collectors.toList()));

        List<String> buildProperties = modelines.stream()
            .filter(modeline -> modeline.contains("build-property"))
            .map(modeline -> modeline.split("=", 2)[1])
            .collect(Collectors.toList());

        Map<String, TraitSpec> traits = new HashMap<>();
        traits.put("builder", new TraitSpec(jsonMapper.valueToTree(Map.of("properties", buildProperties))));

        Set<String> traitDefinitions = modelines.stream()
            .filter(modeline -> modeline.contains("trait"))
            .map(modeline -> modeline.split("=", 2)[1])
            .collect(Collectors.toSet());

        Map<String, Map<String, Object>> td = processTraits(traitDefinitions);
        for (Map.Entry<String, Map<String, Object>> entry : td.entrySet()) {
            traits.put(entry.getKey(), new TraitSpec(jsonMapper.valueToTree(entry.getValue())));
        }

        specBuilder.withTraits(traits);

        // if there are any properties set, use the configmap in the integration's configuration
        if (!integrationBuilder.getProperties().isEmpty()) {
            specBuilder.withConfiguration(new ConfigurationSpecBuilder()
                .withType("configmap")
                .withValue(name)
                .build()
            );
        }

        if (integrationBuilder instanceof CamelKIntegrationBuilder) {
            CamelKIntegrationBuilder ckib = (CamelKIntegrationBuilder) integrationBuilder;
            // add the named secret to configuration
            if (ckib.getSecret() != null) {
                specBuilder.withConfiguration(new ConfigurationSpecBuilder()
                    .withType("secret")
                    .withValue(ckib.getSecret())
                    .build()
                );
            }
        }

        // add resources
        List<ResourceSpec> resources = new ArrayList<>();
        for (org.jboss.fuse.tnb.product.integration.Resource resource : integrationBuilder.getResources()) {
            ResourceSpecBuilder rsb = new ResourceSpecBuilder()
                .withName(new File(resource.getName()).getName())
                .withContent(resource.getContent());
            ResourceType type = resource instanceof CamelKResource ? ((CamelKResource) resource).getType() : ResourceType.DATA;
            rsb.withType(type.getValue());
            rsb.withMountPath(type == ResourceType.DATA ? "/etc/camel/resources/" + resource.getName() : null);
            resources.add(rsb.build());
        }
        specBuilder.withResources(resources);

        // Process all integration spec customizers
        integrationBuilder.getCustomizers().stream()
            .filter(IntegrationSpecCustomizer.class::isInstance)
            .map(IntegrationSpecCustomizer.class::cast)
            .forEach(i -> i.customizeIntegration(specBuilder));

        integration.withSpec(specBuilder.build());

        // Create the Integration object
        camelKClient.v1().integrations().createOrReplace(integration.build());

        // If there are any properties set, create a config map with the same map as the integration
        // Set the later created integration object as the owner of the configmap, so that the configmap is deleted together with the integration
        if (!integrationBuilder.getProperties().isEmpty()) {
            ConfigMap integrationProperties = OpenshiftClient.get()
                .createConfigMap(name, Map.of("application.properties", PropertiesUtils.toString(integrationBuilder.getProperties())));
            Executor.get().submit(new OwnerReferenceSetter(integrationProperties, name));
        }
    }

    private Map<String, Map<String, Object>> processTraits(Collection<String> traitDefinitions) {
        Map<String, Map<String, Object>> out = new HashMap<>();
        for (String t : traitDefinitions) {
            String[] kv = t.split("\\.", 2);
            String[] config = kv[1].split("=");
            Map<String, Object> cfg = Optional.ofNullable(out.get(kv[0])).orElse(new HashMap<>());

            cfg.put(config[0], deriveType(config[1]));
            out.put(kv[0], cfg);
        }
        return out;
    }

    private Object deriveType(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            // no-op
        }
        return "true".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value) ? Boolean.parseBoolean(value)
            : value;
    }

    /**
     * Waits until the knative resource is ready. There can be multiple knative resources created by the integration and there is no other common
     * interface than HasMetadata that would allow access to status, so serialize it to json and check the status that way.
     *
     * @param supplier supplier that returns the object
     */
    private void waitForKnativeResource(Supplier<HasMetadata> supplier) {
        HasMetadata item = supplier.get();
        WaitUtils.waitFor(() -> {
            HasMetadata i = supplier.get();
            JSONObject json = new JSONObject(Serialization.asJson(i));
            JSONObject status;
            try {
                status = json.getJSONObject("status");
            } catch (JSONException e) {
                return false;
            }
            JSONArray conditions;
            try {
                conditions = status.getJSONArray("conditions");
            } catch (JSONException e) {
                return false;
            }
            if (conditions.length() == 0) {
                return false;
            }

            final List<Condition> conditionsList = StreamSupport.stream(conditions.spliterator(), false)
                .map(c -> Serialization.unmarshal(c.toString(), Condition.class))
                .collect(Collectors.toList());
            LOG.debug("Knative {} {}: {}", i.getKind(), i.getMetadata().getName(), conditionsList.stream()
                .map(c -> c.getType() + ": " + c.getStatus()).collect(Collectors.joining(", ")));
            return conditionsList.stream().allMatch(c -> "True".equals(c.getStatus()));
        }, 12, 5000L, "Waiting for the Knative " + item.getKind() + " " + item.getMetadata().getName() + " to be ready");
    }
}
