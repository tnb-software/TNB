package software.tnb.product.ck.application;

import software.tnb.common.config.TestConfiguration;
import software.tnb.common.openshift.OpenshiftClient;
import software.tnb.common.utils.PropertiesUtils;
import software.tnb.common.utils.WaitUtils;
import software.tnb.product.application.App;
import software.tnb.product.application.Phase;
import software.tnb.product.ck.customizer.IntegrationSpecCustomizer;
import software.tnb.product.ck.integration.builder.CamelKIntegrationBuilder;
import software.tnb.product.ck.integration.resource.CamelKResource;
import software.tnb.product.ck.integration.resource.ResourceType;
import software.tnb.product.ck.log.IntegrationKitBuildLogHandler;
import software.tnb.product.ck.log.MavenBuildLogHandler;
import software.tnb.product.ck.utils.OwnerReferenceSetter;
import software.tnb.product.endpoint.Endpoint;
import software.tnb.product.integration.builder.AbstractIntegrationBuilder;
import software.tnb.product.integration.generator.IntegrationGenerator;
import software.tnb.product.log.OpenshiftLog;
import software.tnb.product.log.stream.LogStream;
import software.tnb.product.log.stream.OpenshiftLogStream;
import software.tnb.product.util.executor.Executor;

import org.apache.camel.v1.Integration;
import org.apache.camel.v1.IntegrationSpec;
import org.apache.camel.v1.Pipe;
import org.apache.camel.v1.integrationspec.Configuration;
import org.apache.camel.v1.integrationspec.Sources;
import org.apache.camel.v1.integrationspec.Traits;
import org.apache.camel.v1.integrationspec.traits.Builder;
import org.apache.camel.v1.integrationspec.traits.Mount;
import org.apache.camel.v1alpha1.KameletBinding;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import cz.xtf.core.openshift.helpers.ResourceFunctions;
import io.fabric8.knative.client.KnativeClient;
import io.fabric8.knative.eventing.v1.Trigger;
import io.fabric8.knative.serving.v1.Route;
import io.fabric8.knative.serving.v1.Service;
import io.fabric8.kubernetes.api.model.Condition;
import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.api.model.DeletionPropagation;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.ObjectMetaBuilder;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.KubernetesClientException;
import io.fabric8.kubernetes.client.utils.Serialization;

public class CamelKApp extends App {
    private static final Logger LOG = LoggerFactory.getLogger(CamelKApp.class);

    private Object integrationSource;
    private MavenBuildLogHandler buildLogHandler;

    public CamelKApp(String name) {
        super(name);

        if (name.length() > 63) {
            throw new RuntimeException("Camel-K integration name " + name + " must be shorter than 63 characters");
        }
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

    public CamelKApp(Pipe pipe) {
        this(pipe.getMetadata().getName());
        this.integrationSource = pipe;
    }

    @Override
    public void start() {
        if (integrationSource instanceof KameletBinding) {
            LOG.info("Creating KameletBinding {}", name);
            OpenshiftClient.get().resources(KameletBinding.class).resource((KameletBinding) integrationSource).create();
        } else if (integrationSource instanceof AbstractIntegrationBuilder) {
            createIntegrationResources((AbstractIntegrationBuilder<?>) integrationSource);
        } else if (integrationSource instanceof Pipe) {
            LOG.info("Creating Pipe {}", name);
            OpenshiftClient.get().resources(Pipe.class).resource((Pipe) integrationSource).create();
        }

        endpoint = new Endpoint(() -> {
            Route knativeRoute = OpenshiftClient.get().adapt(KnativeClient.class).routes().withName(name).get();
            if (knativeRoute != null) {
                return knativeRoute.getStatus().getUrl();
            } else {
                return "http://" + OpenshiftClient.get().routes().withName(name).get().getSpec().getHost();
            }
        });

        Predicate<Pod> podSelector = p -> p.getMetadata().getLabels() != null
            && p.getMetadata().getLabels().containsKey("camel.apache.org/integration")
            && name.equals(p.getMetadata().getLabels().get("camel.apache.org/integration"));
        log = new OpenshiftLog(podSelector, getLogPath());

        if (TestConfiguration.streamLogs()) {
            buildLogHandler = new MavenBuildLogHandler(getName());
            Executor.get().submit(buildLogHandler);
            Executor.get().submit(new IntegrationKitBuildLogHandler(getName(), getLogPath(Phase.BUILD)));
            logStream = new OpenshiftLogStream(podSelector, LogStream.marker(name));
        }
    }

    @Override
    public void stop() {
        if (buildLogHandler != null) {
            buildLogHandler.stop();
        }
        if (logStream != null) {
            logStream.stop();
        }
        if (getLog() != null) {
            ((OpenshiftLog) getLog()).save(started);
        }

        LOG.info("Removing integration {}", name);
        if (integrationSource instanceof KameletBinding) {
            LOG.info("Deleting KameletBinding {}", name);
            OpenshiftClient.get().resources(KameletBinding.class).withName(name).withPropagationPolicy(DeletionPropagation.BACKGROUND).delete();
        } else if (integrationSource instanceof Pipe) {
            LOG.info("Deleting Pipe {}", name);
            OpenshiftClient.get().resources(Pipe.class).withName(name).withPropagationPolicy(DeletionPropagation.BACKGROUND).delete();
        } else {
            OpenshiftClient.get().resources(Integration.class).withName(name).withPropagationPolicy(DeletionPropagation.BACKGROUND).delete();
        }
        WaitUtils.waitFor(() -> ResourceFunctions.areExactlyNPodsRunning(0)
                .apply(OpenshiftClient.get().getLabeledPods("camel.apache.org/integration", name)),
            "Waiting until the integration " + name + " is undeployed");
    }

    @Override
    public boolean isReady() {
        try {
            return "running".equalsIgnoreCase(OpenshiftClient.get().resources(Integration.class).withName(name).get().getStatus().getPhase())
                && ResourceFunctions.areExactlyNPodsReady(1).apply(OpenshiftClient.get().getLabeledPods("camel.apache.org/integration", name));
        } catch (Exception ignored) {
            return false;
        }
    }

    @Override
    public boolean isFailed() {
        try {
            return "error".equalsIgnoreCase(OpenshiftClient.get().resources(Integration.class).withName(name).get().getStatus().getPhase());
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
        if (!triggers.isEmpty()) {
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

        ObjectMeta metadata = new ObjectMetaBuilder()
            .withName(integrationBuilder.getIntegrationName())
            .build();

        IntegrationSpec spec = new IntegrationSpec();

        String integrationSourceCode = IntegrationGenerator.toString(integrationBuilder);

        if (integrationBuilder.getFileName().endsWith(".yaml")) {
            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            try {
                spec.setFlows(mapper.readValue(integrationSourceCode, new TypeReference<>() { }));
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Can't parse yaml flow from source file.");
            }
        } else {
            Sources sources = new Sources();
            sources.setContent(integrationSourceCode);
            sources.setName(integrationBuilder.getFileName());
            spec.setSources(List.of(sources));
        }

        String modelinePrefixPattern = integrationBuilder.getFileName().endsWith(".yaml") ? "^# *camel-k:" : "^// *camel-k:";
        List<String> modelines = integrationSourceCode.lines()
            .filter(l -> l.trim().matches(modelinePrefixPattern + ".*"))
            .map(l -> l.replaceAll(modelinePrefixPattern, ""))
            .flatMap(l -> Stream.of(l.split("\\s")))
            .toList();

        spec.setDependencies(modelines.stream()
            .filter(modeline -> modeline.contains("dependency"))
            .map(modeline -> modeline.split("=")[1])
            // unify dependency format
            .map(dependency -> dependency.replaceAll("^camel-quarkus-|^camel-", "camel:")).collect(
                Collectors.toList()));

        List<String> buildProperties = modelines.stream()
            .filter(modeline -> modeline.contains("build-property"))
            .map(modeline -> modeline.split("=", 2)[1])
            .toList();

        Set<String> traitDefinitions = modelines.stream()
            .filter(modeline -> modeline.contains("trait"))
            .map(modeline -> modeline.split("=", 2)[1])
            .collect(Collectors.toSet());

        Map<String, Map<String, Object>> td = processTraits(traitDefinitions);
        spec.setTraits(jsonMapper.convertValue(td, Traits.class));

        Builder builder = spec.getTraits().getBuilder() == null ? new Builder() : spec.getTraits().getBuilder();
        List<String> properties = builder.getProperties() == null ? new ArrayList<>() : builder.getProperties();
        properties.addAll(buildProperties);
        builder.setProperties(properties);
        spec.getTraits().setBuilder(builder);

        List<Configuration> specConfiguration = new ArrayList<>();
        // if there are any properties set, use the configmap in the integration's configuration
        if (!integrationBuilder.getProperties().isEmpty()) {
            Configuration props = new Configuration();
            props.setType("configmap");
            props.setValue(name);
            specConfiguration.add(props);
        }

        if (integrationBuilder instanceof CamelKIntegrationBuilder) {
            CamelKIntegrationBuilder ckib = (CamelKIntegrationBuilder) integrationBuilder;
            // add the named secret to configuration
            if (ckib.getSecret() != null) {
                Configuration secret = new Configuration();
                secret.setType("secret");
                secret.setValue(ckib.getSecret());
                specConfiguration.add(secret);
            }
        }

        // add resources
        List<String> resources = new ArrayList<>();
        for (software.tnb.product.integration.Resource resource : integrationBuilder.getResources()) {
            ResourceType type = resource instanceof CamelKResource ? ((CamelKResource) resource).getType() : ResourceType.FILE;
            if (type == ResourceType.FILE) {
                // Create a configmap with the file and use the configmap mount
                String cmName = resource.getName().replaceAll("\\.", "-");
                ConfigMap cm = OpenshiftClient.get().createConfigMap(cmName, Map.of(resource.getName(), resource.getContent()));
                Executor.get().submit(new OwnerReferenceSetter(cm, name));
                type = ResourceType.CONFIG_MAP;
                resource.setName(cmName);
            }

            resources.add(type.getValue() + ":" + resource.getName());
        }
        final Mount mount = spec.getTraits().getMount() == null ? new Mount() : spec.getTraits().getMount();
        List<String> mountResources = mount.getResources() == null ? new ArrayList<>() : mount.getResources();
        mountResources.addAll(resources);
        mount.setResources(mountResources);
        spec.getTraits().setMount(mount);

        // Process all integration spec customizers
        integrationBuilder.getCustomizers().stream()
            .filter(IntegrationSpecCustomizer.class::isInstance)
            .map(IntegrationSpecCustomizer.class::cast)
            .forEach(i -> i.customizeIntegration(spec));

        spec.setConfiguration(specConfiguration);

        // Create the Integration object
        Integration i = new Integration();
        i.setMetadata(metadata);
        i.setSpec(spec);
        OpenshiftClient.get().resources(Integration.class).resource(i).create();

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
            if (conditions.isEmpty()) {
                return false;
            }

            final List<Condition> conditionsList = StreamSupport.stream(conditions.spliterator(), false)
                .map(c -> Serialization.unmarshal(c.toString(), Condition.class))
                .toList();
            LOG.debug("Knative {} {}: {}", i.getKind(), i.getMetadata().getName(), conditionsList.stream()
                .map(c -> c.getType() + ": " + c.getStatus()).collect(Collectors.joining(", ")));
            return conditionsList.stream().allMatch(c -> "True".equals(c.getStatus()));
        }, 12, 5000L, "Waiting for the Knative " + item.getKind() + " " + item.getMetadata().getName() + " to be ready");
    }
}
