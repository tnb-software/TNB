package software.tnb.hashicorp.vault.account;

import software.tnb.common.account.Account;

public record HashicorpVaultAccount(String token) implements Account {
    private static final String DEFAULT_TOKEN = "myTestToken";

    public HashicorpVaultAccount() {
        this(DEFAULT_TOKEN);
    }
}
