package software.tnb.aws.redshift.account;

import software.tnb.aws.common.account.AWSAccount;

public class RedshiftAccount extends AWSAccount {
    private String password;
    private String cluster_identifier;

    @Override
    public String credentialsId() {
        return "aws-redshift";
    }

    public String password() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String clusterIdentifier() {
        return cluster_identifier;
    }

    public void setCluster_identifier(String cluster_identifier) {
        this.cluster_identifier = cluster_identifier;
    }
}
