package org.jboss.fuse.tnb.sftp.resource.local;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.sftp.SFTPClient;

import net.schmizz.sshj.transport.verification.PromiscuousVerifier;

import org.jboss.fuse.tnb.common.config.SystemXConfiguration;
import org.jboss.fuse.tnb.common.deployment.Deployable;
import org.jboss.fuse.tnb.common.utils.IOUtils;
import org.jboss.fuse.tnb.ftp.service.Ftp;

import org.jboss.fuse.tnb.sftp.service.Sftp;

import org.junit.jupiter.api.extension.ExtensionContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.auto.service.AutoService;

import java.io.IOException;

@AutoService(Sftp.class)
public class LocalSftp extends Sftp implements Deployable {
    private static final Logger LOG = LoggerFactory.getLogger(LocalSftp.class);
    private SftpContainer container;
    private SFTPClient client;
    
    @Override

    public void deploy() {
        LOG.info("Starting sftp container");
        container = new SftpContainer(SystemXConfiguration.sftpImage(), containerEnvironment());
        container.start();
    }

    @Override
    public void undeploy() {
        IOUtils.closeQuietly(client);
        container.stop();
    }

    @Override
    public SFTPClient client() {
        if (client == null) {
            try {
                LOG.debug("Creating new SFTPClient instance");
                SSHClient sshClient = new SSHClient();
                sshClient.addHostKeyVerifier(new PromiscuousVerifier());
                sshClient.connect(host(), port());
                sshClient.authPassword(account().username(), account().password());
                client = sshClient.newSFTPClient();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return client;
    }

    @Override
    public String host() {
        return container.getContainerInfo().getNetworkSettings().getNetworks().get("bridge").getIpAddress();
    }

    @Override
    public void afterAll(ExtensionContext extensionContext) throws Exception {
        undeploy();
    }

    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {
        deploy();
    }
}
