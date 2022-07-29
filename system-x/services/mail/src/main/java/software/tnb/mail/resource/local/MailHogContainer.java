package software.tnb.mail.resource.local;

import org.testcontainers.containers.GenericContainer;

public class MailHogContainer extends GenericContainer<MailHogContainer> {
    private final int smtpPort;
    private final int httpPort;

    public MailHogContainer(String image, int smtpPort, int httpPort) {
        super(image);
        this.smtpPort = smtpPort;
        this.httpPort = httpPort;
        withExposedPorts(smtpPort, httpPort);
    }

    public int getSmtpPort() {
        return getMappedPort(smtpPort);
    }

    public int getHttpPort() {
        return getMappedPort(httpPort);
    }
}
