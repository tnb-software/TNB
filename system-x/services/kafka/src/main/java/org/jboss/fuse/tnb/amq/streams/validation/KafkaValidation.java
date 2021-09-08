package org.jboss.fuse.tnb.amq.streams.validation;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class KafkaValidation {
    private final KafkaProducer<String, String> producer;
    private final KafkaConsumer<String, String> consumer;

    public KafkaValidation(KafkaProducer<String, String> producer, KafkaConsumer<String, String> consumer) {
        this.producer = producer;
        this.consumer = consumer;
    }

    public void produce(String topic, String message) {
        producer.send(new ProducerRecord<>(topic, message));
    }

    public List<ConsumerRecord<String, String>> consume(String topic) {
        consumer.subscribe(Collections.singletonList(topic));
        consumer.seekToBeginning(consumer.assignment());
        return StreamSupport.stream(consumer.poll(Duration.ofSeconds(30)).records(topic).spliterator(), false).collect(Collectors.toList());
    }
}
