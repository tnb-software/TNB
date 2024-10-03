package software.tnb.searchengine.common.resource.local;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.LogMessageWaitStrategy;
import org.testcontainers.utility.TestcontainersConfiguration;

import java.util.Map;

public class SearchContainer extends GenericContainer<SearchContainer> {

    public SearchContainer(String image, int port,
        Map<String, String> environment, String regex, String networkAliases) {
        super(image);

        if (TestcontainersConfiguration.getInstance().getEnvironment().get("DOCKER_HOST") != null) {
            // Use network mode "host" so that elasticsearch can bind itself to the public ip if a remote docker host is used
            withNetworkMode("host");
            // This is not exposed in SearchContainer, that's why it is not used
            addFixedExposedPort(port, port);
        } else {
            addExposedPort(port);
        }

        withEnv(environment);

        // This is a copy of testcontainers' elasticsearch container init
        withNetworkAliases(networkAliases);

        // regex that
        //   matches 8.3 JSON logging with started message and some follow up content within the message field
        //   matches 8.0 JSON logging with no whitespace between message field and content
        //   matches 7.x JSON logging with whitespace between message field and content
        //   matches 6.x text logging with node name in brackets and just a 'started' message till the end of the line
        setWaitStrategy(new LogMessageWaitStrategy().withRegEx(regex));
    }
}
