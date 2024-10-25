package software.tnb.elasticsearch.resource.local;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.LogMessageWaitStrategy;
import org.testcontainers.utility.Base58;
import org.testcontainers.utility.TestcontainersConfiguration;

import java.util.Map;

public class ElasticsearchContainer extends GenericContainer<ElasticsearchContainer> {
    public ElasticsearchContainer(String image, int port, String password) {
        super(image);

        if (TestcontainersConfiguration.getInstance().getEnvironment().get("DOCKER_HOST") != null) {
            // Use network mode "host" so that elasticsearch can bind itself to the public ip if a remote docker host is used
            withNetworkMode("host");
            // This is not exposed in ElasticsearchContainer, that's why it is not used
            addFixedExposedPort(port, port);
        } else {
            addExposedPort(port);
        }

        withEnv(Map.of(
            "ELASTIC_PASSWORD", password,
            "xpack.security.enabled", "true"
        ));

        // This is a copy of testcontainers' elasticsearch container init
        withNetworkAliases("elasticsearch-" + Base58.randomString(6));
        withEnv("discovery.type", "single-node");

        // regex that
        //   matches 8.3 JSON logging with started message and some follow up content within the message field
        //   matches 8.0 JSON logging with no whitespace between message field and content
        //   matches 7.x JSON logging with whitespace between message field and content
        //   matches 6.x text logging with node name in brackets and just a 'started' message till the end of the line
        String regex = ".*(\"message\":\\s?\"started[\\s?|\"].*|] started\n$)";
        setWaitStrategy(new LogMessageWaitStrategy().withRegEx(regex));
    }
}
