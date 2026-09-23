package io.github.sekelenao.adtech.budget.live.campaign;

import io.github.sekelenao.adtech.model.annotation.Nullable;
import io.github.sekelenao.adtech.model.budget.CampaignBudget;
import io.github.sekelenao.adtech.model.budget.CampaignLiveBudget;

@FunctionalInterface
public interface CampaignBudgetEnricher {

    CampaignLiveBudget enrich(CampaignLiveBudget live, @Nullable CampaignBudget budget);

}
