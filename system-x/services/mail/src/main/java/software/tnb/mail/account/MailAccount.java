package software.tnb.mail.account;

import software.tnb.common.account.Account;

public class MailAccount implements Account {
    private String username = "user@tnb.test";
    private String password = "user";
    private String recipient = "recipient@tnb.test";

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String recipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String username() {
        return username;
    }

    public String password() {
        return password;
    }
}
