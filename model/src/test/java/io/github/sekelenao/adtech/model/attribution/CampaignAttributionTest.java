package io.github.sekelenao.adtech.model.attribution;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.github.sekelenao.flinkboot.test.api.assertion.FlinkbootAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CampaignAttributionTest {

    @Test
    @DisplayName("CampaignAttribution must comply with Flink POJO serialization rules")
    void shouldComplyWithPojoRules() {
        assertThat(CampaignAttribution.class).isPojo();
    }

    @Test
    @DisplayName("Should assign and read fields correctly")
    void shouldAssignFields() {
        var attribution = new CampaignAttribution();
        attribution.campaignId = "cmp-google";
        attribution.attributedConversionCount = 5L;
        attribution.attributedRevenue = 500_000_000L;
        attribution.lastUpdateTime = 123456789L;

        assertAll(
            () -> assertEquals("cmp-google", attribution.campaignId),
            () -> assertEquals(5L, attribution.attributedConversionCount),
            () -> assertEquals(500_000_000L, attribution.attributedRevenue),
            () -> assertEquals(123456789L, attribution.lastUpdateTime)
        );
    }
}
