package org.jboss.fuse.tnb.common.openshift;

import org.jboss.fuse.tnb.common.config.OpenshiftConfiguration;
import org.jboss.fuse.tnb.common.utils.MapUtils;
import org.jboss.fuse.tnb.common.utils.WaitUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.function.BooleanSupplier;

import cz.xtf.core.openshift.OpenShift;
import io.fabric8.kubernetes.api.model.ConfigMapBuilder;
import io.fabric8.kubernetes.api.model.Container;
import io.fabric8.kubernetes.api.model.Namespace;
import io.fabric8.kubernetes.api.model.NamespaceBuilder;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.Secret;
import io.fabric8.kubernetes.api.model.SecretBuilder;
import io.fabric8.kubernetes.client.dsl.ContainerResource;
import io.fabric8.kubernetes.client.dsl.PodResource;
import io.fabric8.openshift.api.model.operatorhub.v1.OperatorGroup;
import io.fabric8.openshift.api.model.operatorhub.v1.OperatorGroupBuilder;
import io.fabric8.openshift.api.model.operatorhub.v1alpha1.InstallPlan;
import io.fabric8.openshift.api.model.operatorhub.v1alpha1.Subscription;
import io.fabric8.openshift.api.model.operatorhub.v1alpha1.SubscriptionBuilder;
import io.fabric8.openshift.client.OpenShiftConfig;
import io.fabric8.openshift.client.OpenShiftConfigBuilder;

public class OpenshiftClient extends OpenShift {
    private static final Logger LOG = LoggerFactory.getLogger(OpenshiftClient.class);
    private static OpenshiftClient client;

    static {
        LOG.info("Creating new OpenShift client");
        OpenShiftConfig openShiftConfig = new OpenShiftConfigBuilder()
            .withMasterUrl(OpenshiftConfiguration.openshiftUrl())
            .withTrustCerts(true)
            .withNamespace(OpenshiftConfiguration.openshiftNamespace())
            .withUsername(OpenshiftConfiguration.openshiftUsername())
            .withPassword(OpenshiftConfiguration.openshiftPassword())
            .build();
        client = new OpenshiftClient(openShiftConfig);
    }

    private OpenshiftClient(OpenShiftConfig openShiftConfig) {
        super(openShiftConfig);
    }

    public static OpenshiftClient get() {
        return client;
    }

    /**
     * Creates the operatorgroup and subscription.
     *
     * @param channel operatorhub channel
     * @param operatorName operator name
     * @param source operator catalog source
     * @param subscriptionName name of the subscription
     */
    public static void createSubscription(String channel, String operatorName, String source, String subscriptionName,
        String subscriptionSourceNamespace) {
        LOG.info("Creating subcription with name {}, for operator {}, channel {}, source {} in {}", subscriptionName, operatorName, channel, source,
            subscriptionSourceNamespace);
        LOG.debug("Creating operator group {}", subscriptionName);
        if (get().operatorHub().operatorGroups().inNamespace(OpenshiftConfiguration.openshiftNamespace()).
            list().getItems().size() == 0) {
            OperatorGroup og = new OperatorGroupBuilder()
                .withNewMetadata()
                .withName(subscriptionName)
                .endMetadata()
                .withNewSpec()
                .withTargetNamespaces(OpenshiftConfiguration.openshiftNamespace())
                .endSpec()
                .build();
            client.operatorHub().operatorGroups().createOrReplace(og);
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
            .endSpec()
            .build();
        client.operatorHub().subscriptions().createOrReplace(s);
    }

    /**
     * Waits until the install plan for a given subscription completes.
     *
     * @param name subscription name
     */
    public static void waitForCompletion(String name) {
        WaitUtils.waitFor(() -> {
            Subscription subscription = get().operatorHub().subscriptions().withName(name).get();
            if (subscription == null || subscription.getStatus() == null || subscription.getStatus().getInstallplan() == null) {
                return false;
            }
            String ipName = subscription.getStatus().getInstallplan().getName();
            InstallPlan installPlan = get().operatorHub().installPlans().withName(ipName).get();
            if (installPlan == null || installPlan.getStatus() == null || installPlan.getStatus().getPhase() == null) {
                return false;
            }
            return installPlan.getStatus().getPhase().equalsIgnoreCase("complete");
        }, 60, 5000L, String.format("Waiting until the install plan from subscription %s is complete", name));
    }

    /**
     * Deletes the operatorgroup subscription.
     *
     * @param name subscription name
     */
    public static void deleteSubscription(String name) {
        LOG.info("Deleting subscription {}", name);
        Subscription subscription = get().operatorHub().subscriptions().withName(name).get();
        String csvName = subscription.getStatus().getCurrentCSV();
        //CSV being null can happen if you delete the subscription without deleting the CSV, then your new subscription is CSV-less
        if (csvName != null) {
            get().operatorHub().clusterServiceVersions().withName(csvName).delete();
        }
        get().operatorHub().subscriptions().withName(name).delete();
    }

    /**
     * Waits until the image stream is populated with given tag.
     *
     * @param name imagestream name
     * @param tag imagestream tag
     */
    public static void waitForImageStream(String name, String tag) {
        WaitUtils.waitFor(() -> OpenshiftClient.get().imageStreams().withName(name).get().getSpec().getTags().stream()
            .anyMatch(t -> tag.equals(t.getName())), 24, 5000L, String.format("Waiting until the imagestream %s contains %s tag", name, tag));
    }

    /**
     * Starts a new s2i build from a given file.
     *
     * @param name buildconfig name
     * @param filePath path to the file
     */
    public static void doS2iBuild(String name, Path filePath) {
        LOG.info("Instantiating a new build for buildconfig {} from file {}", name, filePath.toAbsolutePath());
        OpenshiftClient.get().buildConfigs().withName(name).instantiateBinary().fromFile(filePath.toFile());

        BooleanSupplier success = () -> "complete".equalsIgnoreCase(OpenshiftClient.get()
            .getBuild(name + "-" + OpenshiftClient.get().getBuildConfig(name).getStatus().getLastVersion()).getStatus().getPhase());
        BooleanSupplier fail = () -> "failed".equalsIgnoreCase(OpenshiftClient.get()
            .getBuild(name + "-" + OpenshiftClient.get().getBuildConfig(name).getStatus().getLastVersion()).getStatus().getPhase());

        LOG.info("Waiting until the build completes");
        WaitUtils.waitFor(success, fail, 5000L, "Waiting until the build completes");
    }

    /**
     * Create namespace (name obtained from system property openshift.namespace)
     */
    public static void createNamespace() {
        createNamespace(OpenshiftConfiguration.openshiftNamespace());
    }

    /**
     * Create namespace with given name
     *
     * @param name of namespace to be created
     */
    public static void createNamespace(String name) {
        if ((name == null) || (name.isEmpty())) {
            LOG.info("Skipped creating namespace, name null or empty");
            return;
        }
        Namespace ns = new NamespaceBuilder().withNewMetadata().withName(name).endMetadata().build();
        if (client.namespaces().withName(name).get() == null) {
            client.namespaces().create(ns);
            LOG.info("Created namespace " + name);
        } else {
            LOG.info("Skipped creating namespace " + name + ", already exists");
        }
    }

    /**
     * Delete namespace (name obtained from system property openshift.namespace)
     */
    public static void deleteNamespace() {
        deleteNamespace(OpenshiftConfiguration.openshiftNamespace());
    }

    /**
     * Delete namespace of given name
     *
     * @param name of namespace to be deleted
     */
    public static void deleteNamespace(String name) {
        if ((name == null) || (name.isEmpty())) {
            LOG.info("Skipped deleting namespace, name null or empty");
            return;
        }
        if (client.namespaces().withName(name).get() == null) {
            LOG.info("Skipped deleting namespace " + name + ", not found");
        } else {
            client.namespaces().withName(name).delete();
            LOG.info("Deleted namespace " + name);
        }
    }

    /**
     * Creates a config map with given name and data.
     *
     * @param name configmap name
     * @param data map with data
     */
    public static void createConfigMap(String name, Map<String, String> data) {
        client.configMaps().withName(name).createOrReplace(
            new ConfigMapBuilder()
                .withNewMetadata()
                .withName(name)
                .endMetadata()
                .withData(data)
                .build()
        );
    }

    /**
     * Get log of pod (alternative to OpenShift instance method getPodLog())
     *
     * @param p the pod
     * @return log of the pod
     */
    public static String getLogs(Pod p) {
        PodResource<Pod> pr = OpenshiftClient.get().pods().withName(p.getMetadata().getName());
        Container c = OpenshiftClient.get().getAnyContainer(p);
        ContainerResource cr = pr.inContainer(c.getName());
        return cr.getLog();
    }

    /**
     * @param name of secret to be created
     * @param credentials of service from credentials.yaml
     * @param labels default null
     * @param prefix default null, prefix which should be prepended to keys in credentials (<prefix>key=value)
     * @return created secret
     */
    public static Secret createApplicationPropertiesSecret(String name, Properties credentials, Map<String, String> labels, String prefix) {
        String credentialsString = MapUtils.propertiesToString(credentials, Optional.ofNullable(prefix).orElse(""));
        String dataFileName = (name.indexOf(".") != -1) ? name.substring(0, name.indexOf(".")) : name;
        Secret secret = new SecretBuilder()
            .withStringData(Collections.singletonMap(dataFileName + ".properties", credentialsString)).withNewMetadata()
            .withName(name)
            .addToLabels(labels)
            .endMetadata().build();
        return client.secrets().createOrReplace(secret);
    }

    public static void deleteSecret(String name) {
        client.secrets().withName(name).delete();
    }
}
