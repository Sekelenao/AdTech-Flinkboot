package io.github.sekelenao.demo.properties;

import io.github.sekelenao.demo.properties.part.KafkaProperties;
import io.github.sekelenao.demo.properties.part.ScenariosProperties;
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
    @NotNull @Valid KafkaProperties kafka,
    @NotNull @Valid ScenariosProperties scenarios
) {
    public AdtechProperties {
        Objects.requireNonNull(kafka);
        Objects.requireNonNull(scenarios);
    }
}
