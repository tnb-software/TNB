package org.jboss.fuse.tnb.firehose.account;

import org.jboss.fuse.tnb.aws.account.AWSAccount;

public class KinesisFirehoseAccount extends AWSAccount {

    private String kinesis_role_arn;

    public void setKinesis_role_arn(String kinesis_role_arn) {
        this.kinesis_role_arn = kinesis_role_arn;
    }

    public String kinesisRoleArn() {
        return kinesis_role_arn;
    }
}
