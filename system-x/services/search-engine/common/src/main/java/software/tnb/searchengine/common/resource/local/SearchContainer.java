package software.tnb.searchengine.common.resource.local;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.LogMessageWaitStrategy;

import java.util.Map;

public class SearchContainer extends GenericContainer<SearchContainer> {

    public SearchContainer(String image, int port,
        Map<String, String> environment, String regex, String networkAliases) {
        super(image);

        withNetworkMode("host");
        addFixedExposedPort(port, port);

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
