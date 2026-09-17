package software.tnb.cyberark.cyberarkvault.account;

import software.tnb.common.account.Account;

public class CyberarkVaultAccount implements Account {
    // key generated using docker run --rm cyberark/conjur data-key generate > data_key
    private String encryptionKey = "tkRr4s9PO7h291oPzSUYQdUlgbfOPonBmvJwXIRpjyQ=";
    private String accountName = "tnb";
    private String userName = "admin";
    private String apiKey;

    public String encryptionKey() {
        return encryptionKey;
    }

    public void setEncryptionKey(String encryptionKey) {
        this.encryptionKey = encryptionKey;
    }

    public String accountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String userName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String apiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }
}
