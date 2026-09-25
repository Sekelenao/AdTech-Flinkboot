package io.github.sekelenao.demo.scenario.handler;

import io.github.sekelenao.adtech.model.event.AdImpression;
import io.github.sekelenao.adtech.model.util.Currencies;
import io.github.sekelenao.demo.kafka.ImpressionPublisher;
import io.github.sekelenao.demo.model.Action;
import io.github.sekelenao.demo.model.ScenarioStep;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Component
public class ImpressionStepHandler implements StepHandler {

    private final ImpressionPublisher impressionPublisher;

    public ImpressionStepHandler(ImpressionPublisher impressionPublisher) {
        this.impressionPublisher = Objects.requireNonNull(impressionPublisher);
    }

    @Override
    public Action action() {
        return Action.IMPRESSION;
    }

    @Override
    public void handle(ScenarioStep step) {
        Objects.requireNonNull(step);

        AdImpression impression = new AdImpression();
        impression.timestamp = Instant.now().toEpochMilli();
        impression.impressionId = UUID.randomUUID().toString();
        impression.campaignId = step.campaignId();
        impression.advertiserId = step.advertiserId();
        impression.userId = step.userId();
        impression.cost = Currencies.toMicros(step.costEur());

        impressionPublisher.publish(impression);
    }
}
