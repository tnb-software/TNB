package org.jboss.fuse.tnb.sns.validation;

import org.jboss.fuse.tnb.common.service.ServiceFactory;
import org.jboss.fuse.tnb.common.utils.WaitUtils;

import org.jboss.fuse.tnb.sns.account.SNSAccount;

import org.jboss.fuse.tnb.sqs.service.SQS;

import org.jboss.fuse.tnb.sqs.validation.SQSValidation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.CreateTopicResponse;
import software.amazon.awssdk.services.sqs.model.Message;

public class SNSValidation {
    private static final Logger LOG = LoggerFactory.getLogger(SNSValidation.class);

    private final SnsClient client;
    private final SNSAccount account;
    private final SQS sqs;

    public SNSValidation(SnsClient client, SNSAccount account, SQS sqs) {
        this.client = client;
        this.account = account;
        this.sqs = sqs;
    }

    public void createTopic(String topic) {
        LOG.debug("Creating SNS topic {}", topic);
        final CreateTopicResponse createTopicResonse = client.createTopic(b -> b.name(topic));
        WaitUtils.waitFor(() -> topicExists(createTopicResonse.topicArn()), "Waiting until the topic " + topic + " is created");
    }

    public void createTopicWithConsumer(String topic, String queue) {
        createTopic(topic);
        sqs.validation().createQueue(queue);
        sqs.validation().setPermissiveAccessPolicy(queue);
        createSQSSubscription(topic, queue);
    }

    public void deleteTopic(String topic) {
        LOG.debug("Deleting SNS topic {}", topic);
        client.deleteTopic(b -> b.topicArn(account.topicArnPrefix() + topic));
    }

    public void deleteTopicWithConsumer(String topic, String queue) {
        deleteTopic(topic);
        sqs.validation().deleteQueue(queue);
    }

    public void createSQSSubscription(String topic, String queue) {
        client.subscribe(builder -> builder.topicArn(account.topicArnPrefix() + topic).protocol("sqs").endpoint(sqs.account().queueArnPrefix() + queue));
    }

    public boolean topicExists(String topicArn) {
        return client.listTopics().topics().stream().anyMatch(topic -> topic.topicArn().equals(topicArn));
    }
}
