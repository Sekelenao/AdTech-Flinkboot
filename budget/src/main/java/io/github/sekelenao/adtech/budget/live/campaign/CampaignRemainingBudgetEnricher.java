package io.github.sekelenao.adtech.budget.live.campaign;

import io.github.sekelenao.adtech.model.annotation.Nullable;
import io.github.sekelenao.adtech.model.budget.CampaignBudget;
import io.github.sekelenao.adtech.model.budget.CampaignLiveBudget;

/**
 * Recalculates remaining budget whenever the campaign budget envelope changes.
 */
public final class CampaignRemainingBudgetEnricher implements CampaignBudgetEnricher {

    @Override
    public CampaignLiveBudget enrich(CampaignLiveBudget live, @Nullable CampaignBudget budget) {
        live.remainingBudget = live.allocatedBudget - live.spentBudget;
        return live;
    }
}
