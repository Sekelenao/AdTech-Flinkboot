package io.github.sekelenao.adtech.attribution.serde;

import io.github.sekelenao.adtech.model.attribution.AttributedConversion;
import io.github.sekelenao.adtech.model.event.AdClick;
import io.github.sekelenao.adtech.model.event.Conversion;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JsonSerdeTest {

    @Test
    @DisplayName("Should reject null targetType in JsonDeserializer constructor")
    void shouldRejectNullTargetType() {
        assertThrows(NullPointerException.class, () -> new JsonDeserializer<>(null));
    }

    @Test
    @DisplayName("Should serialize and deserialize AdClick round-trip")
    void shouldRoundTripAdClick() throws Exception {
        var serializer = new JsonSerializer<AdClick>();
        serializer.open(null);

        var deserializer = new JsonDeserializer<>(AdClick.class);
        deserializer.open(null);

        var click = new AdClick();
        click.timestamp = 1000L;
        click.clickId = "clk-1";
        click.impressionId = "imp-1";
        click.campaignId = "camp-1";
        click.advertiserId = "adv-1";
        click.userId = "user-1";
        click.cost = 500_000L;

        var bytes = serializer.serialize(click);
        var result = deserializer.deserialize(bytes);

        assertAll(
            () -> assertEquals(click.timestamp, result.timestamp),
            () -> assertEquals(click.clickId, result.clickId),
            () -> assertEquals(click.impressionId, result.impressionId),
            () -> assertEquals(click.campaignId, result.campaignId),
            () -> assertEquals(click.advertiserId, result.advertiserId),
            () -> assertEquals(click.userId, result.userId),
            () -> assertEquals(click.cost, result.cost),
            () -> assertFalse(deserializer.isEndOfStream(result))
        );
    }

    @Test
    @DisplayName("Should serialize and deserialize Conversion round-trip")
    void shouldRoundTripConversion() throws Exception {
        var serializer = new JsonSerializer<Conversion>();
        serializer.open(null);

        var deserializer = new JsonDeserializer<>(Conversion.class);
        deserializer.open(null);

        var conversion = new Conversion();
        conversion.timestamp = 2000L;
        conversion.conversionId = "conv-1";
        conversion.advertiserId = "adv-1";
        conversion.userId = "user-1";
        conversion.orderAmount = 120_000_000L;

        var bytes = serializer.serialize(conversion);
        var result = deserializer.deserialize(bytes);

        assertAll(
            () -> assertEquals(conversion.timestamp, result.timestamp),
            () -> assertEquals(conversion.conversionId, result.conversionId),
            () -> assertEquals(conversion.advertiserId, result.advertiserId),
            () -> assertEquals(conversion.userId, result.userId),
            () -> assertEquals(conversion.orderAmount, result.orderAmount)
        );
    }

    @Test
    @DisplayName("Should serialize and deserialize AttributedConversion round-trip")
    void shouldRoundTripAttributedConversion() throws Exception {
        var serializer = new JsonSerializer<AttributedConversion>();
        serializer.open(null);

        var deserializer = new JsonDeserializer<>(AttributedConversion.class);
        deserializer.open(null);

        var attributed = new AttributedConversion();
        attributed.campaignId = "camp-1";
        attributed.advertiserId = "adv-1";
        attributed.userId = "user-1";
        attributed.clickId = "clk-1";
        attributed.conversionId = "conv-1";
        attributed.orderAmount = 120_000_000L;
        attributed.clickTimestamp = 1000L;
        attributed.conversionTimestamp = 2000L;

        var bytes = serializer.serialize(attributed);
        var result = deserializer.deserialize(bytes);

        assertAll(
            () -> assertEquals(attributed.campaignId, result.campaignId),
            () -> assertEquals(attributed.advertiserId, result.advertiserId),
            () -> assertEquals(attributed.userId, result.userId),
            () -> assertEquals(attributed.clickId, result.clickId),
            () -> assertEquals(attributed.conversionId, result.conversionId),
            () -> assertEquals(attributed.orderAmount, result.orderAmount),
            () -> assertEquals(attributed.clickTimestamp, result.clickTimestamp),
            () -> assertEquals(attributed.conversionTimestamp, result.conversionTimestamp)
        );
    }
}
