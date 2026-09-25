package io.github.sekelenao.demo.scenario.handler;

import io.github.sekelenao.demo.model.Action;
import io.github.sekelenao.demo.model.ScenarioStep;

/**
 * Strategy interface for executing a specific type of scenario step.
 */
public interface StepHandler {

    Action action();

    void handle(ScenarioStep step);

}
