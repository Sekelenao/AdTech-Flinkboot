package io.github.sekelenao.adtech.budget.serde;

import io.github.sekelenao.adtech.model.budget.CampaignLiveBudget;
import io.github.sekelenao.adtech.model.budget.CampaignStatus;
import org.apache.flink.table.data.StringData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CampaignLiveBudgetFlussRowConverterTest {

    @Test
    @DisplayName("Should convert all 11 fields of CampaignLiveBudget to RowData accurately")
    void shouldConvertCampaignLiveBudgetToRowData() {
        var budget = new CampaignLiveBudget();
        budget.campaignId = "camp-1";
        budget.advertiserId = "adv-1";
        budget.status = CampaignStatus.ACTIVE;
        budget.allocatedBudget = 10_000_000L;
        budget.spentBudget = 4_000_000L;
        budget.remainingBudget = 6_000_000L;
        budget.impressionCount = 100L;
        budget.clickCount = 5L;
        budget.attributedConversionCount = 2L;
        budget.attributedRevenue = 50_000_000L;
        budget.lastUpdateTime = 123456789L;

        var row = CampaignLiveBudgetFlussRowConverter.toRowData(budget);

        assertAll(
            () -> assertEquals(11, row.getArity()),
            () -> assertEquals(StringData.fromString("camp-1"), row.getString(0)),
            () -> assertEquals(StringData.fromString("adv-1"), row.getString(1)),
            () -> assertEquals(StringData.fromString("ACTIVE"), row.getString(2)),
            () -> assertEquals(10_000_000L, row.getLong(3)),
            () -> assertEquals(4_000_000L, row.getLong(4)),
            () -> assertEquals(6_000_000L, row.getLong(5)),
            () -> assertEquals(100L, row.getLong(6)),
            () -> assertEquals(5L, row.getLong(7)),
            () -> assertEquals(2L, row.getLong(8)),
            () -> assertEquals(50_000_000L, row.getLong(9)),
            () -> assertEquals(123456789L, row.getLong(10))
        );
    }
}
