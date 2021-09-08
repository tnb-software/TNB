package org.jboss.fuse.tnb.common.openshift;

import org.jboss.fuse.tnb.common.config.OpenshiftConfiguration;
import org.jboss.fuse.tnb.common.utils.IOUtils;
import org.jboss.fuse.tnb.common.utils.PropertiesUtils;
import org.jboss.fuse.tnb.common.utils.WaitUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.function.BooleanSupplier;

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
import io.fabric8.kubernetes.client.dsl.PodResource;
import io.fabric8.openshift.api.model.operatorhub.v1.OperatorGroupBuilder;
import io.fabric8.openshift.api.model.operatorhub.v1alpha1.InstallPlan;
import io.fabric8.openshift.api.model.operatorhub.v1alpha1.Subscription;
import io.fabric8.openshift.api.model.operatorhub.v1alpha1.SubscriptionBuilder;
import io.fabric8.openshift.client.OpenShiftConfig;
import io.fabric8.openshift.client.OpenShiftConfigBuilder;

public final class OpenshiftClient extends OpenShift {
    private static final Logger LOG = LoggerFactory.getLogger(OpenshiftClient.class);
    private static OpenshiftClient client;

    private OpenshiftClient(OpenShiftConfig openShiftConfig) {
        super(openShiftConfig);
    }

    private static OpenshiftClient createClient() {
        if (OpenshiftConfiguration.openshiftUrl() == null) {
            try {
                OpenShiftConfig config = new OpenShiftConfig(Config.fromKubeconfig(IOUtils.readFile(OpenshiftConfiguration.openshiftKubeconfig())));
                LOG.info("Using cluster {}", config.getMasterUrl());
                config.setNamespace(OpenshiftConfiguration.openshiftNamespace());
                config.setHttpsProxy(OpenshiftConfiguration.openshiftHttpsProxy());
                config.setBuildTimeout(10 * 60 * 1000);
                config.setRequestTimeout(120_000);
                config.setConnectionTimeout(120_000);
                config.setTrustCerts(true);
                return new OpenshiftClient(config);
            } catch (IOException e) {
                throw new RuntimeException("Unable to create openshift config: ", e);
            }
        } else {
            return new OpenshiftClient(
                new OpenShiftConfigBuilder()
                    .withMasterUrl(OpenshiftConfiguration.openshiftUrl())
                    .withTrustCerts(true)
                    .withNamespace(OpenshiftConfiguration.openshiftNamespace())
                    .withUsername(OpenshiftConfiguration.openshiftUsername())
                    .withPassword(OpenshiftConfiguration.openshiftPassword())
                    .withHttpsProxy(OpenshiftConfiguration.openshiftHttpsProxy())
                    .build());
        }
    }

    public static OpenshiftClient get() {
        if (client == null) {
            client = createClient();
        }
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
    public void createSubscription(String channel, String operatorName, String source, String subscriptionName) {
        createSubscription(channel, operatorName, source, subscriptionName, "openshift-marketplace", OpenshiftConfiguration.openshiftNamespace(),
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
        createSubscription(channel, operatorName, source, subscriptionName, subscriptionNamespace, OpenshiftConfiguration.openshiftNamespace(),
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
            client.operatorHub().operatorGroups().inNamespace(targetNamespace).createOrReplace(operatorGroupBuilder.build());
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
            .endSpec()
            .build();
        client.operatorHub().subscriptions().inNamespace(targetNamespace).createOrReplace(s);
    }

    /**
     * Waits until the install plan for a given subscription completes.
     *
     * @param subscriptionName subscription name
     */
    public void waitForInstallPlanToComplete(String subscriptionName) {
        waitForInstallPlanToComplete(subscriptionName, OpenshiftConfiguration.openshiftNamespace());
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
        deleteSubscription(name, OpenshiftConfiguration.openshiftNamespace());
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
        WaitUtils.waitFor(() -> OpenshiftClient.get().imageStreams().withName(name).get().getSpec().getTags().stream()
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
        OpenshiftClient.get().buildConfigs().withName(name).instantiateBinary().fromFile(filePath.toFile());

        BooleanSupplier success = () -> "complete".equalsIgnoreCase(OpenshiftClient.get()
            .getBuild(name + "-" + OpenshiftClient.get().getBuildConfig(name).getStatus().getLastVersion()).getStatus().getPhase());
        BooleanSupplier fail = () -> "failed".equalsIgnoreCase(OpenshiftClient.get()
            .getBuild(name + "-" + OpenshiftClient.get().getBuildConfig(name).getStatus().getLastVersion()).getStatus().getPhase());

        WaitUtils.waitFor(success, fail, 5000L, "Waiting until the build completes");
    }

    /**
     * Create namespace (name obtained from system property openshift.namespace).
     */
    public void createNamespace() {
        createNamespace(OpenshiftConfiguration.openshiftNamespace());
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
        Namespace ns = new NamespaceBuilder().withNewMetadata().withName(name).endMetadata().build();
        if (client.namespaces().withName(name).get() == null) {
            client.namespaces().create(ns);
            LOG.info("Created namespace " + name);
        } else {
            LOG.info("Skipped creating namespace " + name + ", already exists");
        }
    }

    /**
     * Delete namespace (name obtained from system property openshift.namespace).
     */
    public void deleteNamespace() {
        deleteNamespace(OpenshiftConfiguration.openshiftNamespace());
    }

    /**
     * Delete namespace of given name.
     *
     * @param name of namespace to be deleted
     */
    public void deleteNamespace(String name) {
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
     * @return created configmap instance
     */
    public ConfigMap createConfigMap(String name, Map<String, String> data) {
        return client.configMaps().withName(name).createOrReplace(
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
        PodResource<Pod> pr = OpenshiftClient.get().pods().withName(p.getMetadata().getName());
        final List<Container> allContainers = OpenshiftClient.get().getAllContainers(p);
        Container c;
        if (allContainers.size() == 1) {
            c = allContainers.get(0);
        } else {
            // this throws exception if there is no "integration" container, we can solve that later once it starts failing
            c = OpenshiftClient.get().getContainer(p, "integration");
        }
        return pr.inContainer(c.getName()).getLog();
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
        return client.secrets().createOrReplace(secret);
    }

    public void deleteSecret(String name) {
        client.secrets().withName(name).delete();
    }
}
