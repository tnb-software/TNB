package software.tnb.splunk.resource.local;

import software.tnb.common.deployment.Deployable;
import software.tnb.splunk.service.Splunk;
import software.tnb.splunk.service.configuration.SplunkProtocol;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.auto.service.AutoService;

import java.util.Map;

@AutoService(Splunk.class)
public class LocalSplunk extends Splunk implements Deployable {

    private static final Logger LOG = LoggerFactory.getLogger(LocalSplunk.class);

    private SplunkContainer container;

    @Override
    public void defaultConfiguration() {
        getConfiguration().protocol(SplunkProtocol.HTTP);
    }

    @Override
    public void deploy() {
        if (SplunkProtocol.HTTPS == getConfiguration().getProtocol()) {
            throw new IllegalStateException("HTTPS protocol is not implemented for Local Splunk service! Use HTTP.");
        }
        LOG.info("Starting Splunk container");
        container = new SplunkContainer(image(), PORT, containerEnvironment());
        container.start();
        LOG.info("Splunk container started");
    }

    @Override
    public void undeploy() {
        if (container != null) {
            LOG.info("Stopping Splunk container");
            container.stop();
        }
    }

    @Override
    public String host() {
        return container.getHost();
    }

    @Override
    public int port() {
        return container.getMappedPort(PORT);
    }

    public Map<String, String> containerEnvironment() {
        return Map.of(
            "SPLUNK_START_ARGS", "--accept-license",
            "SPLUNKD_SSL_ENABLE", "false",
            "SPLUNK_PASSWORD", account().password()
        );
    }
}
