package io.github.sekelenao.adtech.budget.live.spend;

import io.github.sekelenao.adtech.model.budget.CampaignLiveBudget;
import io.github.sekelenao.adtech.model.budget.CampaignSpendEvent;
import io.github.sekelenao.adtech.model.budget.SpendType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ClickCountEnricherTest {

    private final ClickCountEnricher enricher = new ClickCountEnricher();

    @Test
    @DisplayName("Should increment clickCount when spend type is CLICK")
    void shouldIncrementWhenClick() {
        var live = new CampaignLiveBudget();
        live.clickCount = 2L;

        var spend = new CampaignSpendEvent();
        spend.type = SpendType.CLICK;

        var result = enricher.enrich(live, spend);

        assertEquals(3L, result.clickCount);
    }

    @Test
    @DisplayName("Should not increment clickCount when spend type is IMPRESSION")
    void shouldNotIncrementWhenImpression() {
        var live = new CampaignLiveBudget();
        live.clickCount = 2L;

        var spend = new CampaignSpendEvent();
        spend.type = SpendType.IMPRESSION;

        var result = enricher.enrich(live, spend);

        assertEquals(2L, result.clickCount);
    }
}
