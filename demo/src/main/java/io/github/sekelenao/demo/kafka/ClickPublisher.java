package io.github.sekelenao.demo.kafka;

import io.github.sekelenao.adtech.model.event.AdClick;
import io.github.sekelenao.demo.properties.AdtechProperties;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class ClickPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private final String topic;

    public ClickPublisher(KafkaTemplate<String, Object> kafkaTemplate, AdtechProperties properties) {
        this.kafkaTemplate = Objects.requireNonNull(kafkaTemplate);
        this.topic = Objects.requireNonNull(properties).kafka().topics().ad().clicks();
    }

    public void publish(AdClick click) {
        Objects.requireNonNull(click);
        kafkaTemplate.send(topic, click.userId, click);
    }
}
