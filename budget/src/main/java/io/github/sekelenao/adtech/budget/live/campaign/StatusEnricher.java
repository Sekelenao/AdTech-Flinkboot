package io.github.sekelenao.adtech.budget.live.campaign;

import io.github.sekelenao.adtech.model.annotation.Nullable;
import io.github.sekelenao.adtech.model.budget.CampaignBudget;
import io.github.sekelenao.adtech.model.budget.CampaignLiveBudget;
import io.github.sekelenao.adtech.model.budget.CampaignStatus;

public final class StatusEnricher implements CampaignBudgetEnricher {


    @Override
    public CampaignLiveBudget enrich(CampaignLiveBudget live, @Nullable CampaignBudget budget) {
        if (budget == null || budget.status == CampaignStatus.PAUSED) {
            live.status = CampaignStatus.PAUSED;
            return live;
        }
        if (live.allocatedBudget > 0 && live.remainingBudget <= 0) {
            live.status = CampaignStatus.CAPPED;
            return live;
        }
        live.status = CampaignStatus.ACTIVE;
        return live;
    }
}
