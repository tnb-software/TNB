package software.tnb.aws.sns.account;

import software.tnb.aws.common.account.AWSAccount;

public class SNSAccount extends AWSAccount {
    private String topicUrlPrefix;
    private String topicArnPrefix;

    public String topicUrlPrefix() {
        return topicUrlPrefix;
    }

    public void setTopicUrlPrefix(String topicUrlPrefix) {
        this.topicUrlPrefix = topicUrlPrefix;
    }

    public String topicArnPrefix() {
        return topicArnPrefix;
    }

    public void setTopicArnPrefix(String topicArnPrefix) {
        this.topicArnPrefix = topicArnPrefix;
    }
}
