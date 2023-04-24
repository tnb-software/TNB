package software.tnb.aws.sns.validation;

import software.tnb.aws.sns.account.SNSAccount;
import software.tnb.aws.sqs.service.SQS;
import software.tnb.common.utils.WaitUtils;
import software.tnb.common.validation.Validation;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.CreateTopicResponse;

public class SNSValidation implements Validation {
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
        final CreateTopicResponse createTopicResponse = client.createTopic(b -> b.name(topic));
        WaitUtils.waitFor(() -> topicExists(createTopicResponse.topicArn()), "Waiting until the topic " + topic + " is created");
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
        client.subscribe(builder -> builder.topicArn(account.topicArnPrefix() + topic).protocol("sqs")
            .endpoint(sqs.account().queueArnPrefix() + queue));
    }

    public boolean topicExists(String topicNameOrArn) {
        return client.listTopics().topics().stream().anyMatch(topic ->
            StringUtils.equalsAny(topic.topicArn(), topicNameOrArn, account.topicArnPrefix() + topicNameOrArn));
    }
}
