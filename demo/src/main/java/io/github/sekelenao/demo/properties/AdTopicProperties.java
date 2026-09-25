package io.github.sekelenao.demo.properties;

import jakarta.validation.constraints.NotBlank;

import java.util.Objects;

/**
 * Validated immutable properties mapping ad Kafka topics.
 */
public record AdTopicProperties(
    @NotBlank String impressions,
    @NotBlank String clicks
) {
    public AdTopicProperties {
        Objects.requireNonNull(impressions, "impressions must not be null");
        Objects.requireNonNull(clicks, "clicks must not be null");
    }
}
