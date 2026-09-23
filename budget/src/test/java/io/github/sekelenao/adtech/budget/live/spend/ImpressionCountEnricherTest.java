package io.github.sekelenao.adtech.budget.live.spend;

import io.github.sekelenao.adtech.model.budget.CampaignLiveBudget;
import io.github.sekelenao.adtech.model.budget.CampaignSpendEvent;
import io.github.sekelenao.adtech.model.budget.SpendType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ImpressionCountEnricherTest {

    private final ImpressionCountEnricher enricher = new ImpressionCountEnricher();

    @Test
    @DisplayName("Should increment impressionCount when spend type is IMPRESSION")
    void shouldIncrementWhenImpression() {
        var live = new CampaignLiveBudget();
        live.impressionCount = 5L;

        var spend = new CampaignSpendEvent();
        spend.type = SpendType.IMPRESSION;

        var result = enricher.enrich(live, spend);

        assertEquals(6L, result.impressionCount);
    }

    @Test
    @DisplayName("Should not increment impressionCount when spend type is CLICK")
    void shouldNotIncrementWhenClick() {
        var live = new CampaignLiveBudget();
        live.impressionCount = 5L;

        var spend = new CampaignSpendEvent();
        spend.type = SpendType.CLICK;

        var result = enricher.enrich(live, spend);

        assertEquals(5L, result.impressionCount);
    }
}
