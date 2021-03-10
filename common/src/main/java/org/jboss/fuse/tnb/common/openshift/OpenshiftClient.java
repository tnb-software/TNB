package org.jboss.fuse.tnb.common.openshift;

import org.jboss.fuse.tnb.common.config.OpenshiftConfiguration;
import org.jboss.fuse.tnb.common.utils.WaitUtils;

import java.util.concurrent.TimeoutException;

import cz.xtf.core.openshift.OpenShift;
import io.fabric8.openshift.api.model.operatorhub.v1alpha1.InstallPlan;
import io.fabric8.openshift.api.model.operatorhub.v1alpha1.Subscription;

public class OpenshiftClient {
    private static OpenShift client;

    public static OpenShift get() {
        if (client == null) {
            client = OpenShift.get(
                OpenshiftConfiguration.openshiftUrl(),
                OpenshiftConfiguration.openshiftNamespace(),
                OpenshiftConfiguration.openshiftUsername(),
                OpenshiftConfiguration.openshiftPassword()
            );
        }
        return client;
    }

    public static void createSubscription(String channel, String operatorName, String source, String subscriptionName) {
        if (get().operatorHub().operatorGroups().inNamespace(OpenshiftConfiguration.openshiftNamespace()).
            list().getItems().size() == 0) {
            get().operatorHub().operatorGroups().createOrReplaceWithNew()
                .withNewMetadata()
                    .withName(subscriptionName)
                .endMetadata()
                .withNewSpec()
                .   withTargetNamespaces(OpenshiftConfiguration.openshiftNamespace())
                .endSpec()
                .done();
        }

        get().operatorHub().subscriptions().createOrReplaceWithNew()
            .editOrNewMetadata()
                .withName(subscriptionName)
            .endMetadata()
            .withNewSpec()
                .withName(operatorName)
                .withChannel(channel)
                .withSource(source)
                .withSourceNamespace("openshift-marketplace")
            .endSpec()
            .done();
    }

    public static void waitForCompletion(String name) {
        try {
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
            }, 60, 5000L);
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }

    public static void deleteSubscription(String name) {
        Subscription subscription = get().operatorHub().subscriptions().withName(name).get();
        String csvName = subscription.getStatus().getCurrentCSV();
        //CSV being null can happen if you delete the subscription without deleting the CSV, then your new subscription is CSV-less
        if (csvName != null) {
            get().operatorHub().clusterServiceVersions().withName(csvName).delete();
        }
        get().operatorHub().subscriptions().withName(name).delete();
    }
}
