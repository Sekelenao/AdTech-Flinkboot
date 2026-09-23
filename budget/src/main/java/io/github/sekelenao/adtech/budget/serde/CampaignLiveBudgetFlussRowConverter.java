package io.github.sekelenao.adtech.budget.serde;

import io.github.sekelenao.adtech.model.budget.CampaignLiveBudget;
import org.apache.flink.table.data.GenericRowData;
import org.apache.flink.table.data.RowData;
import org.apache.flink.table.data.StringData;

/**
 * Utility to convert CampaignLiveBudget POJO to Flink RowData for Apache Fluss sink.
 */
public final class CampaignLiveBudgetFlussRowConverter {

    private CampaignLiveBudgetFlussRowConverter() {
        throw new UnsupportedOperationException();
    }

    public static RowData toRowData(CampaignLiveBudget budget) {
        GenericRowData row = new GenericRowData(11);
        row.setField(0, StringData.fromString(budget.campaignId));
        row.setField(1, StringData.fromString(budget.advertiserId));
        row.setField(2, StringData.fromString(budget.status.toString()));
        row.setField(3, budget.allocatedBudget);
        row.setField(4, budget.spentBudget);
        row.setField(5, budget.remainingBudget);
        row.setField(6, budget.impressionCount);
        row.setField(7, budget.clickCount);
        row.setField(8, budget.attributedConversionCount);
        row.setField(9, budget.attributedRevenue);
        row.setField(10, budget.lastUpdateTime);
        return row;
    }
}
