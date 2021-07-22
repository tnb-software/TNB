package org.jboss.fuse.tnb.common;

import org.jboss.fuse.tnb.common.account.Account;

public interface FileTransferAccount extends Account {
    void setUsername(String username);

    void setPassword(String password);

    String username();

    String password();
}
