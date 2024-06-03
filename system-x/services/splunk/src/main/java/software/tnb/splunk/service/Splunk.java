package software.tnb.splunk.service;

import software.tnb.common.deployment.WithDockerImage;
import software.tnb.common.service.ConfigurableService;
import software.tnb.splunk.account.SplunkAccount;
import software.tnb.splunk.service.configuration.SplunkConfiguration;
import software.tnb.splunk.validation.SplunkValidation;

import com.splunk.Service;
import com.splunk.ServiceArgs;

public abstract class Splunk extends ConfigurableService<SplunkAccount, Service, SplunkValidation, SplunkConfiguration>
    implements WithDockerImage {
    protected static final int UI_PORT = 8000;
    protected static final int PORT = 8089;
    protected static final int HEC_PORT = 8088;

    protected SplunkAccount account;
    protected com.splunk.Service client;
    protected SplunkValidation validation;

    public abstract String host();

    public String hecHost() {
        return host();
    }

    public int port() {
        return PORT;
    }

    public int hecPort() {
        return HEC_PORT;
    }

    /**
     * Due to self sign certificate, the client is not able to communicate via localhost and port-forward.
     * OCP external route with `reencrypt` is used. (Cluster needs to have valid certificate!)
     */
    protected com.splunk.Service client() {
        if (client == null) {
            ServiceArgs loginArgs = new ServiceArgs();
            loginArgs.setUsername(account().username());
            loginArgs.setPassword(account().password());
            loginArgs.setHost(host());
            loginArgs.setPort(port());
            loginArgs.setScheme(protocol());
            client = com.splunk.Service.connect(loginArgs);
        }
        return client;
    }

    public SplunkValidation validation() {
        if (validation == null) {
            validation = new SplunkValidation(client());
        }
        return validation;
    }

    @Override
    public String defaultImage() {
        return "quay.io/fuse_qe/splunk:9.0.3-a2";
    }

    public String protocol() {
        return getConfiguration().getProtocol().toString();
    }

    public void openResources() {
        // nothing to do
    }

    public void closeResources() {
        validation = null;
        client = null;
    }
}
