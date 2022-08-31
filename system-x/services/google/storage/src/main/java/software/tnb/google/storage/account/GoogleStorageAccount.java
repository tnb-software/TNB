package software.tnb.google.storage.account;

import software.tnb.common.account.Account;
import software.tnb.common.account.WithId;

public class GoogleStorageAccount implements Account, WithId {
    // this needs to be in base64 in the credentials file
    private String service_account_key;

    public String serviceAccount() {
        return service_account_key;
    }

    public void setService_account_key(String service_account_key) {
        this.service_account_key = service_account_key;
    }

    @Override
    public String credentialsId() {
        return "google-cloud";
    }
}
