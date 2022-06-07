package org.jboss.fuse.tnb.cryostat.service.local;

import org.jboss.fuse.tnb.common.deployment.Deployable;
import org.jboss.fuse.tnb.common.utils.HTTPUtils;
import org.jboss.fuse.tnb.common.utils.WaitUtils;
import org.jboss.fuse.tnb.cryostat.service.Cryostat;
import org.jboss.fuse.tnb.cryostat.service.CryostatClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.auto.service.AutoService;

import java.util.HashMap;
import java.util.Map;

@AutoService(Cryostat.class)
public class CryostatLocal extends Cryostat implements Deployable {
    private static final Logger LOG = LoggerFactory.getLogger(CryostatLocal.class);
    private CryostatContainer container;

    @Override
    public void deploy() {
        LOG.info("Starting Cryostat");
        container = new CryostatContainer(containerEnvironment(), containerPorts());
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

    private int[] containerPorts() {
        int[] ports = {8181, 9091};
        return ports;
    }

    protected Map<String, String> containerEnvironment() {
        final Map<String, String> env = new HashMap<>();
        env.put("CRYOSTAT_DISABLE_JMX_AUTH", "true");
        env.put("CRYOSTAT_ALLOW_UNTRUSTED_SSL", "true");
        env.put("CRYOSTAT_DISABLE_SSL", "true");
        return env;
    }

}
