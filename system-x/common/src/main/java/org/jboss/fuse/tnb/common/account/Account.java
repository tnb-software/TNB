package org.jboss.fuse.tnb.common.account;

public interface Account {
    default String credentialsId() {
        return null;
    }
}
