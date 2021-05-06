package org.jboss.fuse.tnb.sftp.resource.local;

import org.jboss.fuse.tnb.common.deployment.Deployable;
import org.jboss.fuse.tnb.common.utils.IOUtils;
import org.jboss.fuse.tnb.sftp.service.Sftp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.auto.service.AutoService;

import java.io.IOException;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.sftp.SFTPClient;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;

@AutoService(Sftp.class)
public class LocalSftp extends Sftp implements Deployable {
    private static final Logger LOG = LoggerFactory.getLogger(LocalSftp.class);
    private SftpContainer container;
    private SFTPClient client;
    
    @Override

    public void deploy() {
        LOG.info("Starting sftp container");
        container = new SftpContainer(sftpImage(), containerEnvironment());
        container.start();
    }

    @Override
    public void undeploy() {
        container.stop();
    }

    @Override
    public void openResources() {
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

    @Override
    public void closeResources() {
        IOUtils.closeQuietly(client);
    }

    @Override
    public SFTPClient client() {
        return client;
    }

    @Override
    public String host() {
        return container.getContainerInfo().getNetworkSettings().getNetworks().get("bridge").getIpAddress();
    }
}
