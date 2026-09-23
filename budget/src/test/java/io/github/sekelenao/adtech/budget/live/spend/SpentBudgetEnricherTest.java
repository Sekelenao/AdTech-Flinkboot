package io.github.sekelenao.adtech.budget.live.spend;

import io.github.sekelenao.adtech.model.budget.CampaignLiveBudget;
import io.github.sekelenao.adtech.model.budget.CampaignSpendEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SpentBudgetEnricherTest {

    private final SpentBudgetEnricher enricher = new SpentBudgetEnricher();

    @Test
    @DisplayName("Should add spend cost to cumulative spentBudget")
    void shouldAccumulateSpentBudget() {
        var live = new CampaignLiveBudget();
        live.spentBudget = 1000L;

        var spend = new CampaignSpendEvent();
        spend.cost = 500L;

        var result = enricher.enrich(live, spend);

        assertEquals(1500L, result.spentBudget);
    }
}
