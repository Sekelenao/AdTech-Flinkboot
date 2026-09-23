package io.github.sekelenao.adtech.budget.live.spend;

import io.github.sekelenao.adtech.model.budget.CampaignLiveBudget;
import io.github.sekelenao.adtech.model.budget.CampaignSpendEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CampaignIdentifierEnricherTest {

    private final CampaignIdentifierEnricher enricher = new CampaignIdentifierEnricher();

    @Test
    @DisplayName("Should copy campaignId from spend event")
    void shouldCopyCampaignId() {
        var live = new CampaignLiveBudget();
        var spend = new CampaignSpendEvent();
        spend.campaignId = "camp-123";

        var result = enricher.enrich(live, spend);

        assertEquals("camp-123", result.campaignId);
    }
}
