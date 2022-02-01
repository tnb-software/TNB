package org.jboss.fuse.tnb.cassandra.account;

import org.jboss.fuse.tnb.common.account.Account;

public class CassandraAccount implements Account {
    private String username = "cassandra";
    private String password = "cassandra";
    private String datacenter = "datacenter1";

    public String username() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String password() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String datacenter() {
        return datacenter;
    }

    public void setDatacenter(String datacenter) {
        this.datacenter = datacenter;
    }
}
