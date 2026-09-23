package io.github.sekelenao.adtech.budget.live.campaign;

import io.github.sekelenao.adtech.model.budget.CampaignBudget;
import io.github.sekelenao.adtech.model.budget.CampaignLiveBudget;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CampaignRemainingBudgetEnricherTest {

    private final CampaignRemainingBudgetEnricher enricher = new CampaignRemainingBudgetEnricher();

    @Test
    @DisplayName("Should recompute remainingBudget as allocatedBudget minus spentBudget")
    void shouldRecomputeRemainingBudget() {
        var live = new CampaignLiveBudget();
        live.allocatedBudget = 20_000_000L;
        live.spentBudget = 5_000_000L;

        var result = enricher.enrich(live, new CampaignBudget());

        assertEquals(15_000_000L, result.remainingBudget);
    }

    @Test
    @DisplayName("Should handle null budget safely when recomputing remainingBudget")
    void shouldHandleNullBudgetSafely() {
        var live = new CampaignLiveBudget();
        live.allocatedBudget = 10_000_000L;
        live.spentBudget = 2_000_000L;

        var result = enricher.enrich(live, null);

        assertEquals(8_000_000L, result.remainingBudget);
    }
}
