package software.tnb.azure.common.account;

import software.tnb.common.account.Account;
import software.tnb.common.account.WithId;

public class AzureStorageAccount implements Account, WithId {
    private String access_key;
    private String account_name;

    @Override
    public String credentialsId() {
        return "tnb-azure-storage";
    }

    public void setAccess_key(String access_key) {
        this.access_key = access_key;
    }

    public void setAccount_name(String account_name) {
        this.account_name = account_name;
    }

    public String accessKey() {
        return access_key;
    }

    public String accountName() {
        return account_name;
    }
}
