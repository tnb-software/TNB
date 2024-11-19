package software.tnb.kafka.validation;

import software.tnb.common.validation.Validation;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class KafkaValidation<T> implements Validation {
    private static final Logger LOG = LoggerFactory.getLogger(KafkaValidation.class);
    private final KafkaProducer<String, T> producer;
    private final KafkaConsumer<String, T> consumer;

    public KafkaValidation(KafkaProducer<String, T> producer, KafkaConsumer<String, T> consumer) {
        this.producer = producer;
        this.consumer = consumer;
    }

    public void closeProducer() {
        producer.close();
    }

    public void closeConsumer() {
        consumer.close();
    }

    public void produce(String topic, T message) {
        produce(topic, message, Collections.emptyList());
    }

    public void produce(String topic, T message, List<Header> headers) {
        StringBuilder log = new StringBuilder("Producing message \"").append(message).append("\"");
        if (headers != null && !headers.isEmpty()) {
            log.append(" with headers: ");
            log.append(headers.stream().map(h -> h.key() + "=" + new String(h.value())).collect(Collectors.joining(", ")));
        }
        log.append(" to topic \"").append(topic).append("\"");
        LOG.debug(log.toString());
        producer.send(new ProducerRecord<String, T>(topic, null, null, message, headers));
    }

    public void produce(String topic, Integer partition, Long timestamp, T message, List<Header> headers) {
        StringBuilder log = new StringBuilder("Producing message \"").append(message).append("\"");
        if (headers != null && !headers.isEmpty()) {
            log.append(" with headers: ");
            log.append(headers.stream().map(h -> h.key() + "=" + new String(h.value())).collect(Collectors.joining(", ")));
        }
        log.append(" to topic \"").append(topic).append("\"");
        producer.send(new ProducerRecord(topic, partition, timestamp, Long.toString(timestamp), message, headers), new Callback() {
            public void onCompletion(RecordMetadata recordMetadata, Exception e) {
                // executes every time a record is successfully sent or an exception is thrown
                if (e == null) {
                    // the record was successfully sent
                    LOG.debug("Received new metadata. \n"
                        + "Topic:" + recordMetadata.topic() + "\n"
                        + "Partition: " + recordMetadata.partition() + "\n"
                        + "Offset: " + recordMetadata.offset() + "\n"
                        + "Timestamp: " + recordMetadata.timestamp());
                } else {
                    LOG.debug("Error while producing", e);
                }
            }
        });
        LOG.debug(log.toString());
    }

    public void produce(String topic, T message, Map<String, String> headers) {
        produce(topic, message, headers.entrySet().stream()
            .map(e -> new RecordHeader(e.getKey(), e.getValue().getBytes())).collect(Collectors.toList()));
    }

    public List<ConsumerRecord<String, T>> consume(String topic) {
        consumer.subscribe(Collections.singletonList(topic));
        consumer.seekToBeginning(consumer.assignment());
        return StreamSupport.stream(consumer.poll(Duration.ofSeconds(30)).records(topic).spliterator(), false).collect(Collectors.toList());
    }
}
