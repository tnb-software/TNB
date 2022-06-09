package software.tnb.kafka.configuration;

import software.tnb.common.config.Configuration;

public class RhoasConfiguration extends Configuration {

    public static final String RHOAS_KAFKA_BOOSTRAP_SERVERS = "rhoas.kafka.bootstrap.servers";

    public static String kafkaBootstrapServers() {
        return getProperty(RHOAS_KAFKA_BOOSTRAP_SERVERS);
    }

}
