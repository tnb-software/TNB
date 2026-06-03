
package software.tnb.kafka.resource.local;

import software.tnb.common.utils.NetworkUtils;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.TestcontainersConfiguration;

public class StrimziContainer extends GenericContainer<StrimziContainer> {

    private static final String CONTAINER_NAME = "kafka" + RandomStringUtils.randomAlphabetic(5);
    private static final int KAFKA_PORT = NetworkUtils.getFreePort();

    public StrimziContainer(String image) {
        super(image);

        withCreateContainerCmdModifier(
            cmd -> {
                cmd.withHostName(CONTAINER_NAME);
                cmd.withName(CONTAINER_NAME);
            }
        );

        String listenerAddress = "localhost";
        String dockerHost = TestcontainersConfiguration.getInstance().getEnvironment().get("DOCKER_HOST");
        if (dockerHost != null && dockerHost.contains("tcp://")) {
            listenerAddress = StringUtils.substringBetween(dockerHost, "tcp://", ":");
        }
        addFixedExposedPort(KAFKA_PORT, KAFKA_PORT);
        withEnv("LOG_DIR", "/tmp/log");
        withCommand("sh", "-c", String.format(
            "sed"
                + " -e 's|^listeners=.*|listeners=PLAINTEXT://:%d,CONTROLLER://:9093|'"
                + " -e 's|^advertised.listeners=.*|advertised.listeners=PLAINTEXT://%s:%d|'"
                + " config/server.properties > /tmp/server.properties"
                + " && CLUSTER_ID=$(bin/kafka-storage.sh random-uuid)"
                + " && bin/kafka-storage.sh format --standalone -t $CLUSTER_ID -c /tmp/server.properties"
                + " && bin/kafka-server-start.sh /tmp/server.properties",
            KAFKA_PORT, listenerAddress, KAFKA_PORT));

        waitingFor(Wait.forListeningPort());
    }

    public int getKafkaPort() {
        return getMappedPort(KAFKA_PORT);
    }
}
