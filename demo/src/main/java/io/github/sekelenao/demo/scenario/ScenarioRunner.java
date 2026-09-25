package io.github.sekelenao.demo.scenario;

import io.github.sekelenao.demo.model.Action;
import io.github.sekelenao.demo.model.Scenario;
import io.github.sekelenao.demo.model.ScenarioStep;
import io.github.sekelenao.demo.scenario.handler.StepHandler;
import io.github.sekelenao.demo.util.Sleeps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Orchestrates the sequential execution of scenario steps using registered {@link StepHandler}s.
 */
@Component
public class ScenarioRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScenarioRunner.class);

    private final Map<Action, StepHandler> handlers;

    public ScenarioRunner(List<StepHandler> stepHandlers) {
        Objects.requireNonNull(stepHandlers);
        this.handlers = stepHandlers.stream().collect(Collectors.toMap(StepHandler::action, Function.identity()));
    }

    public void run(Scenario scenario) {
        Objects.requireNonNull(scenario);
        LOGGER.info("Starting execution of scenario '{}' ({} steps)", scenario.name(), scenario.steps().size());
        for (int i = 0; i < scenario.steps().size(); i++) {
            var step = scenario.steps().get(i);
            executeStep(step, i + 1, scenario.steps().size());
        }
        LOGGER.info("Finished execution of scenario '{}'", scenario.name());
    }

    private void executeStep(ScenarioStep step, int stepNumber, int totalSteps) {
        Objects.requireNonNull(step);
        LOGGER.info("[Step {}/{}] Executing {} - {}", stepNumber, totalSteps, step.action(), step.description());
        var handler = handlers.get(step.action());
        if(handler == null){
            throw new IllegalStateException("No handler registered for action: " + step.action());
        }
        handler.handle(step);
        Sleeps.sleep(step.delayAfterMs());
    }
}
