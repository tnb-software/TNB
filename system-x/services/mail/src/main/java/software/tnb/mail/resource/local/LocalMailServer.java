package software.tnb.mail.resource.local;

import software.tnb.common.deployment.Deployable;
import software.tnb.mail.service.MailServer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.auto.service.AutoService;

@AutoService(MailServer.class)
public class LocalMailServer extends MailServer implements Deployable {
    private static final Logger LOG = LoggerFactory.getLogger(LocalMailServer.class);
    private JamesServerContainer container;

    @Override
    public void deploy() {
        LOG.info("Starting James container");
        container = new JamesServerContainer(image(), smtpPort(), httpPort(), imapPort(), popPort());
        container.start();
        LOG.info("James container started");
    }

    @Override
    public void undeploy() {
        if (container != null) {
            LOG.info("Stopping James container");
            container.stop();
        }
    }

    @Override
    public void openResources() {
    }

    @Override
    public void closeResources() {
    }

    public int smtpPort() {
        if (container == null) {
            return super.smtpPort();
        }
        return container.getSmtpPort();
    }

    public int httpPort() {
        if (container == null) {
            return super.httpPort();
        }

        return container.getHttpPort();
    }

    public int imapPort() {
        if (container == null) {
            return super.imapPort();
        }
        return container.getImapPort();
    }

    public int popPort() {
        if (container == null) {
            return super.popPort();
        }

        return container.getPopPort();
    }

    @Override
    public String hostname() {
        return container.getHost();
    }
}
