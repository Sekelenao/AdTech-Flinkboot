package io.github.sekelenao.adtech.model.budget;

import java.io.Serializable;

/**
 * Represents the allocated budget envelope and operational status of an advertising campaign.
 */
public class CampaignBudget implements Serializable {

    private static final long serialVersionUID = 1L;

    public String campaignId;
    public String advertiserId;
    public long allocatedBudget;
    public CampaignStatus status;
    public long updatedAt;
}
