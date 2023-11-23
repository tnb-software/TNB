package software.tnb.common.openshift;

import software.tnb.common.config.OpenshiftConfiguration;
import software.tnb.common.config.TestConfiguration;
import software.tnb.common.utils.HTTPUtils;
import software.tnb.common.utils.IOUtils;
import software.tnb.common.utils.PropertiesUtils;
import software.tnb.common.utils.WaitUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cz.xtf.core.openshift.OpenShift;
import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.api.model.ConfigMapBuilder;
import io.fabric8.kubernetes.api.model.Container;
import io.fabric8.kubernetes.api.model.Namespace;
import io.fabric8.kubernetes.api.model.NamespaceBuilder;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.Secret;
import io.fabric8.kubernetes.api.model.SecretBuilder;
import io.fabric8.kubernetes.client.Config;
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
            configBuilder = new OpenShiftConfigBuilder(
                new OpenShiftConfig(Config.fromKubeconfig(IOUtils.readFile(OpenshiftConfiguration.openshiftKubeconfig()))));
        } else {
            LOG.info("Auto-configuring openshift client");
            configBuilder = new OpenShiftConfigBuilder(new OpenShiftConfig(OpenShiftConfig.autoConfigure(null)));
        }

        String namespace = OpenshiftConfiguration.openshiftNamespace();

        configBuilder
            .withNamespace(namespace)
            .withHttpsProxy(OpenshiftConfiguration.openshiftHttpsProxy())
            .withBuildTimeout(60_000)
            .withRequestTimeout(120_000)
            .withConnectionTimeout(120_000)
            .withTrustCerts(true);

        LOG.info("Using cluster {}", configBuilder.getMasterUrl());

        return new OpenshiftClient(configBuilder.build());
    }

    private static OpenshiftClient init() {
        final OpenshiftClient c = OpenshiftClient.createInstance();
        c.createNamespace(c.getNamespace());
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
        } else if (clientWrapper.getClient() == null) {
            // This happens when a thread is reused - there was a test running in this thread and it closed and deleted the client, so re-init it
            clientWrapper.init();
        }
        return clientWrapper.getClient();
    }

    public String getOauthToken() {
        if (OpenshiftConfiguration.openshiftUrl() == null) {
            return OpenshiftClient.get().authorization().getConfiguration().getOauthToken();
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
        LOG.info("Creating subcription with name \"{}\", for operator \"{}\", channel \"{}\", catalog source \"{}\" from \"{}\" namespace",
            subscriptionName, operatorName, channel, source, subscriptionSourceNamespace);
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
            WaitUtils.waitFor(() -> get().namespaces().withName(name).get() == null, "Waiting until the namespace is removed");
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
}
