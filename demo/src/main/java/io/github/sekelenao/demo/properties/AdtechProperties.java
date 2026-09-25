package io.github.sekelenao.demo.properties;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.Objects;

/**
 * Root validated immutable configuration properties for AdTech.
 */
@Validated
@ConfigurationProperties(prefix = "adtech")
public record AdtechProperties(
    @NotNull @Valid KafkaProperties kafka
) {
    public AdtechProperties {
        Objects.requireNonNull(kafka, "kafka must not be null");
    }
}
