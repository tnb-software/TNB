package software.tnb.kafka.resource.local;

import software.tnb.common.utils.NetworkUtils;

import org.apache.commons.lang3.RandomStringUtils;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.wait.strategy.Wait;

public class ZookeeperContainer extends GenericContainer<ZookeeperContainer> {

    public static final String CONTAINER_NAME = "zookeeper" + RandomStringUtils.randomAlphabetic(5);
    public static final int ZOOKEEPER_PORT = NetworkUtils.getFreePort();

    public ZookeeperContainer(String image, Network network) {
        super(image);

        withNetwork(network);

        withCreateContainerCmdModifier(
            cmd -> {
                cmd.withHostName(CONTAINER_NAME);
                cmd.withName(CONTAINER_NAME);
            }
        );

        addFixedExposedPort(ZOOKEEPER_PORT, ZOOKEEPER_PORT);
        withEnv("LOG_DIR", "/tmp/log");
        // Not nice, but it seems that it is not possible to override zookeeper properties in the same way as kafka properties
        // Also using withCopyFileToContainer / withFileSystemBind resulted in permission errors
        withCommand("sh", "-c", "sed 's/2181/" + ZOOKEEPER_PORT + "/' config/zookeeper.properties > /tmp/zookeeper.properties"
            + " && bin/zookeeper-server-start.sh /tmp/zookeeper.properties"
        );

        waitingFor(Wait.forListeningPort());
    }
}
