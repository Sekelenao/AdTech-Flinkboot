package io.github.sekelenao.adtech.budget.live.spend;

import io.github.sekelenao.adtech.model.budget.CampaignLiveBudget;
import io.github.sekelenao.adtech.model.budget.CampaignSpendEvent;

public final class LastUpdateTimeEnricher implements SpendEnricher {

    @Override
    public CampaignLiveBudget enrich(CampaignLiveBudget live, CampaignSpendEvent spend) {
        live.lastUpdateTime = spend.timestamp;
        return live;
    }
}
