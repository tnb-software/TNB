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
        container = new JamesServerContainer(image(), SMTP_PORT, HTTP_PORT, IMAP_PORT, POP3_PORT);
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

    @Override
    public String smtpHostname() {
        return "localhost:" + container.getSmtpPort();
    }

    @Override
    public String imapHostname() {
        return "localhost:" + container.getImapPort();
    }

    @Override
    public String pop3Hostname() {
        return "localhost:" + container.getPop3Port();
    }

    @Override
    public String httpHostname() {
        return "localhost:" + container.getHttpPort();
    }

    @Override
    public String smtpValidationHostname() {
        return smtpHostname();
    }
}
