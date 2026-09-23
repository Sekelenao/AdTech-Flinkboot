package io.github.sekelenao.adtech.budget.live;

import io.github.sekelenao.adtech.budget.live.campaign.AllocatedBudgetEnricher;
import io.github.sekelenao.adtech.budget.live.campaign.CampaignBudgetEnricher;
import io.github.sekelenao.adtech.budget.live.campaign.CampaignRemainingBudgetEnricher;
import io.github.sekelenao.adtech.budget.live.campaign.StatusEnricher;
import io.github.sekelenao.adtech.budget.live.spend.AdvertiserIdentifierEnricher;
import io.github.sekelenao.adtech.budget.live.spend.CampaignIdentifierEnricher;
import io.github.sekelenao.adtech.budget.live.spend.ClickCountEnricher;
import io.github.sekelenao.adtech.budget.live.spend.ImpressionCountEnricher;
import io.github.sekelenao.adtech.budget.live.spend.LastUpdateTimeEnricher;
import io.github.sekelenao.adtech.budget.live.spend.RemainingBudgetEnricher;
import io.github.sekelenao.adtech.budget.live.spend.SpendEnricher;
import io.github.sekelenao.adtech.budget.live.spend.SpentBudgetEnricher;
import io.github.sekelenao.adtech.budget.live.spend.StatusAfterSpendEnricher;
import io.github.sekelenao.adtech.model.annotation.Nullable;
import io.github.sekelenao.adtech.model.budget.CampaignBudget;
import io.github.sekelenao.adtech.model.budget.CampaignLiveBudget;
import io.github.sekelenao.adtech.model.budget.CampaignSpendEvent;

import java.util.List;
import java.util.Objects;

public final class LiveCampaignBudgetEnrichers {

    private static final List<SpendEnricher> SPEND_ENRICHERS = List.of(
        new CampaignIdentifierEnricher(),
        new AdvertiserIdentifierEnricher(),
        new SpentBudgetEnricher(),
        new ImpressionCountEnricher(),
        new ClickCountEnricher(),
        new RemainingBudgetEnricher(),
        new LastUpdateTimeEnricher(),
        new StatusAfterSpendEnricher()
    );

    private static final List<CampaignBudgetEnricher> CAMPAIGN_ENRICHERS = List.of(
        new AllocatedBudgetEnricher(),
        new CampaignRemainingBudgetEnricher(),
        new StatusEnricher()
    );

    private LiveCampaignBudgetEnrichers() {
        throw new UnsupportedOperationException();
    }

    public static CampaignLiveBudget enrich(CampaignLiveBudget live, CampaignSpendEvent spend) {
        Objects.requireNonNull(live);
        Objects.requireNonNull(spend);
        for (var i = 0; i < SPEND_ENRICHERS.size(); i++) {
            live = SPEND_ENRICHERS.get(i).enrich(live, spend);
        }
        return live;
    }

    public static CampaignLiveBudget enrich(CampaignLiveBudget live, @Nullable CampaignBudget budget) {
        Objects.requireNonNull(live);
        for (var i = 0; i < CAMPAIGN_ENRICHERS.size(); i++) {
            live = CAMPAIGN_ENRICHERS.get(i).enrich(live, budget);
        }
        return live;
    }

}
