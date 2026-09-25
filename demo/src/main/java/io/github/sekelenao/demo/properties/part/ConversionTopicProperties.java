package io.github.sekelenao.demo.properties.part;

import jakarta.validation.constraints.NotBlank;

import java.util.Objects;

/**
 * Validated immutable properties mapping conversion Kafka topics.
 */
public record ConversionTopicProperties(
    @NotBlank String events
) {
    public ConversionTopicProperties {
        Objects.requireNonNull(events, "events must not be null");
    }
}
