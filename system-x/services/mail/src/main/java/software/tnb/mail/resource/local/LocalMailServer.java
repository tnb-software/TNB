package software.tnb.mail.resource.local;

import software.tnb.common.deployment.ContainerDeployable;
import software.tnb.mail.service.MailServer;

import com.google.auto.service.AutoService;

@AutoService(MailServer.class)
public class LocalMailServer extends MailServer implements ContainerDeployable<MailServerContainer> {
    private final MailServerContainer container = new MailServerContainer(image(), SMTP_PORT, HTTP_PORT, IMAP_PORT, POP3_PORT);

    @Override
    public void openResources() {
    }

    @Override
    public void closeResources() {
        validation = null;
    }

    @Override
    public String smtpHostname() {
        return String.format("%s:%d", container.getHost(), container.getSmtpPort());
    }

    @Override
    public String imapHostname() {
        return String.format("%s:%d", container.getHost(), container.getImapPort());
    }

    @Override
    protected String imapExternalHostname() {
        return imapHostname();
    }

    @Override
    public String pop3Hostname() {
        return String.format("%s:%d", container.getHost(), container.getPop3Port());
    }

    @Override
    public String httpHostname() {
        return String.format("%s:%d", container.getHost(), container.getHttpPort());
    }

    @Override
    public String smtpValidationHostname() {
        return smtpHostname();
    }

    @Override
    public MailServerContainer container() {
        return container;
    }
}
