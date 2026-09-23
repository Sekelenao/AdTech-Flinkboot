package io.github.sekelenao.adtech.budget.configuration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.sekelenao.flinkboot.core.api.properties.JobProperties;
import io.github.sekelenao.flinkboot.fluss.api.properties.sink.FlussSinkProperties;
import io.github.sekelenao.flinkboot.kafka.api.properties.sink.KafkaSinkProperties;
import io.github.sekelenao.flinkboot.kafka.api.properties.source.KafkaSourceProperties;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

/**
 * Immutable root configuration model for the budget and pacing streaming job.
 * Validated fail-fast at startup by Flinkboot using Jakarta Bean Validation.
 */
public final class BudgetJobConfiguration {

    @Valid
    @NotNull
    private final JobProperties job;

    @Valid
    @NotNull
    private final KafkaSourceProperties campaignBudgetSource;

    @Valid
    @NotNull
    private final KafkaSourceProperties impressionsSource;

    @Valid
    @NotNull
    private final KafkaSourceProperties clicksSource;

    @Valid
    @NotNull
    private final KafkaSinkProperties alertsSink;

    @Valid
    @NotNull
    private final FlussSinkProperties flussSink;

    @JsonCreator
    public BudgetJobConfiguration(
            @JsonProperty("job") JobProperties job,
            @JsonProperty("campaign-budget-source") KafkaSourceProperties campaignBudgetSource,
            @JsonProperty("impressions-source") KafkaSourceProperties impressionsSource,
            @JsonProperty("clicks-source") KafkaSourceProperties clicksSource,
            @JsonProperty("alerts-sink") KafkaSinkProperties alertsSink,
            @JsonProperty("fluss-sink") FlussSinkProperties flussSink
    ) {
        this.job = job;
        this.campaignBudgetSource = campaignBudgetSource;
        this.impressionsSource = impressionsSource;
        this.clicksSource = clicksSource;
        this.alertsSink = alertsSink;
        this.flussSink = flussSink;
    }

    public JobProperties job() {
        return job;
    }

    public KafkaSourceProperties campaignBudgetSource() {
        return campaignBudgetSource;
    }

    public KafkaSourceProperties impressionsSource() {
        return impressionsSource;
    }

    public KafkaSourceProperties clicksSource() {
        return clicksSource;
    }

    public KafkaSinkProperties alertsSink() {
        return alertsSink;
    }

    public FlussSinkProperties flussSink() {
        return flussSink;
    }
}
