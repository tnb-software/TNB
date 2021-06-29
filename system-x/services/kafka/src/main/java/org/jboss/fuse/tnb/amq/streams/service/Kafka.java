package org.jboss.fuse.tnb.amq.streams.service;

import org.jboss.fuse.tnb.common.service.Service;

public abstract class Kafka implements Service {

    public abstract String bootstrapServers(boolean tls);

    public abstract void createTopic(String name, int partitions, int replicas);

    public static String kafkaLocalImage() {
        return "quay.io/strimzi/kafka:latest-kafka-2.7.0";
    }

    public static String zookeeperLocalImage() {
        // actually the same image
        return kafkaLocalImage();
    }
}
