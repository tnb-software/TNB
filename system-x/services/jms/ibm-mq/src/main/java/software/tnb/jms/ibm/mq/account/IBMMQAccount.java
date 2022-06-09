package software.tnb.jms.ibm.mq.account;

import software.tnb.common.account.Account;

public class IBMMQAccount implements Account {
    private String queueManager = "QM1";
    private String channel = "DEV.APP.SVRCONN";
    private String adminChannel = "DEV.ADMIN.SVRCONN";
    private String username = "app";
    private String password = "passw0rd";
    private String adminUsername = "admin";
    private String adminPassword = "passw0rd";

    public String queueManager() {
        return queueManager;
    }

    public void setQueueManager(String queueManager) {
        this.queueManager = queueManager;
    }

    public String channel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String adminChannel() {
        return adminChannel;
    }

    public void setAdminChannel(String adminChannel) {
        this.adminChannel = adminChannel;
    }

    public String username() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String password() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String adminUsername() {
        return adminUsername;
    }

    public void setAdminUsername(String adminUsername) {
        this.adminUsername = adminUsername;
    }

    public String adminPassword() {
        return adminPassword;
    }

    public void setAdminPassword(String adminPassword) {
        this.adminPassword = adminPassword;
    }
}
