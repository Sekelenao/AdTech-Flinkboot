package io.github.sekelenao.adtech.budget.live.spend;

import io.github.sekelenao.adtech.model.budget.CampaignLiveBudget;
import io.github.sekelenao.adtech.model.budget.CampaignSpendEvent;
import io.github.sekelenao.adtech.model.budget.CampaignStatus;

public final class StatusAfterSpendEnricher implements SpendEnricher {

    @Override
    public CampaignLiveBudget enrich(CampaignLiveBudget live, CampaignSpendEvent spend) {
        if (live.status == CampaignStatus.PAUSED) {
            return live;
        }
        if (live.remainingBudget <= 0) {
            live.status = CampaignStatus.CAPPED;
            return live;
        }
        return live;
    }

}
