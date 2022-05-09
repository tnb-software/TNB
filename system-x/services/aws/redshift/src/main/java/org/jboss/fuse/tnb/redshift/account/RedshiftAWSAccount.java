package org.jboss.fuse.tnb.redshift.account;

import org.jboss.fuse.tnb.aws.account.AWSAccount;

public class RedshiftAWSAccount extends AWSAccount {
    private String redshift_password;
    private String redshift_cluster_identifier;

    public String redshiftPassword() {
        return redshift_password;
    }

    public void setRedshift_password(String redshift_password) {
        this.redshift_password = redshift_password;
    }

    public String redshiftClusterIdentifier() {
        return redshift_cluster_identifier;
    }

    public void setRedshift_cluster_identifier(String redshift_cluster_identifier) {
        this.redshift_cluster_identifier = redshift_cluster_identifier;
    }
}
