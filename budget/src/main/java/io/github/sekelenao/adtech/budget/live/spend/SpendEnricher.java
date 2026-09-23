package io.github.sekelenao.adtech.budget.live.spend;

import io.github.sekelenao.adtech.model.budget.CampaignLiveBudget;
import io.github.sekelenao.adtech.model.budget.CampaignSpendEvent;

@FunctionalInterface
public interface SpendEnricher {

    CampaignLiveBudget enrich(CampaignLiveBudget live, CampaignSpendEvent spend);

}
