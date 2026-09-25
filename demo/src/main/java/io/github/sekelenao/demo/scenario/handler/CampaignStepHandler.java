package io.github.sekelenao.demo.scenario.handler;

import io.github.sekelenao.adtech.model.budget.CampaignBudget;
import io.github.sekelenao.adtech.model.budget.CampaignStatus;
import io.github.sekelenao.adtech.model.util.Currencies;
import io.github.sekelenao.demo.kafka.BudgetPublisher;
import io.github.sekelenao.demo.model.Action;
import io.github.sekelenao.demo.model.ScenarioStep;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

@Component
public class CampaignStepHandler implements StepHandler {

    private final BudgetPublisher budgetPublisher;

    public CampaignStepHandler(BudgetPublisher budgetPublisher) {
        this.budgetPublisher = Objects.requireNonNull(budgetPublisher);
    }

    @Override
    public Action action() {
        return Action.CAMPAIGN;
    }

    @Override
    public void handle(ScenarioStep step) {
        Objects.requireNonNull(step);

        CampaignBudget budget = new CampaignBudget();
        budget.campaignId = step.campaignId();
        budget.advertiserId = step.advertiserId();
        budget.allocatedBudget = Currencies.toMicros(step.allocatedBudgetEur());
        budget.status = Optional.ofNullable(step.status()).orElse(CampaignStatus.ACTIVE);
        budget.updatedAt = Instant.now().toEpochMilli();

        budgetPublisher.publish(budget);
    }
}
