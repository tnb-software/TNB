package software.tnb.azure.common.account;

import software.tnb.common.account.Account;
import software.tnb.common.account.WithId;

public class AzureStorageAccount implements Account, WithId {
    private String accessKey;
    private String accountName;

    @Override
    public String credentialsId() {
        return "azure_storage";
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String accessKey() {
        return accessKey;
    }

    public String accountName() {
        return accountName;
    }
}
