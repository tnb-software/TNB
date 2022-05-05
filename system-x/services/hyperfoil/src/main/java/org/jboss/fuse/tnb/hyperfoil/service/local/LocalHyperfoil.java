package org.jboss.fuse.tnb.hyperfoil.service.local;

import org.jboss.fuse.tnb.common.deployment.Deployable;
import org.jboss.fuse.tnb.hyperfoil.service.Hyperfoil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.auto.service.AutoService;

import java.util.HashMap;

@AutoService(Hyperfoil.class)
public class LocalHyperfoil extends Hyperfoil implements Deployable {
    private static final Logger LOG = LoggerFactory.getLogger(LocalHyperfoil.class);
    private HyperfoilContainer container;

    @Override
    public void deploy() {
        LOG.info("Starting Hyperfoil");
        container = new HyperfoilContainer(new HashMap<>(), containerPorts());
        container.start();
        LOG.info("Hyperfoil container started");
    }

    @Override
    public void undeploy() {
        if (container != null) {
            LOG.info("Stopping Hyperfoil container");
            container.stop();
        }
    }

    @Override
    public void openResources() {
    }

    @Override
    public void closeResources() {
    }

    @Override
    public String hyperfoilUrl() {
        return "localhost";
    }

    @Override
    public String connection() {
        return "http://" + hyperfoilUrl() + ":" + getPortMapping(8090) + "/";
    }

    @Override
    public int getPortMapping(int port) {
        return 8090;
    }

    private int[] containerPorts() {
        int[] ports = {8090};
        return ports;
    }
}
