package io.github.sekelenao.demo.model;

import java.util.List;
import java.util.Objects;

/**
 * Immutable record representing a scenario file definition with its ordered steps.
 */
public record Scenario(String name, String description, List<ScenarioStep> steps) {

    public Scenario {
        Objects.requireNonNull(name, "name must not be null");
        Objects.requireNonNull(description, "description must not be null");
        Objects.requireNonNull(steps, "steps must not be null");
    }

}
