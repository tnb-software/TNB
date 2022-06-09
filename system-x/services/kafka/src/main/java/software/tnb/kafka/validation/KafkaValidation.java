package software.tnb.kafka.validation;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
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

public class KafkaValidation {
    private static final Logger LOG = LoggerFactory.getLogger(KafkaValidation.class);
    private final KafkaProducer<String, String> producer;
    private final KafkaConsumer<String, String> consumer;

    public KafkaValidation(KafkaProducer<String, String> producer, KafkaConsumer<String, String> consumer) {
        this.producer = producer;
        this.consumer = consumer;
    }

    public void produce(String topic, String message) {
        produce(topic, message, Collections.emptyList());
    }

    public void produce(String topic, String message, List<Header> headers) {
        StringBuilder log = new StringBuilder("Producing message \"").append(message).append("\"");
        if (headers != null && !headers.isEmpty()) {
            log.append(" with headers: ");
            log.append(headers.stream().map(h -> h.key() + "=" + new String(h.value())).collect(Collectors.joining(", ")));
        }
        log.append(" to topic \"").append(topic).append("\"");
        LOG.debug(log.toString());
        producer.send(new ProducerRecord<String, String>(topic, null, null, message, headers));
    }

    public void produce(String topic, String message, Map<String, String> headers) {
        produce(topic, message, headers.entrySet().stream()
            .map(e -> new RecordHeader(e.getKey(), e.getValue().getBytes())).collect(Collectors.toList()));
    }

    public List<ConsumerRecord<String, String>> consume(String topic) {
        consumer.subscribe(Collections.singletonList(topic));
        consumer.seekToBeginning(consumer.assignment());
        return StreamSupport.stream(consumer.poll(Duration.ofSeconds(30)).records(topic).spliterator(), false).collect(Collectors.toList());
    }
}
