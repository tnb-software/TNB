package org.jboss.fuse.tnb.sql.common.account;

import org.jboss.fuse.tnb.common.account.Account;

public interface SQLAccount extends Account {
    String username();

    String password();

    String database();
}
