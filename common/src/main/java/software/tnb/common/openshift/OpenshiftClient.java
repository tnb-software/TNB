package software.tnb.common.openshift;

import software.tnb.common.config.OpenshiftConfiguration;
import software.tnb.common.config.TestConfiguration;
import software.tnb.common.utils.HTTPUtils;
import software.tnb.common.utils.IOUtils;
import software.tnb.common.utils.MapUtils;
import software.tnb.common.utils.PropertiesUtils;
import software.tnb.common.utils.WaitUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cz.xtf.core.openshift.OpenShift;
import cz.xtf.core.openshift.OpenShiftBinary;
import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.api.model.ConfigMapBuilder;
import io.fabric8.kubernetes.api.model.Container;
import io.fabric8.kubernetes.api.model.ContainerBuilder;
import io.fabric8.kubernetes.api.model.ContainerPort;
import io.fabric8.kubernetes.api.model.DeletionPropagation;
import io.fabric8.kubernetes.api.model.LabelSelectorBuilder;
import io.fabric8.kubernetes.api.model.Namespace;
import io.fabric8.kubernetes.api.model.NamespaceBuilder;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.Probe;
import io.fabric8.kubernetes.api.model.Secret;
import io.fabric8.kubernetes.api.model.SecretBuilder;
import io.fabric8.kubernetes.api.model.ServiceAccount;
import io.fabric8.kubernetes.api.model.ServiceAccountBuilder;
import io.fabric8.kubernetes.api.model.StatusDetails;
import io.fabric8.kubernetes.api.model.Volume;
import io.fabric8.kubernetes.api.model.VolumeMount;
import io.fabric8.kubernetes.api.model.apps.DeploymentBuilder;
import io.fabric8.kubernetes.api.model.rbac.PolicyRuleBuilder;
import io.fabric8.kubernetes.api.model.rbac.Role;
import io.fabric8.kubernetes.api.model.rbac.RoleBindingBuilder;
import io.fabric8.kubernetes.api.model.rbac.RoleBuilder;
import io.fabric8.kubernetes.api.model.rbac.RoleRefBuilder;
import io.fabric8.kubernetes.api.model.rbac.SubjectBuilder;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ResourceNotFoundException;
import io.fabric8.kubernetes.client.dsl.RbacAPIGroupDSL;
import io.fabric8.kubernetes.client.dsl.base.ResourceDefinitionContext;
import io.fabric8.openshift.api.model.SecurityContextConstraints;
import io.fabric8.openshift.api.model.SecurityContextConstraintsBuilder;
import io.fabric8.openshift.api.model.operatorhub.v1.OperatorGroupBuilder;
import io.fabric8.openshift.api.model.operatorhub.v1alpha1.InstallPlan;
import io.fabric8.openshift.api.model.operatorhub.v1alpha1.Subscription;
import io.fabric8.openshift.api.model.operatorhub.v1alpha1.SubscriptionBuilder;
import io.fabric8.openshift.api.model.operatorhub.v1alpha1.SubscriptionConfig;
import io.fabric8.openshift.client.OpenShiftConfig;
import io.fabric8.openshift.client.OpenShiftConfigBuilder;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OpenshiftClient extends OpenShift {
    private static final Logger LOG = LoggerFactory.getLogger(OpenshiftClient.class);
    protected static OpenshiftClientWrapper clientWrapper;

    protected OpenshiftClient(OpenShiftConfig openShiftConfig) {
        super(openShiftConfig);
    }

    private static OpenshiftClient createInstance() {
        OpenShiftConfigBuilder configBuilder;

        if (OpenshiftConfiguration.openshiftUrl() != null) {
            configBuilder = new OpenShiftConfigBuilder()
                .withMasterUrl(OpenshiftConfiguration.openshiftUrl())
                .withUsername(OpenshiftConfiguration.openshiftUsername())
                .withPassword(OpenshiftConfiguration.openshiftPassword());
        } else if (OpenshiftConfiguration.openshiftKubeconfig() != null) {
            configBuilder = new OpenShiftConfigBuilder();
            LOG.info("Loading config from {}", OpenshiftConfiguration.openshiftKubeconfig());
            configBuilder.copyInstance(Config.fromKubeconfig(IOUtils.readFile(OpenshiftConfiguration.openshiftKubeconfig())));
        } else {
            LOG.info("Auto-configuring openshift client");
            configBuilder = new OpenShiftConfigBuilder().withAutoConfigure();
        }

        String namespace = OpenshiftConfiguration.openshiftNamespace();

        configBuilder
            .withNamespace(namespace)
            .withHttpsProxy(OpenshiftConfiguration.openshiftHttpsProxy())
            .withBuildTimeout(60_000L)
            .withRequestTimeout(120_000)
            .withConnectionTimeout(120_000)
            .withTrustCerts(true);

        LOG.info("Using cluster {}", configBuilder.getMasterUrl());

        return new OpenshiftClient(configBuilder.build());
    }

    private static OpenshiftClient init() {
        final OpenshiftClient c = OpenshiftClient.createInstance();
        c.createNamespace(c.getNamespace());
        if (OpenshiftConfiguration.openshiftNamespaceAutoSet()) {
            LOG.info("Setting {} as your default namespace in your `oc` configuration", c.getNamespace());
            new OpenShiftBinary("oc").project(c.getNamespace());
        }
        return c;
    }

    /**
     * Gets the openshift client.
     * <p>
     * Synchronized to ensure that in parallel runs the wrapper is initialized only once.
     *
     * @return openshift client instance
     */
    public static synchronized OpenshiftClient get() {
        if (clientWrapper == null) {
            // First test running will create the wrapper and others are reused
            clientWrapper = new OpenshiftClientWrapper(OpenshiftClient::init);
            LOG.debug("ocp version: {}", Optional.ofNullable(clientWrapper.getClient().getVersion())
                .map(v -> "%s.%s".formatted(v.getMajor(), v.getMinor())).orElse(""));
        } else if (clientWrapper.getClient() == null) {
            // This happens when a thread is reused - there was a test running in this thread and it closed and deleted the client, so re-init it
            clientWrapper.init();
        }
        return clientWrapper.getClient();
    }

    public String getOauthToken() {
        if (OpenshiftConfiguration.openshiftUrl() == null) {
            String token = OpenshiftClient.get().authorization().getConfiguration().getAutoOAuthToken();
            if (!token.isBlank()) {
                return token;
            } else {
                throw new IllegalStateException("Oauth token not found");
            }
        } else {
            return getTokenByUsernameAndPassword(OpenshiftConfiguration.openshiftUsername(),
                OpenshiftConfiguration.openshiftPassword(), OpenshiftConfiguration.openshiftUrl());
        }
    }

    private static String getTokenByUsernameAndPassword(String username, String password, String openshiftUrl) {
        try {
            String openshiftHost = new URI(openshiftUrl).getHost().replaceFirst("api.", "");
            OkHttpClient httpClient = new HTTPUtils.OkHttpClientBuilder().trustAllSslClient()
                .getInternalBuilder().followRedirects(false).build();
            Request request = new Request.Builder().get().url("https://oauth-openshift.apps."
                    + openshiftHost + "/oauth/authorize?response_type=token&client_id=openshift-challenging-client")
                .headers(Headers.of("Authorization",
                    "Basic " + Base64.getEncoder().encodeToString((username + ":" + password).getBytes())))
                .build();
            Response response = httpClient.newCall(request).execute();
            Pattern p = Pattern.compile("(?<=access_token=)[^&]+");
            Matcher m = p.matcher(new URI(response.headers().get("Location")).getFragment());
            if (m.find()) {
                return m.group(0);
            } else {
                throw new IllegalStateException("Oauth token not found");
            }
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Executes function in given namespace.
     *
     * @param ns namespace to use
     * @param function a function to execute
     * @return result of the function
     */
    public synchronized <T> T inNamespace(String ns, Function<OpenshiftClient, T> function) {
        OpenshiftClientWrapper wr = clientWrapper;
        if (!OpenshiftClient.get().getNamespace().equals(ns)) {
            // create new client for this namespaced operation
            try {
                // create deep copy of the client
                ObjectMapper objectMapper = new ObjectMapper();
                OpenShiftConfig cf = objectMapper
                    .readValue(objectMapper.writeValueAsString(get().config().getConfiguration()), OpenShiftConfig.class);
                cf.setNamespace(ns);
                clientWrapper = new OpenshiftClientWrapper(() -> new OpenshiftClient(cf));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(String.format("Can't create OpenShift client in namespace %s", ns), e);
            }
        }
        T result;
        try {
            result = function.apply(get());
        } finally {
            clientWrapper = wr;
        }
        return result;
    }

    /**
     * Creates the operatorgroup and subscription.
     *
     * @param channel operatorhub channel
     * @param operatorName operator name
     * @param source operator catalog source
     * @param subscriptionName name of the subscription
     */
    public void createSubscription(String channel, String operatorName, String source, String subscriptionName) {
        createSubscription(channel, operatorName, source, subscriptionName, "openshift-marketplace", get().getNamespace(),
            false);
    }

    /**
     * Creates the operatorgroup and subscription.
     *
     * @param channel operatorhub channel
     * @param operatorName operator name
     * @param source operator catalog source
     * @param subscriptionName name of the subscription
     * @param subscriptionNamespace namespace of the catalogsource
     */
    public void createSubscription(String channel, String operatorName, String source, String subscriptionName, String subscriptionNamespace) {
        createSubscription(channel, operatorName, source, subscriptionName, subscriptionNamespace, get().getNamespace(),
            false);
    }

    /**
     * Creates the operatorgroup and subscription.
     *
     * @param channel operatorhub channel
     * @param operatorName operator name
     * @param source operator catalog source
     * @param subscriptionName name of the subscription
     * @param subscriptionSourceNamespace namespace of the catalogsource
     * @param targetNamespace where the subscription should be created
     * @param clusterWide if the installation is clusterwide or not
     */
    public void createSubscription(String channel, String operatorName, String source, String subscriptionName,
        String subscriptionSourceNamespace, String targetNamespace, boolean clusterWide) {
        createSubscription(channel, operatorName, source, subscriptionName, subscriptionSourceNamespace, targetNamespace, clusterWide, null);
    }

    /**
     * Creates the operatorgroup and subscription.
     *
     * @param channel operatorhub channel
     * @param operatorName operator name
     * @param source operator catalog source
     * @param subscriptionName name of the subscription
     * @param subscriptionSourceNamespace namespace of the catalogsource
     * @param targetNamespace where the subscription should be created
     * @param clusterWide if the installation is clusterwide or not
     * @param startingWithCSV the starting CSV version
     */
    public void createSubscription(String channel, String operatorName, String source, String subscriptionName,
        String subscriptionSourceNamespace, String targetNamespace, boolean clusterWide, String startingWithCSV) {
        createSubscription(channel, operatorName, source, subscriptionName, subscriptionSourceNamespace, targetNamespace, clusterWide,
            startingWithCSV, null);
    }

    /**
     * Creates the operatorgroup and subscription.
     *
     * @param channel operatorhub channel
     * @param operatorName operator name
     * @param source operator catalog source
     * @param subscriptionName name of the subscription
     * @param subscriptionSourceNamespace namespace of the catalogsource
     * @param targetNamespace where the subscription should be created
     * @param clusterWide if the installation is clusterwide or not
     * @param startingWithCSV the starting CSV version
     * @param config subscription config specification
     */
    public void createSubscription(String channel, String operatorName, String source, String subscriptionName,
        String subscriptionSourceNamespace, String targetNamespace, boolean clusterWide, String startingWithCSV, SubscriptionConfig config) {
        LOG.info("Creating subcription with name \"{}\", for operator \"{}\", channel \"{}\", catalog source \"{}\" from \"{}\" namespace,"
                + " with \"{}\" target namespace, is clusterWide: \"{}\", with \"{}\" starting CSV version, with additional config: \"{}\"",
            subscriptionName, operatorName, channel, source, subscriptionSourceNamespace, targetNamespace, clusterWide, startingWithCSV, config);
        // There can be only one operatorgroup in the namespace, otherwise the new operatorhub deployments complain about multiple operatorgroups
        // in the namespace
        if (get().operatorHub().operatorGroups().inNamespace(targetNamespace).list().getItems().size() == 0) {
            LOG.debug("Creating operator group {}", subscriptionName);
            final OperatorGroupBuilder operatorGroupBuilder = new OperatorGroupBuilder()
                .withNewMetadata()
                .withName(subscriptionName)
                .endMetadata();
            if (!clusterWide) {
                operatorGroupBuilder
                    .withNewSpec()
                    .withTargetNamespaces(targetNamespace)
                    .endSpec();
            }
            get().operatorHub().operatorGroups().inNamespace(targetNamespace).createOrReplace(operatorGroupBuilder.build());
        }

        Subscription s = new SubscriptionBuilder()
            .editOrNewMetadata()
            .withName(subscriptionName)
            .endMetadata()
            .withNewSpec()
            .withName(operatorName)
            .withChannel(channel)
            .withSource(source)
            .withSourceNamespace(subscriptionSourceNamespace)
            .withStartingCSV(startingWithCSV)
            .withConfig(config)
            .endSpec()
            .build();
        get().operatorHub().subscriptions().inNamespace(targetNamespace).createOrReplace(s);
    }

    /**
     * Waits until the install plan for a given subscription completes.
     *
     * @param subscriptionName subscription name
     */
    public void waitForInstallPlanToComplete(String subscriptionName) {
        waitForInstallPlanToComplete(subscriptionName, get().getNamespace());
    }

    /**
     * Waits until the install plan for a given subscription completes.
     *
     * @param subscriptionName subscription name
     * @param targetNamespace subscription namespace
     */
    public void waitForInstallPlanToComplete(String subscriptionName, String targetNamespace) {
        WaitUtils.waitFor(() -> {
            Subscription subscription = get().operatorHub().subscriptions().inNamespace(targetNamespace).withName(subscriptionName).get();
            if (subscription == null || subscription.getStatus() == null || subscription.getStatus().getInstallplan() == null) {
                return false;
            }
            String ipName = subscription.getStatus().getInstallplan().getName();
            InstallPlan installPlan = get().operatorHub().installPlans().inNamespace(targetNamespace).withName(ipName).get();
            if (installPlan == null || installPlan.getStatus() == null || installPlan.getStatus().getPhase() == null) {
                return false;
            }
            return installPlan.getStatus().getPhase().equalsIgnoreCase("complete");
        }, 50, 5000L, String.format("Waiting until the install plan from subscription %s is complete", subscriptionName));
    }

    /**
     * Deletes the operatorhub subscription.
     *
     * @param name subscription name
     */
    public void deleteSubscription(String name) {
        deleteSubscription(name, get().getNamespace());
    }

    /**
     * Deletes the operatorhub subscription in given namespace.
     *
     * @param name subscription name
     * @param namespace subscription namespace
     */
    public void deleteSubscription(String name, String namespace) {
        LOG.info("Deleting subscription {} in namespace {}", name, namespace);
        Subscription subscription = get().operatorHub().subscriptions().inNamespace(namespace).withName(name).get();
        if (subscription == null) {
            // Avoid NPE when you have the operator installed manually through UI
            LOG.warn("Unable to find subscription {} in {} namespace, skipping delete", name, namespace);
            return;
        }
        String csvName = subscription.getStatus().getCurrentCSV();
        // CSV being null can happen if you delete the subscription without deleting the CSV, then your new subscription is CSV-less
        if (csvName != null) {
            get().operatorHub().clusterServiceVersions().inNamespace(namespace).withName(csvName).delete();
        }
        get().operatorHub().subscriptions().inNamespace(namespace).withName(name).delete();
    }

    /**
     * Waits until the image stream is populated with given tag.
     *
     * @param name imagestream name
     * @param tag imagestream tag
     */
    public void waitForImageStream(String name, String tag) {
        WaitUtils.waitFor(() -> get().imageStreams().withName(name).get().getSpec().getTags().stream()
            .anyMatch(t -> tag.equals(t.getName())), 24, 5000L, String.format("Waiting until the imagestream %s contains %s tag", name, tag));
    }

    /**
     * Starts a new s2i build from a given file.
     *
     * @param name buildconfig name
     * @param filePath path to the file
     */
    public void doS2iBuild(String name, Path filePath) {
        LOG.info("Instantiating a new build for buildconfig {} from file {}", name, filePath.toAbsolutePath());
        get().buildConfigs().withName(name).instantiateBinary().fromFile(filePath.toFile());

        BooleanSupplier success = () -> "complete".equalsIgnoreCase(get()
            .getBuild(name + "-" + get().getBuildConfig(name).getStatus().getLastVersion()).getStatus().getPhase());
        BooleanSupplier fail = () -> "failed".equalsIgnoreCase(get()
            .getBuild(name + "-" + get().getBuildConfig(name).getStatus().getLastVersion()).getStatus().getPhase());

        WaitUtils.waitFor(success, fail, 5000L, "Waiting until the build completes");
    }

    /**
     * Create namespace with given name.
     *
     * @param name of namespace to be created
     */
    public void createNamespace(String name) {
        if ((name == null) || (name.isEmpty())) {
            LOG.info("Skipped creating namespace, name null or empty");
            return;
        }

        final Namespace namespace = this.namespaces().withName(name).get();
        if (namespace != null && namespace.getMetadata().getDeletionTimestamp() != null) {
            throw new RuntimeException("Namespace " + name + " already exists and is in Terminating state");
        }

        // @formatter:off
        Map<String, String> labels = TestConfiguration.user() == null ? Map.of() : Map.of("tnb/createdBy", TestConfiguration.user());
        Namespace ns = new NamespaceBuilder()
            .withNewMetadata()
                .withName(name)
                .withLabels(labels)
            .endMetadata().build();
        // @formatter:on
        if (this.namespaces().withName(name).get() == null) {
            this.namespaces().create(ns);
            WaitUtils.waitFor(() -> this.namespaces().withName(name).get() != null, "Waiting until the namespace " + name + " is created");
        } else {
            LOG.info("Skipped creating namespace " + name + ", already exists");
        }
    }

    /**
     * Deletes the current namespace (associated with the openshift client in this thread).
     * <p>
     * Method is static to avoid using OpenshiftClient.get() that would force creating a new instance if the client is null.
     * <p>
     * There is a valid case where the client would be null - when multiple extensions are used in one test class
     */
    public static void deleteNamespace() {
        if (clientWrapper != null && clientWrapper.getClient() != null) {
            deleteNamespace(get().getNamespace());
            // If the current namespace is deleted also close the client
            clientWrapper.closeClient();
        }
    }

    /**
     * Delete namespace of given name.
     *
     * @param name of namespace to be deleted
     */
    public static void deleteNamespace(String name) {
        if ((name == null) || (name.isEmpty())) {
            LOG.info("Skipped deleting namespace, name null or empty");
            return;
        }
        if (get().namespaces().withName(name).get() == null) {
            LOG.info("Skipped deleting namespace " + name + ", not found");
        } else {
            get().namespaces().withName(name).cascading(true).delete();
            WaitUtils.waitFor(() -> get().namespaces().withName(name).get() == null, 60, 5000L, "Waiting until the namespace is removed");
            LOG.info("Deleted namespace " + name);
        }
    }

    /**
     * Creates a config map with given name and data.
     *
     * @param name configmap name
     * @param data map with data
     * @return created configmap instance
     */
    public ConfigMap createConfigMap(String name, Map<String, String> data) {
        return get().configMaps().withName(name).createOrReplace(
            new ConfigMapBuilder()
                .withNewMetadata()
                .withName(name)
                .endMetadata()
                .withData(data)
                .build()
        );
    }

    /**
     * Get log of pod (alternative to OpenShift instance method getPodLog()).
     *
     * @param p the pod
     * @return log of the pod
     */
    public String getLogs(Pod p) {
        return get().pods().withName(p.getMetadata().getName()).inContainer(getIntegrationContainer(p)).getLog();
    }

    /**
     * Creates a secret from given properties, wrappen into a application.properties key.
     *
     * @param name of secret to be created
     * @param credentials of service from credentials.yaml
     * @param labels default null
     * @param prefix default null, prefix which should be prepended to keys in credentials ([prefix]key=value)
     * @return created secret
     */
    public Secret createApplicationPropertiesSecret(String name, Properties credentials, Map<String, String> labels, String prefix) {
        String credentialsString = PropertiesUtils.toString(credentials, Optional.ofNullable(prefix).orElse(""));
        String dataFileName = (name.contains(".")) ? name.substring(0, name.indexOf(".")) : name;
        Secret secret = new SecretBuilder()
            .withStringData(Collections.singletonMap(dataFileName + ".properties", credentialsString)).withNewMetadata()
            .withName(name)
            .addToLabels(labels)
            .endMetadata().build();
        return get().secrets().createOrReplace(secret);
    }

    public void deleteSecret(String name) {
        get().secrets().withName(name).delete();
    }

    public String getClusterHostname(String service) {
        return String.format("%s.%s.svc.cluster.local", service, get().getNamespace());
    }

    public boolean isPodFailed(Pod pod) {
        try {
            return pod.getStatus().getContainerStatuses().stream()
                .anyMatch(p -> "error".equalsIgnoreCase(p.getState().getTerminated().getReason())
                    || "error".equalsIgnoreCase(p.getLastState().getTerminated().getReason()));
        } catch (Exception ignored) {
            return false;
        }
    }

    /**
     * Gets the integration container name. In case of multiple containers in a pod, the one with the name "integration" is returned.
     *
     * @param pod pod
     * @return container name
     */
    public String getIntegrationContainer(Pod pod) {
        String container;
        List<Container> containerList = getAllContainers(pod);
        if (containerList.size() > 1) {
            Optional<Container> integrationContainer = containerList.stream().filter(c -> "integration".equalsIgnoreCase(c.getName())).findFirst();
            if (integrationContainer.isEmpty()) {
                throw new RuntimeException("There were multiple containers in pod and \"integration\" container was not present");
            } else {
                container = integrationContainer.get().getName();
            }
        } else {
            container = containerList.get(0).getName();
        }
        return container;
    }

    public SecurityContextConstraints createSecurityContext(String sccName, String copyFromScc, String... defaultCapabilities) {
        SecurityContextConstraints scc = get().securityContextConstraints().withName(sccName).get();
        if (scc == null) {
            SecurityContextConstraints existingScc = get().securityContextConstraints().withName(copyFromScc).get();
            scc = get().securityContextConstraints().create(
                new SecurityContextConstraintsBuilder(existingScc)
                    .withNewMetadata() // new metadata to override the existing annotations
                    .withName(sccName)
                    .endMetadata()
                    .addToDefaultAddCapabilities(defaultCapabilities)
                    .build());
        }
        return scc;
    }

    public void addUsersToSecurityContext(SecurityContextConstraints scc, String... users) {
        for (String user : users) {
            if (!scc.getUsers().contains(user)) {
                scc.getUsers().add(user);
            }
        }
        get().securityContextConstraints().withName(scc.getMetadata().getName()).patch(scc);
    }

    public void addGroupsToSecurityContext(SecurityContextConstraints scc, String... groups) {
        for (String group : groups) {
            if (!scc.getGroups().contains(group)) {
                scc.getGroups().add(group);
            }
        }
        get().securityContextConstraints().withName(scc.getMetadata().getName()).patch(scc);
    }

    public String getServiceAccountRef(String serviceAccountName) {
        return "system:serviceaccount:" + get().getNamespace() + ":" + serviceAccountName;
    }

    public boolean hasLabels(Pod pod, Map<String, String> expectedLabels) {
        final Map<String, String> labels = pod.getMetadata().getLabels();
        for (Map.Entry<String, String> expected : expectedLabels.entrySet()) {
            if (!labels.containsKey(expected.getKey()) || !labels.get(expected.getKey()).equals(expected.getValue())) {
                return false;
            }
        }
        return true;
    }

    /**
     * #see {@link #createDeployment(Map, Consumer)}
     * @param config map config
     */
    public void createDeployment(Map<String, Object> config) {
        createDeployment(config, builder -> {
        });
    }

    /**
     * Creates an deployment based on the configuration in the given map.
     * <p>
     * The supported key-value pair in the map are:
     * - name - string - deployment name
     * - image - string - docker image to use
     * - env - map of string,string - environment variables
     * - ports - list of {@link ContainerPort} - ports exposed in the deployment
     * - readinessProbe - {@link Probe}
     * - livenessProbe - {@link Probe}
     * - serviceAccount - string - service account associated with the deployment
     * - volumes - list of {@link Volume}
     * - volumeMounts - list of {@link VolumeMount}
     * - capabilities - list of string - capabilities to add to the deployment
     * - scc - string - security constraint
     * - args - list of string - command to run in the pod
     * <p>
     * You can use the customization parameter to access the deployment builder to perform modifications. You always need to "end" all "opened" paths
     * in the builder, so that the code returns the DeploymentBuilder object (e.g. builder.editSpec().editTemplate().endTemplate().endSpec()),
     * otherwise the customization won't work.
     * @param config map of configuration
     * @param customization deploymentbuilder consumer for other modifications
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void createDeployment(Map<String, Object> config, Consumer<DeploymentBuilder> customization) {
        final String name = (String) config.get("name");
        final String image = (String) config.get("image");
        Map<String, String> env = Map.of();
        if (config.get("env") != null) {
            env = (Map) config.get("env");
        }
        List<ContainerPort> ports = Collections.emptyList();
        if (config.get("ports") != null) {
            ports = (List) config.get("ports");
        }
        Probe readinessProbe = null;
        if (config.get("readinessProbe") != null) {
            readinessProbe = (Probe) config.get("readinessProbe");
        }
        Probe livenessProbe = null;
        if (config.get("livenessProbe") != null) {
            livenessProbe = (Probe) config.get("livenessProbe");
        }
        String serviceAccount = null;
        if (config.get("serviceAccount") != null) {
            serviceAccount = (String) config.get("serviceAccount");
        }
        List<Volume> volumes = Collections.emptyList();
        if (config.get("volumes") != null) {
            volumes = (List) config.get("volumes");
        }
        List<VolumeMount> volumeMounts = Collections.emptyList();
        if (config.get("volumeMounts") != null) {
            volumeMounts = (List) config.get("volumeMounts");
        }
        List<String> capabilities = Collections.emptyList();
        if (config.get("capabilities") != null) {
            capabilities = (List) config.get("capabilities");
        }
        Map<String, String> scc = Collections.emptyMap();
        if (config.get("scc") != null) {
            scc = Map.of("openshift.io/scc", (String) config.get("scc"));
        }
        List<String> args = Collections.emptyList();
        if (config.get("args") != null) {
            args = (List) config.get("args");
        }

        Map<String, String> labels = Map.of(OpenshiftConfiguration.openshiftDeploymentLabel(), name);
        // @formatter:off
        DeploymentBuilder builder = new DeploymentBuilder()
            .withNewMetadata()
                .withName(name)
                .withLabels(labels)
                .addToAnnotations(scc)
            .endMetadata()
            .withNewSpec()
                .withSelector(new LabelSelectorBuilder().withMatchLabels(labels).build())
                .withReplicas(1)
                .withNewTemplate()
                    .withNewMetadata()
                        .withLabels(labels)
                    .endMetadata()
                    .withNewSpec()
                        .withServiceAccount(serviceAccount)
                       .withVolumes(volumes)
                       .withContainers(
                            new ContainerBuilder()
                                .withName(name)
                                .withImage(image)
                                .withImagePullPolicy("IfNotPresent")
                                .withEnv(MapUtils.toEnvVars(env))
                                .withArgs(args)
                                .withPorts(ports)
                                .withLivenessProbe(livenessProbe)
                                .withReadinessProbe(readinessProbe)
                                .withVolumeMounts(volumeMounts)
                                .withNewSecurityContext()
                                    .withNewCapabilities()
                                        .addAllToAdd(capabilities)
                                    .endCapabilities()
                                .endSecurityContext()
                                .build()
                            )
                    .endSpec()
                .endTemplate()
            .endSpec();
        // @formatter:on
        customization.accept(builder);

        LOG.debug("Creating deployment {}", name);
        get().apps().deployments().resource(builder.build()).serverSideApply();
    }

    /**
     * Delete custom resource
     * @param group String, name of the resource group
     * @param version String, name of the resource version
     * @param kind String, name of the resource kind
     * @param resourceName String, resource name
     */
    public void deleteCustomResource(String group, String version, String kind, String resourceName) {
        deleteCustomResource(new ResourceDefinitionContext.Builder()
            .withGroup(group)
            .withVersion(version)
            .withKind(kind)
            .build(), resourceName);
    }

    /**
     * Delete custom resource
     * @param resourceContext ResourceDefinitionContext, custom resource context
     * @param resourceName String, resource name
     */
    public void deleteCustomResource(ResourceDefinitionContext resourceContext, String resourceName) {
        final String namespace = get().getNamespace();
        LOG.debug("deleting resource {}/{}", resourceContext.getKind(), resourceName);
        final List<StatusDetails> status = get().resource(get().genericKubernetesResources(resourceContext)
                        .inNamespace(namespace)
                        .list().getItems().stream()
                        .filter(r -> resourceName.equals(r.getMetadata().getName()))
                        .findFirst()
                        .orElseThrow(() -> new ResourceNotFoundException("the resource with name "
                                + resourceName +  " was not found")))
                .withPropagationPolicy(DeletionPropagation.FOREGROUND).delete();
        Optional.ofNullable(status).orElseGet(List::of)
                .forEach(statusDetails -> LOG.debug("deleted {}/{}/{}", statusDetails.getGroup()
                        , statusDetails.getKind()
                    , statusDetails.getName()));
    }

    /**
     * Create a service account in the current namespace
     * @param serviceAccountName String, the name of the service account
     * @return ServiceAccount created
     */
    public ServiceAccount createServiceAccount(String serviceAccountName) {
        LOG.debug("creating service account {}", serviceAccountName);
        return get().serviceAccounts().inNamespace(get().getNamespace()).resource(new ServiceAccountBuilder()
            .withNewMetadata().withName(serviceAccountName).endMetadata()
            .build()).serverSideApply();
    }

    /**
     * Delete a service account in the current namespace
     * @param serviceAccountName String, the name of the service account
     */
    public void deleteServiceAccount(String serviceAccountName) {
        LOG.debug("deleting service account {}", serviceAccountName);
        get().serviceAccounts().inNamespace(get().getNamespace()).resource(new ServiceAccountBuilder()
            .withNewMetadata().withName(serviceAccountName).endMetadata()
            .build()).delete();
    }

    /**
     * Retrieve the base64 encoded token for a service account
     * @param serviceAccountName String, the name of the service account
     * @return String, the base64 encoded auth token
     */
    public String getServiceAccountAuthToken(final String serviceAccountName) {
        LOG.debug("get service account token for {}", serviceAccountName);
        final String secret = get().serviceAccounts().inNamespace(get().getNamespace()).resource(new ServiceAccountBuilder()
            .withNewMetadata().withName(serviceAccountName).endMetadata()
            .build()).get().getSecrets().get(0).getName();
        ObjectMapper objectMapper = new ObjectMapper();
        final String token;
        try {
            Map<String, Object> secretData = objectMapper.readValue(Base64.getDecoder().decode(get().secrets()
                .inNamespace(get().getNamespace()).withName(secret).get()
                .getData().get(".dockercfg")),  new TypeReference<Map<String, Object>>() { });
            Map<String, Object> authMap = (Map<String, Object>) secretData.entrySet().stream()
                .filter(e -> e.getKey().contains("image-registry"))
                .map(Map.Entry::getValue)
                .findFirst().orElseThrow(() -> new RuntimeException("unable to read token for " + serviceAccountName));
            token = new String(Base64.getDecoder().decode(((String) authMap.get("auth")).getBytes(StandardCharsets.UTF_8)))
                .substring("<token>:".length());
        } catch (IOException e) {
            throw new RuntimeException("unable to read secret " + secret + " for service account " + serviceAccountName, e);
        }
        return token;
    }

    /**
     * Wait for PODs with labels are ready
     * @param labels Map of label to create the pod filter
     * @param expectedPodNumber int, the expected pod number matching the labels
     * @param waitSeconds int, seconds to wait for each POD to be ready, it is used also to wait for the expected pods are available
     */
    public void waitUntilThePodsAreReady(Map<String, String> labels, int expectedPodNumber, int waitSeconds) {

        final Predicate<Pod> podPredicate = pod -> get().hasLabels(pod, labels);

        WaitUtils.waitFor(() ->
            get().pods().inNamespace(get().getNamespace())
                .list().getItems().stream().filter(podPredicate).count() == expectedPodNumber
        , waitSeconds, 1000L, String.format("Waiting until the expected pod number is %s", expectedPodNumber));

        LOG.debug("waiting for all pods are ready");
        get().pods().inNamespace(get().getNamespace())
            .list().getItems().stream().filter(podPredicate).toList()
            .forEach(pod -> get().pods().inNamespace(pod.getMetadata().getNamespace())
                .withName(pod.getMetadata().getName()).waitUntilReady(waitSeconds, TimeUnit.SECONDS));
        LOG.debug("all pods are ready");
    }

    /**
     * Apply on server side from classpath resource
     * @param classPathResource String, the path of the resource to apply (i.e. /software/tnb/mycrd.yaml)
     * @param replacements Map, if provided, all the keys will be replaced by the relative values
     * @param inNamespace boolean, if force to be executed in the current namespace
     */
    public void serverSideApply(String classPathResource, Map<String, String> replacements, boolean inNamespace) {
        try (InputStream crRes = OpenshiftClient.class.getResourceAsStream(classPathResource)) {
            final String namespace = get().getNamespace();
            AtomicReference<String> content = new AtomicReference<>(org.apache.commons.io.IOUtils.toString(crRes, StandardCharsets.UTF_8));

            if (replacements != null) {
                replacements.forEach((key, value) -> content.set(content.get().replaceAll(key, value)));
            }
            if (inNamespace) {
                get().inNamespace(namespace).resource(content.get()).serverSideApply();
            } else {
                get().resource(content.get()).serverSideApply();
            }
        } catch (IOException ex) {
            throw new RuntimeException("unable to load " + classPathResource + " resource");
        }
    }

    /**
     * Overload of {@link OpenshiftClient#serverSideApply(String, Map, boolean)}
     * @param classPathResource String, the path of the resource to apply (i.e. /software/tnb/mycrd.yaml)
     */
    public void serverSideApply(String classPathResource) {
        serverSideApply(classPathResource, null, true);
    }

    /**
     * Check if pods with some label are in ready state
     * @param namespace String, the namespace to check PODs
     * @param labelKey String, label key
     * @param labelValue String, label value
     * @return boolean, if the pods are found and if the status is ready for all matching pods
     */
    public boolean arePodsWithLabelReady(String namespace, String labelKey, String labelValue) {
        return OpenshiftClient.get().pods().inNamespace(namespace).withLabel(labelKey, labelValue)
            .list().getItems().stream()
            .filter(pod -> pod.getStatus() != null)
            .filter(pod -> pod.getStatus().getConditions() != null)
            .allMatch(pod -> pod.getStatus().getConditions().stream()
                .anyMatch(c -> "Ready".equals(c.getType()) && "True".equals(c.getStatus())));
    }

    /**
     * Return the URL of the console
     * @return String, the console URL
     */
    public String getConsoleUrl() {
        return "https://%s".formatted(OpenshiftClient.get().routes().inNamespace("openshift-console").withName("console")
            .get().getSpec().getHost());
    }

    /**
     * Creates a role and assign it to the service account
     * @param namespace String, the namespace for the role
     * @param apiGroups List, the api groups of the resource for the role
     * @param resources List, the resources of the role
     * @param verbs List, the verbs for the resources
     * @param name String the name of the new role
     * @param serviceAccountName String the service account associated with the role using role binding
     */
    public void createRole(final String namespace, final List<String> apiGroups, final List<String> resources, final List<String> verbs
        , final String name, final String serviceAccountName) {
        RbacAPIGroupDSL rbac = OpenshiftClient.get().rbac();
        final Role role = rbac.roles().inNamespace(namespace)
            .resource(new RoleBuilder()
                .withNewMetadata()
                .withName(name)
                .withNamespace(namespace)
                .endMetadata()
                .addToRules(new PolicyRuleBuilder()
                    .withApiGroups(apiGroups)
                    .withResources(resources)
                    .withVerbs(verbs).build())
                .build()).create();

        rbac.roleBindings().inNamespace(namespace)
            .resource(new RoleBindingBuilder()
                .withNewMetadata()
                .withName(role.getMetadata().getName())
                .withNamespace(namespace)
                .endMetadata()
                .addToSubjects(new SubjectBuilder()
                    .withNamespace(namespace)
                    .withKind("ServiceAccount")
                    .withName(serviceAccountName).build())
                .withRoleRef(new RoleRefBuilder()
                    .withName(role.getMetadata().getName())
                    .withKind("Role")
                    .withApiGroup("rbac.authorization.k8s.io")
                    .build())
                .build()).create();
    }

    /**
     * overload of {@link #createRole(String, List, List, List, String, String)} using current namespace
     */
    public void createRole(final List<String> apiGroups, final List<String> resources, final List<String> verbs
        , final String name, final String serviceAccountName) {
        createRole(get().getNamespace(), apiGroups, resources, verbs, name, serviceAccountName);
    }

}
