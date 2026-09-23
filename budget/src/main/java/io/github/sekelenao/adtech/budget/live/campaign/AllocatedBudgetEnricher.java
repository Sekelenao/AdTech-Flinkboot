package io.github.sekelenao.adtech.budget.live.campaign;

import io.github.sekelenao.adtech.model.annotation.Nullable;
import io.github.sekelenao.adtech.model.budget.CampaignBudget;
import io.github.sekelenao.adtech.model.budget.CampaignLiveBudget;

public final class AllocatedBudgetEnricher implements CampaignBudgetEnricher {

    @Override
    public CampaignLiveBudget enrich(CampaignLiveBudget live, @Nullable CampaignBudget budget) {
        if (budget == null) {
            return live;
        }
        live.allocatedBudget = budget.allocatedBudget;
        return live;
    }

}
