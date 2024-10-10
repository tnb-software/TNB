package software.tnb.opensearch.resource.local;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.LogMessageWaitStrategy;
import org.testcontainers.utility.Base58;
import org.testcontainers.utility.TestcontainersConfiguration;

import java.util.Map;

public class OpensearchContainer extends GenericContainer<OpensearchContainer> {
    public OpensearchContainer(String image, int port, Map<String, String> env) {
        super(image);

        if (TestcontainersConfiguration.getInstance().getEnvironment().get("DOCKER_HOST") != null) {
            // Use network mode "host" so that opensearch can bind itself to the public ip if a remote docker host is used
            withNetworkMode("host");
            // This is not exposed in OpensearchContainer, that's why it is not used
            addFixedExposedPort(port, port);
        } else {
            addExposedPort(port);
        }

        withEnv(env);

        String networkAlias = "opensearch-" + Base58.randomString(6);

        withNetworkAliases(networkAlias);
        withEnv(env);

        String regex = ".*ML configuration initialized successfully.*";
        setWaitStrategy(new LogMessageWaitStrategy().withRegEx(regex));
    }
}
