package io.github.sekelenao.adtech.budget.serde;

import io.github.sekelenao.adtech.model.budget.BudgetExhaustedAlert;
import io.github.sekelenao.adtech.model.budget.CampaignSpendEvent;
import io.github.sekelenao.adtech.model.budget.SpendType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class JsonSerdeTest {

    @Test
    @DisplayName("Should serialize and deserialize CampaignSpendEvent round-trip")
    void shouldRoundTripCampaignSpendEvent() throws Exception {
        var serializer = new JsonSerializer<CampaignSpendEvent>();
        serializer.open(null);

        var deserializer = new JsonDeserializer<>(CampaignSpendEvent.class);
        deserializer.open(null);

        var event = new CampaignSpendEvent();
        event.timestamp = 1000L;
        event.eventId = "ev-1";
        event.campaignId = "camp-1";
        event.advertiserId = "adv-1";
        event.cost = 500L;
        event.type = SpendType.IMPRESSION;

        var bytes = serializer.serialize(event);
        var result = deserializer.deserialize(bytes);

        assertAll(
            () -> assertEquals(event.timestamp, result.timestamp),
            () -> assertEquals(event.eventId, result.eventId),
            () -> assertEquals(event.campaignId, result.campaignId),
            () -> assertEquals(event.advertiserId, result.advertiserId),
            () -> assertEquals(event.cost, result.cost),
            () -> assertEquals(event.type, result.type),
            () -> assertFalse(deserializer.isEndOfStream(result))
        );
    }

    @Test
    @DisplayName("Should serialize and deserialize BudgetExhaustedAlert round-trip")
    void shouldRoundTripBudgetExhaustedAlert() throws Exception {
        var serializer = new JsonSerializer<BudgetExhaustedAlert>();
        serializer.open(null);

        var deserializer = new JsonDeserializer<>(BudgetExhaustedAlert.class);
        deserializer.open(null);

        var alert = new BudgetExhaustedAlert();
        alert.campaignId = "camp-99";
        alert.advertiserId = "adv-99";
        alert.allocatedBudget = 10_000_000L;
        alert.spentBudget = 10_200_000L;
        alert.exhaustedAt = 5000L;

        var bytes = serializer.serialize(alert);
        var result = deserializer.deserialize(bytes);

        assertAll(
            () -> assertEquals(alert.campaignId, result.campaignId),
            () -> assertEquals(alert.advertiserId, result.advertiserId),
            () -> assertEquals(alert.allocatedBudget, result.allocatedBudget),
            () -> assertEquals(alert.spentBudget, result.spentBudget),
            () -> assertEquals(alert.exhaustedAt, result.exhaustedAt)
        );
    }
}
