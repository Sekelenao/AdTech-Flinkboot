package io.github.sekelenao.adtech.budget.live.spend;

import io.github.sekelenao.adtech.model.budget.CampaignLiveBudget;
import io.github.sekelenao.adtech.model.budget.CampaignSpendEvent;
import io.github.sekelenao.adtech.model.budget.CampaignStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StatusAfterSpendEnricherTest {

    private final StatusAfterSpendEnricher enricher = new StatusAfterSpendEnricher();

    @Test
    @DisplayName("Should switch status to CAPPED when remainingBudget is exactly 0")
    void shouldCapWhenRemainingBudgetIsZero() {
        var live = new CampaignLiveBudget();
        live.status = CampaignStatus.ACTIVE;
        live.remainingBudget = 0L;

        var result = enricher.enrich(live, new CampaignSpendEvent());

        assertEquals(CampaignStatus.CAPPED, result.status);
    }

    @Test
    @DisplayName("Should switch status to CAPPED when remainingBudget is negative")
    void shouldCapWhenRemainingBudgetIsNegative() {
        var live = new CampaignLiveBudget();
        live.status = CampaignStatus.ACTIVE;
        live.remainingBudget = -100L;

        var result = enricher.enrich(live, new CampaignSpendEvent());

        assertEquals(CampaignStatus.CAPPED, result.status);
    }

    @Test
    @DisplayName("Should retain status when remainingBudget is strictly positive")
    void shouldRetainStatusWhenRemainingBudgetIsPositive() {
        var live = new CampaignLiveBudget();
        live.status = CampaignStatus.ACTIVE;
        live.remainingBudget = 500_000L;

        var result = enricher.enrich(live, new CampaignSpendEvent());

        assertEquals(CampaignStatus.ACTIVE, result.status);
    }

    @Test
    @DisplayName("Should retain PAUSED status even when remainingBudget is zero or negative")
    void shouldRetainPausedStatus() {
        var live = new CampaignLiveBudget();
        live.status = CampaignStatus.PAUSED;
        live.remainingBudget = -100L;

        var result = enricher.enrich(live, new CampaignSpendEvent());

        assertEquals(CampaignStatus.PAUSED, result.status);
    }
}
