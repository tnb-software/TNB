package software.tnb.ftp.sftp.account;

import software.tnb.ftp.common.FileTransferAccount;

public class SFTPAccount implements FileTransferAccount {

    private String username = "test";
    private String password = "test";

    @Override
    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String username() {
        return username;
    }

    @Override
    public String password() {
        return password;
    }
    
    public String baseDir() {
        return homeDir() + "/" + username();
    }

    public String homeDir() {
        return "data";
    }
}
