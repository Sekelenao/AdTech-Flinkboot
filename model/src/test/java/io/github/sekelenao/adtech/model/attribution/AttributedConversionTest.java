package io.github.sekelenao.adtech.model.attribution;

import io.github.sekelenao.adtech.model.event.AdClick;
import io.github.sekelenao.adtech.model.event.Conversion;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.github.sekelenao.flinkboot.test.api.assertion.FlinkbootAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AttributedConversionTest {

    @Test
    @DisplayName("AttributedConversion must comply with Flink POJO serialization rules")
    void shouldComplyWithPojoRules() {
        assertThat(AttributedConversion.class).isPojo();
    }

    @Test
    @DisplayName("Should create AttributedConversion from AdClick and Conversion accurately")
    void shouldCreateFromClickAndConversion() {
        var click = new AdClick();
        click.campaignId = "camp-1";
        click.advertiserId = "adv-1";
        click.userId = "user-1";
        click.clickId = "clk-1";
        click.timestamp = 1000L;

        var conversion = new Conversion();
        conversion.advertiserId = "adv-1";
        conversion.userId = "user-1";
        conversion.conversionId = "conv-1";
        conversion.orderAmount = 120_000_000L;
        conversion.timestamp = 2000L;

        var result = AttributedConversion.from(click, conversion);

        assertAll(
            () -> assertEquals("camp-1", result.campaignId),
            () -> assertEquals("adv-1", result.advertiserId),
            () -> assertEquals("user-1", result.userId),
            () -> assertEquals("clk-1", result.clickId),
            () -> assertEquals("conv-1", result.conversionId),
            () -> assertEquals(120_000_000L, result.orderAmount),
            () -> assertEquals(1000L, result.clickTimestamp),
            () -> assertEquals(2000L, result.conversionTimestamp)
        );
    }

    @Test
    @DisplayName("Should enforce null checks on from method")
    void shouldEnforceNullChecks() {
        var click = new AdClick();
        var conversion = new Conversion();

        assertAll(
            () -> assertThrows(NullPointerException.class, () -> AttributedConversion.from(null, conversion)),
            () -> assertThrows(NullPointerException.class, () -> AttributedConversion.from(click, null))
        );
    }
}
