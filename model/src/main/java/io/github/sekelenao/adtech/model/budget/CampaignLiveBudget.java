package io.github.sekelenao.adtech.model.budget;

import java.io.Serializable;

/**
 * Represents the real-time budget and performance metrics of an advertising campaign.
 */
public class CampaignLiveBudget implements Serializable {

    private static final long serialVersionUID = 1L;

    public String campaignId;
    public String advertiserId;
    public CampaignStatus status;
    public long allocatedBudget;
    public long spentBudget;
    public long remainingBudget;
    public long impressionCount;
    public long clickCount;
    public long attributedConversionCount;
    public long attributedRevenue;
    public long lastUpdateTime;
}
