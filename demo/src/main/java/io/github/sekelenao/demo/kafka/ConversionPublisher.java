package io.github.sekelenao.demo.kafka;

import io.github.sekelenao.adtech.model.event.Conversion;
import io.github.sekelenao.demo.properties.AdtechProperties;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class ConversionPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private final String topic;

    public ConversionPublisher(KafkaTemplate<String, Object> kafkaTemplate, AdtechProperties properties) {
        this.kafkaTemplate = Objects.requireNonNull(kafkaTemplate);
        this.topic = Objects.requireNonNull(properties).kafka().topics().conversion().events();
    }

    public void publish(Conversion conversion) {
        Objects.requireNonNull(conversion);
        kafkaTemplate.send(topic, conversion.userId, conversion);
    }
}
