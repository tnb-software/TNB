package org.jboss.fuse.tnb.amq.streams.resource.openshift;

import org.jboss.fuse.tnb.amq.streams.service.Kafka;
import org.jboss.fuse.tnb.common.deployment.OpenshiftDeployable;
import org.jboss.fuse.tnb.common.deployment.WithName;
import org.jboss.fuse.tnb.common.openshift.OpenshiftClient;

import com.google.auto.service.AutoService;

import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import io.fabric8.kubernetes.api.model.SecretBuilder;
import io.fabric8.kubernetes.client.dsl.MixedOperation;
import io.fabric8.kubernetes.client.dsl.Resource;
import io.fabric8.kubernetes.client.dsl.base.CustomResourceDefinitionContext;
import io.strimzi.api.kafka.KafkaList;
import io.strimzi.api.kafka.KafkaTopicList;
import io.strimzi.api.kafka.model.KafkaBuilder;
import io.strimzi.api.kafka.model.KafkaTopic;
import io.strimzi.api.kafka.model.KafkaTopicBuilder;
import io.strimzi.api.kafka.model.listener.arraylistener.KafkaListenerType;
import io.strimzi.api.kafka.model.status.Condition;

@AutoService(Kafka.class)
public class OpenshiftAmqStreams extends Kafka implements OpenshiftDeployable, WithName {

    private static final String CRD_GROUP = "kafka.strimzi.io";
    private static final String CRD_VERSION = "v1beta2";

    private static final CustomResourceDefinitionContext KAFKA_CONTEXT = new CustomResourceDefinitionContext.Builder()
        .withName("Kafka")
        .withGroup(CRD_GROUP)
        .withVersion(CRD_VERSION)
        .withPlural("kafkas")
        .withScope("Namespaced")
        .build();

    private static final MixedOperation<io.strimzi.api.kafka.model.Kafka, KafkaList, Resource<io.strimzi.api.kafka.model.Kafka>> KAFKA_CRD_CLIENT =
        OpenshiftClient.get().customResources(KAFKA_CONTEXT, io.strimzi.api.kafka.model.Kafka.class, KafkaList.class);

    private static final CustomResourceDefinitionContext KAFKA_TOPIC_CONTEXT = new CustomResourceDefinitionContext.Builder()
        .withName("KafkaTopic")
        .withGroup(CRD_GROUP)
        .withVersion(CRD_VERSION)
        .withPlural("kafkatopics")
        .withScope("Namespaced")
        .build();

    private static final MixedOperation<KafkaTopic, KafkaTopicList, Resource<KafkaTopic>> KAFKA_TOPIC_CRD_CLIENT =
        OpenshiftClient.get().customResources(KAFKA_TOPIC_CONTEXT, KafkaTopic.class, KafkaTopicList.class);

    @Override
    public void undeploy() {
        KAFKA_CRD_CLIENT.withName(name()).delete();
        // FIXME: (asmigala) this is just temporary, need to make sure pods are deleted and then undeploy operator
    }

    @Override
    public void openResources() {
        // no-op for now
    }

    @Override
    public void closeResources() {
        // no-op for now
    }

    @Override
    public void create() {
        deployOperator();
        deployKafkaCR();
    }

    @Override
    public boolean isReady() {
        try {
            return KAFKA_CRD_CLIENT
                .withName(name())
                .get()
                .getStatus().getConditions()
                .stream()
                .filter(c -> "Ready".equals(c.getType()))
                .map(Condition::getStatus)
                .map(Boolean::parseBoolean)
                .findFirst().orElse(false);
        } catch (Exception ignored) {
            return false;
        }
    }

    @Override
    public boolean isDeployed() {
        return OpenshiftClient.get().getLabeledPods("name", "amq-streams-cluster-operator").size() != 0
            && KAFKA_CRD_CLIENT.withName(name()).get() != null;
    }

    @Override
    public String name() {
        return "my-kafka-cluster";
    }

    private void deployOperator() {
        OpenshiftClient.get().createSubscription("stable", "amq-streams", "redhat-operators", "amq-streams");
        OpenshiftClient.get().waitForInstallPlanToComplete("amq-streams");
    }

    private void deployKafkaCR() {
        //@formatter:off
        io.strimzi.api.kafka.model.Kafka kafka = new KafkaBuilder()
            .withNewMetadata()
                .withName(name())
            .endMetadata()
            .withNewSpec()
                .withNewKafka()
                .withReplicas(1)
                    .withNewListeners()
                        .addNewGenericKafkaListener()
                            .withName("plain")
                            .withPort(9092)
                            .withTls(false)
                            .withType(KafkaListenerType.INTERNAL)
                        .endGenericKafkaListener()
                        .addNewGenericKafkaListener()
                            .withName("tls")
                            .withPort(9093)
                            .withTls(true)
                            .withType(KafkaListenerType.INTERNAL)
                        .endGenericKafkaListener()
                    .endListeners()
                .withNewEphemeralStorage().endEphemeralStorage()
                .addToConfig("offsets.topic.replication.factor", 1)
                .addToConfig("transaction.state.log.replication.factor", 1)
                .addToConfig("transaction.state.log.min.isr", 1)
                .endKafka()
                .withNewZookeeper()
                    .withReplicas(1)
                    .withNewEphemeralStorage().endEphemeralStorage()
                .endZookeeper()
                .withNewEntityOperator()
                    .withNewTopicOperator().endTopicOperator()
                    .withNewUserOperator().endUserOperator()
                .endEntityOperator()
                .endSpec()
            .build();
        //@formatter:on

        KAFKA_CRD_CLIENT.createOrReplace(kafka);
    }

    @Override
    public String bootstrapServers(boolean tls) {
        return KAFKA_CRD_CLIENT.withName(name()).get().getStatus().getListeners()
            .stream()
            .filter(l -> l.getType().equals(tls ? "tls" : "plain"))
            .findFirst().get().getBootstrapServers();
    }

    @Override
    public void createTopic(String name, int partitions, int replicas) {
        //@formatter:off
        KafkaTopic kafkaTopic = new KafkaTopicBuilder()
            .withNewMetadata()
                .withName(name)
                .addToLabels("strimzi.io/cluster", name())
            .endMetadata()
            .withNewSpec()
                .withPartitions(partitions)
                .withReplicas(replicas)
            .endSpec()
            .build();
        //@formatter:on
        KAFKA_TOPIC_CRD_CLIENT.createOrReplace(kafkaTopic);
    }

    public void createSASLBasedPlainAuth() { // via https://access.redhat.com/documentation/en-us/red_hat_amq/2021
        // .q2/html-single/using_amq_streams_on_openshift/index#type-KafkaClientAuthenticationPlain-reference
        String password = Base64.getEncoder().encodeToString(getSASLBasedPlainAuthPwd().getBytes());
        Map<String, String> labels = new HashMap<>();
        labels.put("strimzi.io/kind", "KafkaUser");
        labels.put("strimzi.io/cluster", name());

        SecretBuilder sb = new SecretBuilder()
            .withApiVersion("v1")
            .editOrNewMetadata().withName(getSASLBasedPlainAuthUser()).withLabels(labels).endMetadata()
            .withType("Opaque")
            .withData(Collections.singletonMap("password", password));
        OpenshiftClient.get().secrets().createOrReplace(sb.build());
    }

    public String getSASLBasedPlainAuthUser() {
        return "testuser";
    }

    public String getSASLBasedPlainAuthPwd() {
        return "testpassword";
    }
}
