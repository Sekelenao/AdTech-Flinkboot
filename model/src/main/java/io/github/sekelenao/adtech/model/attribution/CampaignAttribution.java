package io.github.sekelenao.adtech.model.attribution;

import java.io.Serializable;

/**
 * Represents the real-time cumulative attribution metrics of an advertising campaign.
 */
public class CampaignAttribution implements Serializable {

    private static final long serialVersionUID = 1L;

    public String campaignId;

    public long attributedConversionCount;

    public long attributedRevenue;

    public long lastUpdateTime;
}
