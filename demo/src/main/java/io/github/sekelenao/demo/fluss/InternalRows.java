package io.github.sekelenao.demo.fluss;

import io.github.sekelenao.adtech.model.budget.CampaignLiveBudget;
import io.github.sekelenao.adtech.model.budget.CampaignStatus;
import org.apache.fluss.row.InternalRow;

import java.util.Locale;

public final class InternalRows {

    private InternalRows(){
        throw new AssertionError("You cannot instantiate this class");
    }

    public static CampaignLiveBudget mapToCampaignLiveBudget(InternalRow row) {
        CampaignLiveBudget budget = new CampaignLiveBudget();
        budget.campaignId = row.getString(0).toString();
        budget.advertiserId = row.getString(1).toString();
        budget.status = CampaignStatus.valueOf(row.getString(2).toString().toUpperCase(Locale.ROOT));
        budget.allocatedBudget = row.getLong(3);
        budget.spentBudget = row.getLong(4);
        budget.remainingBudget = row.getLong(5);
        budget.impressionCount = row.getLong(6);
        budget.clickCount = row.getLong(7);
        budget.attributedConversionCount = row.getLong(8);
        budget.attributedRevenue = row.getLong(9);
        budget.lastUpdateTime = row.getLong(10);
        return budget;
    }

}
