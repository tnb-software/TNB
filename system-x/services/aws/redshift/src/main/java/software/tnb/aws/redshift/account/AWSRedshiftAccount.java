package software.tnb.aws.redshift.account;

import software.tnb.aws.common.account.AWSAccount;

public class AWSRedshiftAccount extends AWSAccount {
    private String password;
    private String clusterIdentifier;

    @Override
    public String credentialsId() {
        return "aws_redshift";
    }

    public String password() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String clusterIdentifier() {
        return clusterIdentifier;
    }

    public void setClusterIdentifier(String clusterIdentifier) {
        this.clusterIdentifier = clusterIdentifier;
    }
}
