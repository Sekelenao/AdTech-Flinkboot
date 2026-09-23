package io.github.sekelenao.adtech.budget.live.campaign;

import io.github.sekelenao.adtech.model.budget.CampaignBudget;
import io.github.sekelenao.adtech.model.budget.CampaignLiveBudget;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AllocatedBudgetEnricherTest {

    private final AllocatedBudgetEnricher enricher = new AllocatedBudgetEnricher();

    @Test
    @DisplayName("Should leave allocatedBudget unchanged when CampaignBudget is null")
    void shouldLeaveUnchangedWhenBudgetIsNull() {
        var live = new CampaignLiveBudget();
        live.allocatedBudget = 5000L;

        var result = enricher.enrich(live, null);

        assertEquals(5000L, result.allocatedBudget);
    }

    @Test
    @DisplayName("Should copy allocatedBudget from CampaignBudget")
    void shouldCopyAllocatedBudgetWhenBudgetIsNotNull() {
        var live = new CampaignLiveBudget();
        var budget = new CampaignBudget();
        budget.allocatedBudget = 10_000_000L;

        var result = enricher.enrich(live, budget);

        assertEquals(10_000_000L, result.allocatedBudget);
    }
}
