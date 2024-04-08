package software.tnb.samba.service;

import software.tnb.common.client.NoClient;
import software.tnb.common.deployment.WithDockerImage;
import software.tnb.common.service.Service;
import software.tnb.common.validation.NoValidation;
import software.tnb.samba.account.SambaAccount;

import java.util.Map;

public abstract class SambaServer extends Service<SambaAccount, NoClient, NoValidation> implements WithDockerImage {

    public static final int SAMBA_PORT_DEFAULT = 445;

    @Override
    public String defaultImage() {
        return "quay.io/fuse_qe/samba:latest";
    }

    public abstract String address();

    public abstract int port();

    public abstract String shareName();

    public abstract String getLog();

    public Map<String, String> containerEnvironment() {
        return Map.of();
    }
}
