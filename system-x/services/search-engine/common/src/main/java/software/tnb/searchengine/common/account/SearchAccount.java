package software.tnb.searchengine.common.account;

import software.tnb.common.account.Account;

/*public interface SearchAccount extends Account {

    String user();

    String password();

    void setPassword(String password);
}*/

public class SearchAccount implements Account {

    private String user;
    private String password;

    public SearchAccount(String user, String password) {
        this.user = user;
        this.password = password;
    }

    public String user() {
        return user;
    }

    public String password() {
        return password;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
