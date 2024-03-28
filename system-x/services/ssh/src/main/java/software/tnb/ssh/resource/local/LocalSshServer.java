package software.tnb.ssh.resource.local;

import software.tnb.common.deployment.Deployable;
import software.tnb.ssh.service.SshServer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.auto.service.AutoService;

@AutoService(SshServer.class)
public class LocalSshServer extends SshServer implements Deployable {

    private static final Logger LOG = LoggerFactory.getLogger(LocalSshServer.class);

    private SshServerContainer container;

    @Override
    public void deploy() {
        LOG.info("Starting SSH Server container");
        container = new SshServerContainer(image(), containerEnvironment());
        container.start();
        LOG.info("SSH Server container started");
    }

    @Override
    public void undeploy() {
        if (container != null) {
            LOG.info("Stopping SSH Server container");
            container.stop();
        }
    }

    @Override
    public void openResources() {

    }

    @Override
    public void closeResources() {

    }

    @Override
    public String host() {
        return this.container.getHost();
    }

    @Override
    public int port() {
        return this.container.getMappedPort(SshServer.SSHD_LISTENING_PORT);
    }

    @Override
    public String getLog() {
        return container.getLogs();
    }
}
