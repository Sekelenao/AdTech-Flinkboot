package io.github.sekelenao.demo.properties.part;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.Duration;
import java.util.List;
import java.util.Objects;

/**
 * Validated immutable properties for AdTech Apache Fluss client settings.
 */
public record FlussProperties(
    @NotEmpty List<String> bootstrapServers,
    @NotBlank String database,
    @NotBlank String table,
    @NotNull Duration pollTimeout
) {
    public FlussProperties {
        Objects.requireNonNull(bootstrapServers);
        Objects.requireNonNull(database);
        Objects.requireNonNull(table);
        Objects.requireNonNull(pollTimeout);
    }
}
