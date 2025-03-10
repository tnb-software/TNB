package software.tnb.keycloak.account;

import software.tnb.common.account.Account;

public class KeycloakAccount implements Account {
    private String adminUser = "admin";
    private String adminPassword = "admin";
    private String truststorePassword = "changemechangemechangeme";

    public String adminUser() {
        return adminUser;
    }

    public String adminPassword() {
        return adminPassword;
    }

    public String truststorePassword() {
        return truststorePassword;
    }
}
