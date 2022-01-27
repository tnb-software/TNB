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
import org.jboss.fuse.tnb.product.integration.IntegrationGenerator;
import org.jboss.fuse.tnb.product.integration.IntegrationSpecCustomizer;
import org.jboss.fuse.tnb.product.integration.ResourceType;
import org.jboss.fuse.tnb.product.log.OpenshiftLog;
import org.jboss.fuse.tnb.product.rp.Attachments;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import cz.xtf.core.openshift.helpers.ResourceFunctions;
import io.fabric8.knative.client.KnativeClient;
import io.fabric8.knative.eventing.v1.Trigger;
import io.fabric8.knative.serving.v1.Service;
import io.fabric8.kubernetes.api.model.Condition;
import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.api.model.DeletionPropagation;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.PodConditionBuilder;
import io.fabric8.kubernetes.client.dsl.NonNamespaceOperation;
import io.fabric8.kubernetes.client.dsl.Resource;
import io.fabric8.kubernetes.client.dsl.base.CustomResourceDefinitionContext;
import io.fabric8.kubernetes.client.utils.Serialization;

public class CamelKApp extends App {
    private static final Logger LOG = LoggerFactory.getLogger(CamelKApp.class);
    private static final ExecutorService EXECUTOR_SERVICE = Executors.newSingleThreadExecutor();

    private static final CustomResourceDefinitionContext INTEGRATIONS_CONTEXT =
        CamelKSupport.integrationCRDContext(CamelKSettings.API_VERSION_DEFAULT);
    private static final CustomResourceDefinitionContext kameletBindingCtx =
        CamelKSupport.kameletBindingCRDContext(CamelKSettings.KAMELET_API_VERSION_DEFAULT);
    private static final OpenshiftClient client = OpenshiftClient.get();
    private static final NonNamespaceOperation<KameletBinding, KameletBindingList, Resource<KameletBinding>> kameletBindingClient =
        client.customResources(kameletBindingCtx, KameletBinding.class, KameletBindingList.class).inNamespace(client.getNamespace());
    private final NonNamespaceOperation<Integration, IntegrationList, Resource<Integration>> integrationClient =
        OpenshiftClient.get().customResources(INTEGRATIONS_CONTEXT, Integration.class, IntegrationList.class)
            .inNamespace(OpenshiftConfiguration.openshiftNamespace());
    private Object integrationSource;

    private CamelKApp(String name) {
        super(name);
        if (name.length() > 63) {
            throw new RuntimeException("Camel-K integration name " + name + " must be shorter than 63 characters");
        }
    }

    public CamelKApp(IntegrationBuilder integrationBuilder) {
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
            createIntegrationResources((IntegrationBuilder) integrationSource);
        }
        log = new OpenshiftLog(p -> p.getMetadata().getLabels().containsKey("camel.apache.org/integration")
            && name.equals(p.getMetadata().getLabels().get("camel.apache.org/integration"))
            // find such pod where all containers are ready - sometimes in case of knative integrations it's possible that it gets the pod
            // that is in terminating state
            && "True".equals(p.getStatus().getConditions().stream().filter(c -> "ContainersReady".equals(c.getType())).findFirst()
            .orElse(new PodConditionBuilder().withStatus("False").build()).getStatus())
        );
    }

    @Override
    public void stop() {
        LOG.info("Collecting logs of integration {}", name);
        if (getLog() != null) {
            final Path logPath = TestConfiguration.appLocation().resolve(name + ".log");
            IOUtils.writeFile(logPath, getLog().toString());
            Attachments.addAttachment(logPath);
        }
        LOG.info("Removing integration {}", name);
        if (integrationSource instanceof KameletBinding) {
            LOG.info("Deleting KameletBinding {}", name);
            kameletBindingClient.withName(name).withPropagationPolicy(DeletionPropagation.BACKGROUND).delete();
        } else {
            integrationClient.withName(name).withPropagationPolicy(DeletionPropagation.BACKGROUND).delete();
        }
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

    @Override
    public void waitUntilReady() {
        super.waitUntilReady();
        KnativeClient knClient = OpenshiftClient.get().adapt(KnativeClient.class);
        Service svc = knClient.services().withName(name).get();
        if (svc != null) {
            waitForKnativeResource(() -> knClient.services().withName(name).get());
        }
        final List<Trigger> triggers = knClient.triggers().withLabel("camel.apache.org/integration", name).list().getItems();
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
    private void createIntegrationResources(IntegrationBuilder integrationBuilder) {
        IntegrationSpec is = new IntegrationSpec();

        String integrationSourceCode = IntegrationGenerator.toString(integrationBuilder);
        if (integrationBuilder.getSourceName().endsWith(".yaml")) {
            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            try {
                is.setFlows(mapper.readValue(integrationSourceCode, List.class));
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Can't parse yaml flow from source file.");
            }
        } else {
            IntegrationSpec.Source issrc = new IntegrationSpec.Source();
            issrc.setName(integrationBuilder.getSourceName());
            issrc.setContent(integrationSourceCode);
            is.setSources(Collections.singletonList(issrc));
        }

        String modelinePrefix = integrationBuilder.getSourceName().endsWith(".yaml") ? "# camel-k: " : "// camel-k: ";
        List<String> modelines = integrationSourceCode.lines()
            .filter(l -> l.trim().startsWith(modelinePrefix))
            .map(l -> l.replaceAll(modelinePrefix, ""))
            .flatMap(l -> Stream.of(l.split("\\s")))
            .collect(Collectors.toList());

        is.setDependencies(modelines.stream()
            .filter(modeline -> modeline.contains("dependency"))
            .map(modeline -> modeline.split("=")[1])
            // unify dependency format
            .map(dependency -> dependency.replaceAll("^camel-quarkus-|^camel-", "camel:")).collect(
                Collectors.toList()));

        List<String> buildProperties = modelines.stream()
            .filter(modeline -> modeline.contains("build-property"))
            .map(modeline -> modeline.split("=", 2)[1])
            .collect(Collectors.toList());

        Map<String, IntegrationSpec.TraitConfig> traits = new HashMap<>();
        traits.put("builder", new IntegrationSpec.TraitConfig("properties", buildProperties));

        Set<String> traitDefinitions = modelines.stream()
            .filter(modeline -> modeline.contains("trait"))
            .map(modeline -> modeline.split("=", 2)[1])
            .collect(Collectors.toSet());

        Map<String, Map<String, Object>> td = processTraits(traitDefinitions);
        for (String t : td.keySet()) {
            traits.put(t, new IntegrationSpec.TraitConfig(td.get(t)));
        }

        is.setTraits(traits);

        is.setConfiguration(new ArrayList<>());
        // if there are any properties set, use the configmap in the integration's configuration
        if (!integrationBuilder.getProperties().isEmpty()) {
            IntegrationSpec.Configuration isc = new IntegrationSpec.Configuration("configmap", name);
            is.getConfiguration().add(isc);
        }

        // add the named secret to configuration
        if (integrationBuilder.getSecret() != null) {
            IntegrationSpec.Configuration iss = new IntegrationSpec.Configuration("secret", integrationBuilder.getSecret());
            is.getConfiguration().add(iss);
        }

        // add resources
        if (integrationBuilder.getResources() != null && !integrationBuilder.getResources().isEmpty()) {
            is.setResources(new ArrayList<>());
            integrationBuilder.getResources().forEach(resource -> {
                IntegrationSpec.Resource res =
                    new IntegrationSpec.Resource(resource.getType().getValue(),
                        // add data resources to the integration classpath
                        resource.getType().equals(ResourceType.DATA) ? "/etc/camel/resources/" + resource.getName() : null,
                        new File(resource.getName()).getName(),
                        resource.getContent());
                is.getResources().add(res);
            });
        }

        // Process all integration spec customizers
        integrationBuilder.getCustomizers().stream()
            .filter(IntegrationSpecCustomizer.class::isInstance)
            .map(IntegrationSpecCustomizer.class::cast)
            .forEach(i -> i.customizeIntegration(is));

        // Create the Integration object
        integrationClient.createOrReplace(new Integration.Builder()
            .name(name)
            .build(is)
        );

        // If there are any properties set, create a config map with the same map as the integration
        // Set the later created integration object as the owner of the configmap, so that the configmap is deleted together with the integration
        if (!integrationBuilder.getProperties().isEmpty()) {
            ConfigMap integrationProperties = OpenshiftClient.get()
                .createConfigMap(name, Map.of("application.properties", PropertiesUtils.toString(integrationBuilder.getProperties())));
            EXECUTOR_SERVICE.submit(new OwnerReferenceSetter(integrationProperties, name));
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
