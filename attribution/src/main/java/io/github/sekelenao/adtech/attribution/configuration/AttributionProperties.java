package io.github.sekelenao.adtech.attribution.configuration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

import java.time.Duration;

/**
 * Business configuration properties for the attribution engine.
 */
public final class AttributionProperties {

    @NotNull
    private final Duration window;

    @JsonCreator
    public AttributionProperties(@JsonProperty("window") Duration window) {
        this.window = window;
    }

    public Duration window() {
        return window;
    }
}
