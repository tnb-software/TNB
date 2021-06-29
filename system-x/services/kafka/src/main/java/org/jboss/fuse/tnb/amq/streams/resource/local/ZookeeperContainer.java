package org.jboss.fuse.tnb.amq.streams.resource.local;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.wait.strategy.Wait;

public class ZookeeperContainer extends GenericContainer<ZookeeperContainer> {

    public static final String CONTAINER_NAME = "zookeeper";
    public static final int ZOOKEEPER_PORT = 2181;

    public ZookeeperContainer(String image, Network network) {
        super(image);

        withNetwork(network);

        withCreateContainerCmdModifier(
            cmd -> {
                cmd.withHostName(CONTAINER_NAME);
                cmd.withName(CONTAINER_NAME);
            }
        );

        withExposedPorts(ZOOKEEPER_PORT);
        addFixedExposedPort(ZOOKEEPER_PORT, ZOOKEEPER_PORT);
        withEnv("LOG_DIR", "/tmp/log");
        withCommand("sh", "-c", "bin/zookeeper-server-start.sh config/zookeeper.properties");

        waitingFor(Wait.forListeningPort());
    }
}
