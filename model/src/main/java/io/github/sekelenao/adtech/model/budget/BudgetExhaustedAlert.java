package io.github.sekelenao.adtech.model.budget;

import java.io.Serializable;
import java.util.Objects;

/**
 * High-priority alert emitted when a campaign reaches or exceeds its allocated budget.
 */
public class BudgetExhaustedAlert implements Serializable {

    private static final long serialVersionUID = 1L;

    public String campaignId;
    public String advertiserId;
    public long allocatedBudget;
    public long spentBudget;
    public long exhaustedAt;
    public String reason;

    public static BudgetExhaustedAlert from(CampaignLiveBudget live) {
        Objects.requireNonNull(live);
        var alert = new BudgetExhaustedAlert();
        alert.campaignId = live.campaignId;
        alert.advertiserId = live.advertiserId;
        alert.allocatedBudget = live.allocatedBudget;
        alert.spentBudget = live.spentBudget;
        alert.exhaustedAt = live.lastUpdateTime;
        return alert;
    }

}
