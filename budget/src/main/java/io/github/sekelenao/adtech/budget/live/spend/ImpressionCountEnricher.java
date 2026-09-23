package io.github.sekelenao.adtech.budget.live.spend;

import io.github.sekelenao.adtech.model.budget.CampaignLiveBudget;
import io.github.sekelenao.adtech.model.budget.CampaignSpendEvent;
import io.github.sekelenao.adtech.model.budget.SpendType;

public final class ImpressionCountEnricher implements SpendEnricher {

    @Override
    public CampaignLiveBudget enrich(CampaignLiveBudget live, CampaignSpendEvent spend) {
        if (spend.type == SpendType.IMPRESSION) {
            live.impressionCount++;
        }
        return live;
    }
}
