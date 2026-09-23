package io.github.sekelenao.adtech.budget.live.spend;

import io.github.sekelenao.adtech.model.budget.CampaignLiveBudget;
import io.github.sekelenao.adtech.model.budget.CampaignSpendEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RemainingBudgetEnricherTest {

    private final RemainingBudgetEnricher enricher = new RemainingBudgetEnricher();

    @Test
    @DisplayName("Should correctly calculate positive remaining budget")
    void shouldCalculatePositiveRemainingBudget() {
        var live = new CampaignLiveBudget();
        live.allocatedBudget = 10_000_000L;
        live.spentBudget = 4_000_000L;

        var result = enricher.enrich(live, new CampaignSpendEvent());

        assertEquals(6_000_000L, result.remainingBudget);
    }

    @Test
    @DisplayName("Should correctly calculate negative remaining budget when spend exceeds allocated")
    void shouldCalculateNegativeRemainingBudgetWhenOverspent() {
        var live = new CampaignLiveBudget();
        live.allocatedBudget = 5_000_000L;
        live.spentBudget = 5_200_000L;

        var result = enricher.enrich(live, new CampaignSpendEvent());

        assertEquals(-200_000L, result.remainingBudget);
    }
}
