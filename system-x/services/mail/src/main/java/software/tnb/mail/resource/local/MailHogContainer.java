package software.tnb.mail.resource.local;

import org.testcontainers.containers.GenericContainer;

import java.util.Map;

public class MailHogContainer extends GenericContainer<MailHogContainer> {
    private final int smtpPort;
    private final int httpPort;

    public MailHogContainer(String image, int smtpPort, int httpPort, Map<String, String> env) {
        super(image);
        this.smtpPort = smtpPort;
        this.httpPort = httpPort;
        withExposedPorts(smtpPort, httpPort);
        withEnv(env);
    }

    public int getSmtpPort() {
        return getMappedPort(smtpPort);
    }

    public int getHttpPort() {
        return getMappedPort(httpPort);
    }
}
