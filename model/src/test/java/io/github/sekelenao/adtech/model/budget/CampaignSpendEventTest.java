package io.github.sekelenao.adtech.model.budget;

import io.github.sekelenao.adtech.model.event.AdClick;
import io.github.sekelenao.adtech.model.event.AdImpression;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.github.sekelenao.flinkboot.test.api.assertion.FlinkbootAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CampaignSpendEventTest {

    @Test
    @DisplayName("CampaignSpendEvent must comply with Flink POJO serialization rules")
    void shouldComplyWithPojoRules() {
        assertThat(CampaignSpendEvent.class).isPojo();
    }

    @Test
    @DisplayName("Should convert AdImpression to CampaignSpendEvent accurately")
    void shouldConvertFromAdImpression() {
        var impression = new AdImpression();
        impression.timestamp = 1000L;
        impression.impressionId = "imp-1";
        impression.campaignId = "camp-1";
        impression.advertiserId = "adv-1";
        impression.cost = 2000L;

        var spend = CampaignSpendEvent.from(impression);

        assertAll(
            () -> assertEquals(1000L, spend.timestamp),
            () -> assertEquals("imp-1", spend.eventId),
            () -> assertEquals("camp-1", spend.campaignId),
            () -> assertEquals("adv-1", spend.advertiserId),
            () -> assertEquals(2000L, spend.cost),
            () -> assertEquals(SpendType.IMPRESSION, spend.type)
        );
    }

    @Test
    @DisplayName("Should convert AdClick to CampaignSpendEvent accurately")
    void shouldConvertFromAdClick() {
        var click = new AdClick();
        click.timestamp = 2000L;
        click.clickId = "clk-1";
        click.campaignId = "camp-1";
        click.advertiserId = "adv-1";
        click.cost = 5000L;

        var spend = CampaignSpendEvent.from(click);

        assertAll(
            () -> assertEquals(2000L, spend.timestamp),
            () -> assertEquals("clk-1", spend.eventId),
            () -> assertEquals("camp-1", spend.campaignId),
            () -> assertEquals("adv-1", spend.advertiserId),
            () -> assertEquals(5000L, spend.cost),
            () -> assertEquals(SpendType.CLICK, spend.type)
        );
    }

    @Test
    @DisplayName("Should throw NullPointerException when input is null")
    void shouldThrowWhenNull() {
        assertAll(
            () -> assertThrows(NullPointerException.class, () -> CampaignSpendEvent.from((AdImpression) null)),
            () -> assertThrows(NullPointerException.class, () -> CampaignSpendEvent.from((AdClick) null))
        );
    }
}
