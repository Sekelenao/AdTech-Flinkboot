package io.github.sekelenao.adtech.budget.live;

import io.github.sekelenao.adtech.model.budget.CampaignBudget;
import io.github.sekelenao.adtech.model.budget.CampaignLiveBudget;
import io.github.sekelenao.adtech.model.budget.CampaignSpendEvent;
import io.github.sekelenao.adtech.model.budget.CampaignStatus;
import io.github.sekelenao.adtech.model.budget.SpendType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LiveCampaignBudgetEnrichersTest {

    @Test
    @DisplayName("Should orchestrate campaign enrichment properly")
    void shouldOrchestrateCampaignEnrichment() {
        var live = new CampaignLiveBudget();
        var budget = new CampaignBudget();
        budget.allocatedBudget = 20_000_000L;
        budget.status = CampaignStatus.ACTIVE;

        var result = LiveCampaignBudgetEnrichers.enrich(live, budget);

        assertAll(
            () -> assertEquals(20_000_000L, result.allocatedBudget),
            () -> assertEquals(20_000_000L, result.remainingBudget),
            () -> assertEquals(CampaignStatus.ACTIVE, result.status)
        );
    }

    @Test
    @DisplayName("Should orchestrate spend enrichment through full sequence")
    void shouldOrchestrateSpendEnrichment() {
        var live = new CampaignLiveBudget();
        live.allocatedBudget = 10_000_000L;
        live.status = CampaignStatus.ACTIVE;

        var spend = new CampaignSpendEvent();
        spend.campaignId = "camp-10";
        spend.advertiserId = "adv-20";
        spend.cost = 3_000_000L;
        spend.type = SpendType.IMPRESSION;
        spend.timestamp = 5000L;

        var result = LiveCampaignBudgetEnrichers.enrich(live, spend);

        assertAll(
            () -> assertEquals("camp-10", result.campaignId),
            () -> assertEquals("adv-20", result.advertiserId),
            () -> assertEquals(3_000_000L, result.spentBudget),
            () -> assertEquals(7_000_000L, result.remainingBudget),
            () -> assertEquals(1L, result.impressionCount),
            () -> assertEquals(0L, result.clickCount),
            () -> assertEquals(CampaignStatus.ACTIVE, result.status),
            () -> assertEquals(5000L, result.lastUpdateTime)
        );
    }

    @Test
    @DisplayName("Should enforce null checks on spend enrichment")
    void shouldEnforceNullChecks() {
        var live = new CampaignLiveBudget();
        var spend = new CampaignSpendEvent();

        assertAll(
            () -> assertThrows(NullPointerException.class, () -> LiveCampaignBudgetEnrichers.enrich(null, spend)),
            () -> assertThrows(NullPointerException.class, () -> LiveCampaignBudgetEnrichers.enrich(live, (CampaignSpendEvent) null)),
            () -> assertThrows(NullPointerException.class, () -> LiveCampaignBudgetEnrichers.enrich(null, (CampaignBudget) null))
        );
    }
}
