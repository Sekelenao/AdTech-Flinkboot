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

@Service
public class ScenarioService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScenarioService.class);

    private final ScenarioResourceLoader scenarioResourceLoader;

    private final ScenarioRunner scenarioRunner;

    public ScenarioService(ScenarioResourceLoader scenarioResourceLoader, ScenarioRunner scenarioRunner) {
        this.scenarioResourceLoader = Objects.requireNonNull(scenarioResourceLoader);
        this.scenarioRunner = Objects.requireNonNull(scenarioRunner);
    }

    public List<Scenario> listScenarios() {
        try {
            return scenarioResourceLoader.retrieveAll();
        } catch (IOException exception) {
            throw new UncheckedIOException("Failed to load scenarios from resources", exception);
        }
    }

    public Optional<Scenario> getScenario(String name) {
        Objects.requireNonNull(name);
        return listScenarios().stream()
            .filter(scenario -> scenario.name().equals(name))
            .findFirst();
    }

    public void runScenario(String name) {
        Objects.requireNonNull(name);
        var scenario = getScenario(name)
            .orElseThrow(() -> new IllegalArgumentException("Scenario not found: " + name));
        LOGGER.info("Triggering execution of scenario '{}'", scenario.name());
        scenarioRunner.run(scenario);
    }

    public void reloadScenarios() {
        LOGGER.info("Evicting scenario cache and reloading");
        scenarioResourceLoader.reload();
    }
}
