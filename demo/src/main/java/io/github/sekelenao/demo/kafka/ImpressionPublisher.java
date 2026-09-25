package io.github.sekelenao.demo.kafka;

import io.github.sekelenao.adtech.model.event.AdImpression;
import io.github.sekelenao.demo.properties.AdtechProperties;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class ImpressionPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private final String topic;

    public ImpressionPublisher(KafkaTemplate<String, Object> kafkaTemplate, AdtechProperties properties) {
        this.kafkaTemplate = Objects.requireNonNull(kafkaTemplate);
        this.topic = Objects.requireNonNull(properties).kafka().topics().ad().impressions();
    }

    public void publish(AdImpression impression) {
        Objects.requireNonNull(impression);
        kafkaTemplate.send(topic, impression.campaignId, impression);
    }
}
