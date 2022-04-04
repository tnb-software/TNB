package org.jboss.fuse.tnb.amq.streams.service;

import org.jboss.fuse.tnb.amq.streams.account.KafkaAccount;
import org.jboss.fuse.tnb.amq.streams.validation.KafkaValidation;
import org.jboss.fuse.tnb.common.account.Accounts;
import org.jboss.fuse.tnb.common.service.Service;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;
import java.util.UUID;

public abstract class Kafka implements Service {
    private KafkaAccount account;
    protected KafkaValidation validation;
    protected KafkaProducer<String, String> producer;
    protected KafkaConsumer<String, String> consumer;

    public abstract String bootstrapServers();

    public abstract String bootstrapSSLServers();

    public abstract void createTopic(String name, int partitions, int replicas);

    public static String kafkaLocalImage() {
        return "quay.io/strimzi/kafka:latest-kafka-2.7.0";
    }

    public static String zookeeperLocalImage() {
        // actually the same image
        return kafkaLocalImage();
    }

    public KafkaAccount account() {
        if (account == null) {
            account = Accounts.get(KafkaAccount.class);
        }
        return account;
    }

    public abstract KafkaValidation validation();

    protected Properties defaultClientProperties() {
        Properties props = new Properties();
        props.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.setProperty(ConsumerConfig.GROUP_ID_CONFIG, UUID.randomUUID().toString());
        props.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        return props;
    }
}
