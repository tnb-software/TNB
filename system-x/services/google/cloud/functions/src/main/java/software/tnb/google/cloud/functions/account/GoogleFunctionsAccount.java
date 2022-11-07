package software.tnb.google.cloud.functions.account;

import software.tnb.google.cloud.common.account.GoogleCloudAccount;

public class GoogleFunctionsAccount extends GoogleCloudAccount {
    private String region = "us-central1";

    public String region() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }
}
