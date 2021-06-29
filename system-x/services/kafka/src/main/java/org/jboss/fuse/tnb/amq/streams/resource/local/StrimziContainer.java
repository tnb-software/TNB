
package org.jboss.fuse.tnb.amq.streams.resource.local;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.wait.strategy.Wait;

public class StrimziContainer extends GenericContainer<StrimziContainer> {

    private static final String CONTAINER_NAME = "kafka";
    private static final int KAFKA_PORT = 9092;

    public StrimziContainer(String image, Network network) {
        super(image);

        withNetwork(network);

        withCreateContainerCmdModifier(
            cmd -> {
                cmd.withHostName(CONTAINER_NAME);
                cmd.withName(CONTAINER_NAME);
            }
        );

        withExposedPorts(KAFKA_PORT);
        addFixedExposedPort(KAFKA_PORT, KAFKA_PORT);
        withEnv("LOG_DIR", "/tmp/log");
        withCommand("sh", "-c", String.format("bin/kafka-server-start.sh config/server.properties "
                + "--override zookeeper.connect=%s:%d "
                + "--override advertised.listeners=PLAINTEXT://%s:%d",
            ZookeeperContainer.CONTAINER_NAME, ZookeeperContainer.ZOOKEEPER_PORT,
            "localhost", KAFKA_PORT));

        waitingFor(Wait.forListeningPort());
    }

    public int getKafkaPort() {
        return getMappedPort(KAFKA_PORT);
    }
}
