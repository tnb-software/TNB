package software.tnb.splunk.resource.local;

import software.tnb.common.account.AccountFactory;
import software.tnb.common.deployment.Deployable;
import software.tnb.splunk.account.SplunkAccount;
import software.tnb.splunk.service.Splunk;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.auto.service.AutoService;

import java.util.Map;

@AutoService(Splunk.class)
public class LocalSplunk extends Splunk implements Deployable {

    private static final Logger LOG = LoggerFactory.getLogger(LocalSplunk.class);

    private SplunkContainer container;

    private final int containerApiPort = 8089;
    private static final String PASSWORD = "password";

    @Override
    public void deploy() {
        LOG.info("Starting Splunk container");
        container = new SplunkContainer(image(), containerApiPort, containerEnvironment());
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
    public void openResources() {
        // nothing to do
    }

    @Override
    public void closeResources() {
        // nothing to do
    }

    @Override
    public SplunkAccount account() {
        if (account == null) {
            account = AccountFactory.create(SplunkAccount.class);
            account.setPassword(PASSWORD);
        }
        return account;
    }

    // SSL is disabled for local deployment because the app has self-signed certificate and Splunk client throws
    // "SunCertPathBuilderException: unable to find valid certification path to requested target" exception
    @Override
    public String apiSchema() {
        return "http";
    }

    public Map<String, String> containerEnvironment() {
        return Map.of(
            "SPLUNK_START_ARGS", "--accept-license",
            "SPLUNKD_SSL_ENABLE", "false",
            "SPLUNK_PASSWORD", PASSWORD
        );
    }

    @Override
    public String externalHostname() {
        return "localhost";
    }

    @Override
    public int apiPort() {
        return container.getMappedPort(containerApiPort);
    }
}
