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
public class BurstClicksStepHandler implements StepHandler {

    private final ClickPublisher clickPublisher;

    public BurstClicksStepHandler(ClickPublisher clickPublisher) {
        this.clickPublisher = Objects.requireNonNull(clickPublisher);
    }

    @Override
    public Action action() {
        return Action.BURST_CLICKS;
    }

    @Override
    public void handle(ScenarioStep step) {
        Objects.requireNonNull(step);

        for (int i = 0; i < step.count(); i++) {
            AdClick click = new AdClick();
            click.timestamp = Instant.now().toEpochMilli();
            click.clickId = UUID.randomUUID().toString();
            click.impressionId = UUID.randomUUID().toString();
            click.campaignId = step.campaignId();
            click.advertiserId = step.advertiserId();
            click.userId = step.userId();
            click.cost = Currencies.toMicros(step.costEur());

            clickPublisher.publish(click);

            if (step.delayBetweenMs() > 0 && i < step.count() - 1) {
                try {
                    Thread.sleep(step.delayBetweenMs());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }
}
