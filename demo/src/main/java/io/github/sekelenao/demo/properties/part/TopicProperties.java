package io.github.sekelenao.demo.properties.part;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.Objects;

/**
 * Validated immutable properties aggregating all AdTech Kafka topic categories.
 */
public record TopicProperties(
    @NotNull @Valid CampaignTopicProperties campaign,
    @NotNull @Valid AdTopicProperties ad,
    @NotNull @Valid ConversionTopicProperties conversion
) {
    public TopicProperties {
        Objects.requireNonNull(campaign, "campaign must not be null");
        Objects.requireNonNull(ad, "ad must not be null");
        Objects.requireNonNull(conversion, "conversion must not be null");
    }
}
