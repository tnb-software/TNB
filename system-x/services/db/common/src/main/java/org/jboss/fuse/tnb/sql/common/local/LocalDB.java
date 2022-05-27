package org.jboss.fuse.tnb.sql.common.local;

import org.jboss.fuse.tnb.common.deployment.Deployable;
import org.jboss.fuse.tnb.sql.common.service.SQL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.wait.strategy.WaitStrategy;

public class LocalDB implements Deployable {
    private static final Logger LOG = LoggerFactory.getLogger(LocalDB.class);
    private final SQL sqlService;
    private final DBContainer container;

    public LocalDB(SQL sqlService, int port, WaitStrategy waitStrategy) {
        this.sqlService = sqlService;
        this.container = new DBContainer(sqlService, port, waitStrategy);
    }

    @Override
    public void deploy() {
        LOG.info("Starting " + sqlService.name() + " container");
        container.start();
        LOG.info(sqlService.name() + " container started");
    }

    @Override
    public void undeploy() {
        container.stop();
    }

    @Override
    public void openResources() {
        // no-op
    }

    @Override
    public void closeResources() {
        // no-op
    }

    public int getPort() {
        return container.getPort();
    }
}
