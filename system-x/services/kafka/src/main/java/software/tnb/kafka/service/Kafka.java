package software.tnb.kafka.service;

import software.tnb.common.client.NoClient;
import software.tnb.common.service.Service;
import software.tnb.kafka.account.KafkaAccount;
import software.tnb.kafka.validation.KafkaValidation;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

public abstract class Kafka extends Service<KafkaAccount, NoClient, KafkaValidation<?>> {
    protected Map<Class<?>, KafkaValidation> validations;
    protected Properties props = defaultClientProperties();

    public abstract String bootstrapServers();

    public abstract String bootstrapSSLServers();

    public abstract void createTopic(String name, int partitions, int replicas);

    public <T> KafkaValidation<T> validation(Class<T> clazz) {
        if (!validations.containsKey(clazz)) {
            validations.put(clazz, createValidation(clazz));
        }
        return validations.get(clazz);
    }

    public KafkaValidation<String> validation() {
        return validation(String.class);
    }

    private <T> KafkaValidation<T> createValidation(Class<T> clazz) {
        if (clazz.isInstance(new byte[0])) {
            props.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class.getName());
            props.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ByteArrayDeserializer.class.getName());
        } else if (clazz.isInstance("")) {
            props.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
            props.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        } else {
            throw new IllegalArgumentException("Unsupported class type passed to validation() method: " + clazz.getName());
        }
        return new KafkaValidation<>(new KafkaProducer<>(props), new KafkaConsumer<>(props));
    }

    protected Properties defaultClientProperties() {
        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, UUID.randomUUID().toString());
        properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        return properties;
    }

    public void openResources() {
        validations = new HashMap<>();
    }

    public void closeResources() {
        if (validations != null) {
            validations.values().forEach(validation -> {
                validation.closeProducer();
                validation.closeConsumer();
            });
            validations = null;
        }
    }
}
