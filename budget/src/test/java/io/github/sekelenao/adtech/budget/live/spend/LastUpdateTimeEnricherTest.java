package io.github.sekelenao.adtech.budget.live.spend;

import io.github.sekelenao.adtech.model.budget.CampaignLiveBudget;
import io.github.sekelenao.adtech.model.budget.CampaignSpendEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LastUpdateTimeEnricherTest {

    private final LastUpdateTimeEnricher enricher = new LastUpdateTimeEnricher();

    @Test
    @DisplayName("Should copy spend event timestamp to lastUpdateTime")
    void shouldUpdateTimestamp() {
        var live = new CampaignLiveBudget();
        var spend = new CampaignSpendEvent();
        spend.timestamp = 1700000000000L;

        var result = enricher.enrich(live, spend);

        assertEquals(1700000000000L, result.lastUpdateTime);
    }
}
