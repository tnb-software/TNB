package software.tnb.samba.service;

import software.tnb.common.deployment.WithDockerImage;
import software.tnb.common.service.Service;
import software.tnb.samba.account.SambaAccount;
import software.tnb.samba.validation.SambaValidation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hierynomus.smbj.SMBClient;
import com.hierynomus.smbj.session.Session;

import java.io.IOException;
import java.util.Map;

public abstract class SambaServer extends Service<SambaAccount, SMBClient, SambaValidation> implements WithDockerImage {

    public static final int SAMBA_PORT_DEFAULT = 445;
    private static final Logger LOG = LoggerFactory.getLogger(SambaServer.class);

    @Override
    public String defaultImage() {
        return "quay.io/fuse_qe/samba:latest";
    }

    public abstract String address();

    public abstract String host();

    public abstract int port();

    public abstract String shareName();

    public abstract String getLog();

    public Map<String, String> containerEnvironment() {
        return Map.of();
    }

    public void openResources() {
        LOG.debug("Creating new Samba client");
        try {
            this.client = new SMBClient();
            Session session = this.client.connect(host(), port()).authenticate(
                new com.hierynomus.smbj.auth.AuthenticationContext(
                    account().user(),
                    account().password().toCharArray(),
                    null
                )
            );
            this.validation = new SambaValidation(shareName(), session);
        } catch (IOException e) {
            throw new RuntimeException("Unable to create Samba client", e);
        }
    }

    public void closeResources() {
        validation = null;
        if (client != null) {
            client.close();
        }
    }
}
