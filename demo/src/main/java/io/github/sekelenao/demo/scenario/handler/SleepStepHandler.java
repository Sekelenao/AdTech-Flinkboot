package io.github.sekelenao.demo.scenario.handler;

import io.github.sekelenao.demo.model.Action;
import io.github.sekelenao.demo.model.ScenarioStep;
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

        long duration = step.delayAfterMs() > 0 ? step.delayAfterMs() : step.delayBetweenMs();
        if (duration > 0) {
            try {
                Thread.sleep(duration);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
