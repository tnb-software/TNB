package software.tnb.kafka.account;

import software.tnb.common.account.Account;
import software.tnb.common.account.WithId;

public class KafkaAccount implements Account, WithId {
    private String clientID;
    private String clientSecret;
    private String basicUser = "testuser";
    private String basicPassword = "testpassword";
    private String trustStore = "target/kafka-truststore.p12";
    private String trustStorePassword;

    @Override
    public String credentialsId() {
        return "kafka-rhoas-ck";
    }

    public String getClientID() {
        return clientID;
    }

    public void setClientID(String clientID) {
        this.clientID = clientID;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

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
