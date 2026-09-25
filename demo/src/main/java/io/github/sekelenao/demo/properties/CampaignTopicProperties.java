package io.github.sekelenao.demo.properties;

import jakarta.validation.constraints.NotBlank;

import java.util.Objects;

/**
 * Validated immutable properties mapping campaign Kafka topics.
 */
public record CampaignTopicProperties(
    @NotBlank String budgets
) {
    public CampaignTopicProperties {
        Objects.requireNonNull(budgets, "budgets must not be null");
    }
}
