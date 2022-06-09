package software.tnb.servicenow.account;

import software.tnb.common.account.Account;
import software.tnb.common.account.WithId;

public class ServiceNowAccount implements Account, WithId {
    private String instanceName;
    private String userName;
    private String password;

    public String instanceName() {
        return instanceName;
    }

    public void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
    }

    public String userName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String password() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String url() {
        return String.format("https://%s.service-now.com/api/now/v2/table/incident", instanceName);
    }

    @Override
    public String credentialsId() {
        return "servicenow";
    }
}

