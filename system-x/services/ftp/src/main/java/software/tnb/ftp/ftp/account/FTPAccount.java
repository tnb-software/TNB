package software.tnb.ftp.ftp.account;

import software.tnb.ftp.common.FileTransferAccount;

public class FTPAccount implements FileTransferAccount {

    private String username = "test";
    private String password = "test";

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String username() {
        return username;
    }

    public String password() {
        return password;
    }
}
