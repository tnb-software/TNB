package software.tnb.google.cloud.common.account;

import software.tnb.common.account.Account;
import software.tnb.common.account.WithId;

public class GoogleCloudAccount implements Account, WithId {

    private String project_id;
    // this needs to be in base64 in the credentials file
    private String service_account_key;

    @Override
    public String credentialsId() {
        return "google-cloud";
    }

    public String projectId() {
        return project_id;
    }

    public void setProject_id(String project_id) {
        this.project_id = project_id;
    }

    public String serviceAccountKey() {
        return service_account_key;
    }

    public void setService_account_key(String service_account_key) {
        this.service_account_key = service_account_key;
    }
}
