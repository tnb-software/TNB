package software.tnb.cryostat.resource.local;

import software.tnb.common.deployment.Deployable;
import software.tnb.common.deployment.WithDockerImage;
import software.tnb.common.utils.HTTPUtils;
import software.tnb.common.utils.WaitUtils;
import software.tnb.cryostat.client.CryostatClient;
import software.tnb.cryostat.client.local.LocalCryostatClient;
import software.tnb.cryostat.service.Cryostat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.auto.service.AutoService;

import java.util.HashMap;
import java.util.Map;

@AutoService(Cryostat.class)
public class LocalCryostat extends Cryostat implements Deployable, WithDockerImage {
    private static final Logger LOG = LoggerFactory.getLogger(LocalCryostat.class);
    private CryostatContainer container;

    @Override
    public void deploy() {
        LOG.info("Starting Cryostat");
        container = new CryostatContainer(image(), containerEnvironment());
        container.start();
        LOG.info("Cryostat container started");
    }

    @Override
    public void undeploy() {
        if (container != null) {
            LOG.info("Stopping Cryostat container");
            container.stop();
        }
    }

    /**
     * Open all resources needed after the service is deployed - initialize clients and stuff.
     */
    @Override
    public void openResources() {
        final HTTPUtils client = HTTPUtils.getInstance(HTTPUtils.trustAllSslClient());
        WaitUtils.waitFor(() -> client.get(String.format("%s/health", connectionUrl()), false).isSuccessful()
            , "wait for container ready");
        validation().init();
    }

    /**
     * Close all resources used after before the service is undeployed.
     */
    @Override
    public void closeResources() {

    }

    @Override
    public String connectionUrl() {
        return String.format("http://localhost:%s", getPortMapping(8181));
    }

    @Override
    public CryostatClient client() {
        return new LocalCryostatClient(connectionUrl());
    }

    @Override
    public int getPortMapping(int port) {
        return port; //use fixed port because of the cryostat container using host net
    }

    protected Map<String, String> containerEnvironment() {
        final Map<String, String> env = new HashMap<>();
        env.put("CRYOSTAT_DISABLE_JMX_AUTH", "true");
        env.put("CRYOSTAT_ALLOW_UNTRUSTED_SSL", "true");
        env.put("CRYOSTAT_DISABLE_SSL", "true");
        return env;
    }

    public String defaultImage() {
        return "registry.redhat.io/cryostat-tech-preview/cryostat-rhel8:latest";
    }
}
