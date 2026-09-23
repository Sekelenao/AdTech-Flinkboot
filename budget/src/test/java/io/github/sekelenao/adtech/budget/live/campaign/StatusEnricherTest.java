package io.github.sekelenao.adtech.budget.live.campaign;

import io.github.sekelenao.adtech.model.budget.CampaignBudget;
import io.github.sekelenao.adtech.model.budget.CampaignLiveBudget;
import io.github.sekelenao.adtech.model.budget.CampaignStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StatusEnricherTest {

    private final StatusEnricher enricher = new StatusEnricher();

    @Test
    @DisplayName("Should default status to PAUSED when CampaignBudget is null")
    void shouldDefaultToPausedWhenBudgetIsNull() {
        var live = new CampaignLiveBudget();

        var result = enricher.enrich(live, null);

        assertEquals(CampaignStatus.PAUSED, result.status);
    }

    @Test
    @DisplayName("Should set status to PAUSED when CampaignBudget status is PAUSED")
    void shouldSetPausedStatusWhenBudgetIsPaused() {
        var live = new CampaignLiveBudget();
        var budget = new CampaignBudget();
        budget.status = CampaignStatus.PAUSED;

        var result = enricher.enrich(live, budget);

        assertEquals(CampaignStatus.PAUSED, result.status);
    }

    @Test
    @DisplayName("Should set status to CAPPED when remaining budget is zero or negative and allocated budget is positive")
    void shouldSetCappedWhenBudgetExhausted() {
        var live = new CampaignLiveBudget();
        live.allocatedBudget = 10_000_000L;
        live.remainingBudget = 0L;

        var budget = new CampaignBudget();
        budget.status = CampaignStatus.ACTIVE;
        budget.allocatedBudget = 10_000_000L;

        var result = enricher.enrich(live, budget);

        assertEquals(CampaignStatus.CAPPED, result.status);
    }

    @Test
    @DisplayName("Should set status to ACTIVE when budget is active and remaining budget is strictly positive")
    void shouldSetActiveWhenRemainingBudgetIsPositive() {
        var live = new CampaignLiveBudget();
        live.allocatedBudget = 10_000_000L;
        live.remainingBudget = 5_000_000L;

        var budget = new CampaignBudget();
        budget.status = CampaignStatus.ACTIVE;
        budget.allocatedBudget = 10_000_000L;

        var result = enricher.enrich(live, budget);

        assertEquals(CampaignStatus.ACTIVE, result.status);
    }
}
