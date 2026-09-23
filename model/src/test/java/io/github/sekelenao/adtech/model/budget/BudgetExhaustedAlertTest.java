package io.github.sekelenao.adtech.model.budget;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.github.sekelenao.flinkboot.test.api.assertion.FlinkbootAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BudgetExhaustedAlertTest {

    @Test
    @DisplayName("BudgetExhaustedAlert must comply with Flink POJO serialization rules")
    void shouldComplyWithPojoRules() {
        assertThat(BudgetExhaustedAlert.class).isPojo();
    }

    @Test
    @DisplayName("Should create BudgetExhaustedAlert from CampaignLiveBudget accurately")
    void shouldCreateFromCampaignLiveBudget() {
        var live = new CampaignLiveBudget();
        live.campaignId = "camp-1";
        live.advertiserId = "adv-1";
        live.allocatedBudget = 5_000_000L;
        live.spentBudget = 5_200_000L;
        live.lastUpdateTime = 9999L;

        var alert = BudgetExhaustedAlert.from(live);

        assertAll(
            () -> assertEquals("camp-1", alert.campaignId),
            () -> assertEquals("adv-1", alert.advertiserId),
            () -> assertEquals(5_000_000L, alert.allocatedBudget),
            () -> assertEquals(5_200_000L, alert.spentBudget),
            () -> assertEquals(9999L, alert.exhaustedAt)
        );
    }

    @Test
    @DisplayName("Should throw NullPointerException when CampaignLiveBudget is null")
    void shouldThrowWhenNull() {
        assertThrows(NullPointerException.class, () -> BudgetExhaustedAlert.from(null));
    }
}
