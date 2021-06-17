package org.jboss.fuse.tnb.http.resource.local;

import org.jboss.fuse.tnb.common.deployment.Deployable;
import org.jboss.fuse.tnb.http.service.HttpService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.auto.service.AutoService;

@AutoService(HttpService.class)
public class LocalHttp extends HttpService implements Deployable {
    private static final Logger LOG = LoggerFactory.getLogger(HttpService.class);
    private HttpContainer container;

    @Override
    public void deploy() {
        LOG.info("Starting Http container");
        container = new HttpContainer(httpImage());
        container.start();
        LOG.info("Http container started");
    }

    @Override
    public void undeploy() {
        if (container != null) {
            LOG.info("Stopping Http container");
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
    public String httpUrl() {
        return "http://localhost:" + container.getHttpPort() + "/";
    }

    @Override
    public String httpsUrl() {
        return "https://localhost:" + container.getHttpsPort() + "/";
    }
}
