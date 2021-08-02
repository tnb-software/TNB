package org.jboss.fuse.tnb.common.config;

public class RhoasConfiguration extends Configuration {

    public static final String RHOAS_KAFKA_BOOSTRAP_SERVERS = "rhoas.kafka.bootstrap.servers";

    public static String kafkaBootstrapServers() {
        return getProperty(RHOAS_KAFKA_BOOSTRAP_SERVERS);
    }

}
