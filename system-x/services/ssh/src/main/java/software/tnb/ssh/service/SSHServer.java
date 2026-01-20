package software.tnb.ssh.service;

import software.tnb.common.client.NoClient;
import software.tnb.common.deployment.WithDockerImage;
import software.tnb.common.service.Service;
import software.tnb.ssh.account.SshAccount;
import software.tnb.ssh.validation.SshValidation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public abstract class SSHServer extends Service<SshAccount, NoClient, SshValidation> implements WithDockerImage {

    private static final Logger LOG = LoggerFactory.getLogger(SSHServer.class);

    public static final int SSHD_LISTENING_PORT = 22;

    @Override
    public String defaultImage() {
        return "quay.io/fuse_qe/alpine-openssh:latest";
    }

    public SshValidation validation() {
        if (validation == null) {
            LOG.debug("Creating new Ssh validation");
            validation = new SshValidation(username(), host(), port());
        }
        return validation;
    }

    public String username() {
        return account().username();
    }

    public abstract String host();

    public abstract int port();

    public Map<String, String> containerEnvironment() {
        return Map.of();
    }
}
