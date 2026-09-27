package io.github.sekelenao.demo.scenario;

import io.github.sekelenao.demo.model.Scenario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class ScenarioService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScenarioService.class);

    private final ScenarioResourceLoader scenarioResourceLoader;

    private final ScenarioRunner scenarioRunner;

    private final AtomicBoolean running = new AtomicBoolean(false);

    public ScenarioService(ScenarioResourceLoader scenarioResourceLoader, ScenarioRunner scenarioRunner) {
        this.scenarioResourceLoader = Objects.requireNonNull(scenarioResourceLoader);
        this.scenarioRunner = Objects.requireNonNull(scenarioRunner);
    }

    public List<Scenario> all() {
        try {
            return scenarioResourceLoader.retrieveAll();
        } catch (IOException exception) {
            throw new UncheckedIOException("Failed to load scenarios from resources", exception);
        }
    }

    public Optional<Scenario> get(String name) {
        Objects.requireNonNull(name);
        return all().stream()
            .filter(scenario -> scenario.name().equals(name))
            .findFirst();
    }

    public void run(String name) {
        Objects.requireNonNull(name);
        var scenario = get(name)
            .orElseThrow(() -> new IllegalArgumentException("Scenario not found: " + name));
        if (!running.compareAndSet(false, true)) {
            throw new IllegalStateException("A scenario is already running. Please wait for it to complete.");
        }
        LOGGER.info("Triggering execution of scenario '{}'", scenario.name());
        try {
            scenarioRunner.run(scenario).whenComplete((_, _) -> running.set(false));
        } catch (Exception exception) {
            running.set(false);
            throw exception;
        }
    }

    public boolean isRunning() {
        return running.get();
    }
}
