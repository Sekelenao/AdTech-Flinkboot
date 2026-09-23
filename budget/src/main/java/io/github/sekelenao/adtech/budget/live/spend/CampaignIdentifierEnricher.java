package io.github.sekelenao.adtech.budget.live.spend;

import io.github.sekelenao.adtech.model.budget.CampaignLiveBudget;
import io.github.sekelenao.adtech.model.budget.CampaignSpendEvent;

public final class CampaignIdentifierEnricher implements SpendEnricher {

    @Override
    public CampaignLiveBudget enrich(CampaignLiveBudget live, CampaignSpendEvent spend) {
        live.campaignId = spend.campaignId;
        return live;
    }

}
