package software.tnb.account.util;

import software.tnb.common.account.Account;

public class TestAccountNoId implements Account {
    private String username;
    private String password;

    public TestAccountNoId() {
        this.username = "Hello";
        this.password = "World";
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
