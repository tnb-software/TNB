package org.jboss.fuse.tnb.redshift.account;

import org.jboss.fuse.tnb.aws.account.AWSAccount;

public class RedshiftAWSAccount extends AWSAccount {
    private String redshift_password;

    public String getRedshift_password() {
        return redshift_password;
    }

    public void setRedshift_password(String redshift_password) {
        this.redshift_password = redshift_password;
    }
}
