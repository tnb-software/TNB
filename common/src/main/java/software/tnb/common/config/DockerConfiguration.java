package software.tnb.common.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Optional;

public class DockerConfiguration extends Configuration {
    private static final Logger LOG = LoggerFactory.getLogger(DockerConfiguration.class);

    public static final String DOCKER_CONFIG_PATH = "docker.config.path";
    public static final String DOCKER_REGISTRY_USERNAME = "docker.registry.username";
    public static final String DOCKER_REGISTRY_PASSWORD = "docker.registry.password";
    public static final String DOCKER_PUSH_RETRIES = "docker.push.retries";

    public static String configPath() {
        return getProperty(DOCKER_CONFIG_PATH, () -> Optional.ofNullable(System.getenv("DOCKER_CONFIG"))
            .orElseGet(() -> System.getProperty("user.home") + File.separator + ".docker"));
    }

    //used in jkube configuration
    public static int pushImageRetries() {
        return Integer.parseInt(getProperty(DOCKER_PUSH_RETRIES, "3"));
    }

    //used in jkube configuration
    public static String dockerRegistryUsername() {
        return getProperty(DOCKER_REGISTRY_USERNAME);
    }

    //used in jkube configuration
    public static String dockerRegistryPassword() {
        return getProperty(DOCKER_REGISTRY_PASSWORD);
    }
}
