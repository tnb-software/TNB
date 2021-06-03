package org.jboss.fuse.tnb.sns.account;

import org.jboss.fuse.tnb.aws.account.AWSAccount;

public class SNSAccount extends AWSAccount {
    private String topicUrlPrefix;
    private String topicArnPrefix;

    @Override
    public String credentialsId() {
        return "aws";
    }

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
