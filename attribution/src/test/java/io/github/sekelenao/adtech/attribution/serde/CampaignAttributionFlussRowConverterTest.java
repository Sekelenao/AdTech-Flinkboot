package io.github.sekelenao.adtech.attribution.serde;

import io.github.sekelenao.adtech.model.attribution.CampaignAttribution;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CampaignAttributionFlussRowConverterTest {

    @Test
    @DisplayName("Should convert CampaignAttribution to RowData matching partial update columns")
    void shouldConvertToRowData() {
        var attribution = new CampaignAttribution();
        attribution.campaignId = "cmp-google";
        attribution.attributedConversionCount = 3L;
        attribution.attributedRevenue = 150_000_000L;
        attribution.lastUpdateTime = 123456789L;

        var row = CampaignAttributionFlussRowConverter.toRowData(attribution);

        assertAll(
            () -> assertEquals(11, row.getArity()),
            () -> assertEquals("cmp-google", row.getString(0).toString()),
            () -> assertEquals(3L, row.getLong(8)),
            () -> assertEquals(150_000_000L, row.getLong(9)),
            () -> assertEquals(123456789L, row.getLong(10))
        );
    }

    @Test
    @DisplayName("Should throw NullPointerException when converting null")
    void shouldThrowWhenNull() {
        assertThrows(NullPointerException.class, () -> CampaignAttributionFlussRowConverter.toRowData(null));
    }
}
