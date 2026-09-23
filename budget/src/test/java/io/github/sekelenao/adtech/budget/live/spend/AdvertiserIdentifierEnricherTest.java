package io.github.sekelenao.adtech.budget.live.spend;

import io.github.sekelenao.adtech.model.budget.CampaignLiveBudget;
import io.github.sekelenao.adtech.model.budget.CampaignSpendEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AdvertiserIdentifierEnricherTest {

    private final AdvertiserIdentifierEnricher enricher = new AdvertiserIdentifierEnricher();

    @Test
    @DisplayName("Should copy advertiserId from spend event")
    void shouldCopyAdvertiserId() {
        var live = new CampaignLiveBudget();
        var spend = new CampaignSpendEvent();
        spend.advertiserId = "adv-456";

        var result = enricher.enrich(live, spend);

        assertEquals("adv-456", result.advertiserId);
    }
}
