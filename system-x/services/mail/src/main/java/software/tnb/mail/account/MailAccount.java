package software.tnb.mail.account;

import software.tnb.common.account.Account;

public class MailAccount implements Account {
    private String username1 = "user01@tnb.software";
    private String password1 = "1234";
    private String username2 = "user02@tnb.software";
    private String password2 = "1234";
    private String username3 = "user03@tnb.software";
    private String password3 = "1234";

    public String getUsername1() {
        return username1;
    }

    public void setUsername1(String username1) {
        this.username1 = username1;
    }

    public String getPassword1() {
        return password1;
    }

    public void setPassword1(String password1) {
        this.password1 = password1;
    }

    public String getUsername2() {
        return username2;
    }

    public void setUsername2(String username2) {
        this.username2 = username2;
    }

    public String getPassword2() {
        return password2;
    }

    public void setPassword2(String password2) {
        this.password2 = password2;
    }

    public String getUsername3() {
        return username3;
    }

    public void setUsername3(String username3) {
        this.username3 = username3;
    }

    public String getPassword3() {
        return password3;
    }

    public void setPassword3(String password3) {
        this.password3 = password3;
    }
}
