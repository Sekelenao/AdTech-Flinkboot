package io.github.sekelenao.demo.scenario.handler;

import io.github.sekelenao.demo.model.Action;
import io.github.sekelenao.demo.model.ScenarioStep;
import io.github.sekelenao.demo.util.Sleeps;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class SleepStepHandler implements StepHandler {

    @Override
    public Action action() {
        return Action.SLEEP;
    }

    @Override
    public void handle(ScenarioStep step) {
        Objects.requireNonNull(step);
        Sleeps.sleep(step.delayBetweenMs());
    }
}
