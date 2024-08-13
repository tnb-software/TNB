package software.tnb.rest.service;

import software.tnb.common.account.NoAccount;
import software.tnb.common.client.NoClient;
import software.tnb.common.deployment.WithDockerImage;
import software.tnb.common.service.Service;
import software.tnb.rest.validation.RestValidation;

public abstract class Rest extends Service<NoAccount, NoClient, RestValidation> implements WithDockerImage {

    protected static final int PORT = 8080;

    public abstract String host();

    public abstract int port();

    public RestValidation validation() {
        if (validation == null) {
            validation = new RestValidation(host(), port());
        }
        return validation;
    }

    public String defaultImage() {
        return "quay.io/fuse_qe/petstore3:1.0.19";
    }

    public void openResources() {

    }

    public void closeResources() {

    }
}
