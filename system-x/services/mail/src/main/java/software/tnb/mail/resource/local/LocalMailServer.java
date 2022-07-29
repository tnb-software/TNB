package software.tnb.mail.resource.local;

import software.tnb.common.deployment.Deployable;
import software.tnb.mail.service.MailServer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.auto.service.AutoService;

@AutoService(MailServer.class)
public class LocalMailServer extends MailServer implements Deployable {
    private static final Logger LOG = LoggerFactory.getLogger(LocalMailServer.class);
    private MailHogContainer container;

    @Override
    public void deploy() {
        LOG.info("Starting MailHog container");
        container = new MailHogContainer(image(), smtpPort(), httpPort());
        container.start();
        LOG.info("MailHog container started");
    }

    @Override
    public void undeploy() {
        if (container != null) {
            LOG.info("Stopping MailHog container");
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

    @Override
    public String hostname() {
        return container.getHost();
    }
}
