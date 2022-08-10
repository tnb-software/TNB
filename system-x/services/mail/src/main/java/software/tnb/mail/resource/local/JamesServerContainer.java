package software.tnb.mail.resource.local;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

public class JamesServerContainer extends GenericContainer<JamesServerContainer> {
    private final int smtpPort;
    private final int httpPort;
    private final int imapPort;
    private final int popPort;

    public JamesServerContainer(String image, int smtpPort, int httpPort, int imapPort, int popPort) {
        super(image);
        this.smtpPort = smtpPort;
        this.httpPort = httpPort;
        this.imapPort = imapPort;
        this.popPort = popPort;
        withExposedPorts(smtpPort, httpPort, imapPort, popPort);
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

    public int getPopPort() {
        return getMappedPort(popPort);
    }
}
