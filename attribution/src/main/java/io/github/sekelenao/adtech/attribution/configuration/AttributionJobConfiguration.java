package io.github.sekelenao.adtech.attribution.configuration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.sekelenao.flinkboot.core.api.properties.JobProperties;
import io.github.sekelenao.flinkboot.kafka.api.properties.sink.KafkaSinkProperties;
import io.github.sekelenao.flinkboot.kafka.api.properties.source.KafkaSourceProperties;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

/**
 * Immutable root configuration model for the last-click attribution streaming job.
 * Validated fail-fast at startup by Flinkboot using Jakarta Bean Validation.
 */
public final class AttributionJobConfiguration {

    @Valid
    @NotNull
    private final JobProperties job;

    @Valid
    @NotNull
    private final AttributionProperties attribution;

    @Valid
    @NotNull
    private final KafkaSourceProperties clicksSource;

    @Valid
    @NotNull
    private final KafkaSourceProperties conversionsSource;

    @Valid
    @NotNull
    private final KafkaSinkProperties attributedConversionsSink;

    @JsonCreator
    public AttributionJobConfiguration(
            @JsonProperty("job") JobProperties job,
            @JsonProperty("attribution") AttributionProperties attribution,
            @JsonProperty("clicks-source") KafkaSourceProperties clicksSource,
            @JsonProperty("conversions-source") KafkaSourceProperties conversionsSource,
            @JsonProperty("attributed-conversions-sink") KafkaSinkProperties attributedConversionsSink
    ) {
        this.job = job;
        this.attribution = attribution;
        this.clicksSource = clicksSource;
        this.conversionsSource = conversionsSource;
        this.attributedConversionsSink = attributedConversionsSink;
    }

    public JobProperties job() {
        return job;
    }

    public AttributionProperties attribution() {
        return attribution;
    }

    public KafkaSourceProperties clicksSource() {
        return clicksSource;
    }

    public KafkaSourceProperties conversionsSource() {
        return conversionsSource;
    }

    public KafkaSinkProperties attributedConversionsSink() {
        return attributedConversionsSink;
    }
}
