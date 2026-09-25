package io.github.sekelenao.demo.properties.part;

import jakarta.validation.constraints.NotBlank;

import java.util.Objects;

/**
 * Validated immutable properties for scenarios resource pattern.
 */
public record ScenariosProperties(
    @NotBlank String pattern
) {
    public ScenariosProperties {
        Objects.requireNonNull(pattern);
    }
}
