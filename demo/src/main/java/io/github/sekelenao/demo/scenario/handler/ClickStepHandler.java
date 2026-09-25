package io.github.sekelenao.demo.scenario.handler;

import io.github.sekelenao.adtech.model.event.AdClick;
import io.github.sekelenao.adtech.model.util.Currencies;
import io.github.sekelenao.demo.kafka.ClickPublisher;
import io.github.sekelenao.demo.model.Action;
import io.github.sekelenao.demo.model.ScenarioStep;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Component
public class ClickStepHandler implements StepHandler {

    private final ClickPublisher clickPublisher;

    public ClickStepHandler(ClickPublisher clickPublisher) {
        this.clickPublisher = Objects.requireNonNull(clickPublisher);
    }

    @Override
    public Action action() {
        return Action.CLICK;
    }

    @Override
    public void handle(ScenarioStep step) {
        Objects.requireNonNull(step);

        AdClick click = new AdClick();
        click.timestamp = Instant.now().toEpochMilli();
        click.clickId = UUID.randomUUID().toString();
        click.impressionId = UUID.randomUUID().toString();
        click.campaignId = step.campaignId();
        click.advertiserId = step.advertiserId();
        click.userId = step.userId();
        click.cost = Currencies.toMicros(step.costEur());

        clickPublisher.publish(click);
    }
}
