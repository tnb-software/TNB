package org.jboss.fuse.tnb.sql.common.local;

import org.jboss.fuse.tnb.sql.common.service.SQL;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.WaitStrategy;

public class DBContainer extends GenericContainer<DBContainer> {
    private final int port;

    public DBContainer(SQL service, int port, WaitStrategy waitStrategy) {
        super(service.image());
        this.port = port;
        withExposedPorts(port);
        withEnv(service.containerEnvironment());
        waitingFor(waitStrategy);
    }

    public int getPort() {
        return getMappedPort(port);
    }
}
