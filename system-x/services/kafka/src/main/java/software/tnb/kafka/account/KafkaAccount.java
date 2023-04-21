package software.tnb.kafka.account;

import software.tnb.common.account.Account;

public class KafkaAccount implements Account {
    private String basicUser = "testuser";
    private String basicPassword = "testpassword";
    private String trustStore = "target/kafka-truststore.p12";
    private String trustStorePassword;

    public String basicUser() {
        return "testuser";
    }

    public String basicPassword() {
        return "testpassword";
    }

    public String trustStore() {
        return trustStore;
    }

    public String trustStorePassword() {
        return trustStorePassword;
    }

    public void setTrustStorePassword(String password) {
        this.trustStorePassword = password;
    }
}
