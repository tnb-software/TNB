package software.tnb.splunk.service;

import software.tnb.common.deployment.WithDockerImage;
import software.tnb.common.deployment.WithExternalHostname;
import software.tnb.common.service.ConfigurableService;
import software.tnb.splunk.account.SplunkAccount;
import software.tnb.splunk.service.configuration.SplunkConfiguration;
import software.tnb.splunk.validation.SplunkValidation;

import com.splunk.ServiceArgs;

public abstract class Splunk extends ConfigurableService<SplunkConfiguration> implements WithExternalHostname, WithDockerImage {

    protected SplunkAccount account;
    private com.splunk.Service client;
    private SplunkValidation validation;

    public abstract int apiPort();

    public abstract SplunkAccount account();

    /**
     * Due to self sign certificate, the client is not able to communicate via localhost and port-forward.
     * OCP external route with `reencrypt` is used. (Cluster needs to have valid certificate!)
     */
    protected com.splunk.Service client() {
        if (client == null) {
            ServiceArgs loginArgs = new ServiceArgs();
            loginArgs.setUsername(account().username());
            loginArgs.setPassword(account().password());
            loginArgs.setHost(externalHostname());
            loginArgs.setPort(apiPort());
            loginArgs.setScheme(apiSchema());
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

    public String apiSchema() {
        return getConfiguration().getProtocol().toString();
    }
}
