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
        var stepsAmount = scenario.steps().size();
        LOGGER.info("Starting execution of scenario '{}' ({} steps)", scenario.name(), stepsAmount);
        for (int i = 0; i < stepsAmount; i++) {
            var step = scenario.steps().get(i);
            LOGGER.info("[Step {}/{}] Executing {} - {}", i + 1, stepsAmount, step.action(), step.description());
            var handler = handlers.get(step.action());
            if(handler == null){
                throw new IllegalStateException("No handler registered for action: " + step.action());
            }
            handler.handle(step);
            step.delayAfterMs().ifPresent(Sleeps::sleep);
        }
        LOGGER.info("Finished execution of scenario '{}'", scenario.name());
    }

}
