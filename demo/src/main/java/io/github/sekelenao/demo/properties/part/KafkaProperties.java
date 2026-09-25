package io.github.sekelenao.demo.properties.part;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.Objects;

/**
 * Validated immutable properties for AdTech Kafka settings.
 */
public record KafkaProperties(
    @NotNull @Valid TopicProperties topics
) {
    public KafkaProperties {
        Objects.requireNonNull(topics, "topics must not be null");
    }
}
