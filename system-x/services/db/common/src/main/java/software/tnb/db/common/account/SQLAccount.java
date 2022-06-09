package software.tnb.db.common.account;

import software.tnb.common.account.Account;

public interface SQLAccount extends Account {
    String username();

    String password();

    String database();
}
