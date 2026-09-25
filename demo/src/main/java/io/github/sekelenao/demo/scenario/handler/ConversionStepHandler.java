package io.github.sekelenao.demo.scenario.handler;

import io.github.sekelenao.adtech.model.event.Conversion;
import io.github.sekelenao.adtech.model.util.Currencies;
import io.github.sekelenao.demo.kafka.ConversionPublisher;
import io.github.sekelenao.demo.model.Action;
import io.github.sekelenao.demo.model.ScenarioStep;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Component
public class ConversionStepHandler implements StepHandler {

    private final ConversionPublisher conversionPublisher;

    public ConversionStepHandler(ConversionPublisher conversionPublisher) {
        this.conversionPublisher = Objects.requireNonNull(conversionPublisher);
    }

    @Override
    public Action action() {
        return Action.CONVERSION;
    }

    @Override
    public void handle(ScenarioStep step) {
        Objects.requireNonNull(step);

        Conversion conversion = new Conversion();
        conversion.timestamp = Instant.now().toEpochMilli();
        conversion.conversionId = UUID.randomUUID().toString();
        conversion.advertiserId = step.advertiserId();
        conversion.userId = step.userId();
        conversion.orderAmount = Currencies.toMicros(step.orderAmountEur());

        conversionPublisher.publish(conversion);
    }
}
