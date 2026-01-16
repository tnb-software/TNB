package software.tnb.mail.resource.local;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

public class MailServerContainer extends GenericContainer<MailServerContainer> {
    private final int smtpPort;
    private final int httpPort;
    private final int imapPort;
    private final int pop3Port;

    public MailServerContainer(String image, int smtpPort, int httpPort, int imapPort, int pop3Port) {
        super(image);
        this.smtpPort = smtpPort;
        this.httpPort = httpPort;
        this.imapPort = imapPort;
        this.pop3Port = pop3Port;
        withExposedPorts(smtpPort, httpPort, imapPort, pop3Port);
        waitingFor(Wait.forLogMessage(".*AddUser command executed sucessfully in.*", 1));
    }

    public int getSmtpPort() {
        return getMappedPort(smtpPort);
    }

    public int getHttpPort() {
        return getMappedPort(httpPort);
    }

    public int getImapPort() {
        return getMappedPort(imapPort);
    }

    public int getPop3Port() {
        return getMappedPort(pop3Port);
    }
}
