package io.github.sekelenao.adtech.model.budget;

import io.github.sekelenao.adtech.model.event.AdClick;
import io.github.sekelenao.adtech.model.event.AdImpression;

import java.io.Serializable;
import java.util.Objects;

/**
 * Unified event representing a spend increment (from an impression or click) impacting campaign budget.
 */
public class CampaignSpendEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    public long timestamp;
    public String eventId;
    public String campaignId;
    public String advertiserId;
    public long cost;
    public SpendType type;

    public static CampaignSpendEvent from(AdImpression impression) {
        Objects.requireNonNull(impression);
        var spend = new CampaignSpendEvent();
        spend.timestamp = impression.timestamp;
        spend.eventId = impression.impressionId;
        spend.campaignId = impression.campaignId;
        spend.advertiserId = impression.advertiserId;
        spend.cost = impression.cost;
        spend.type = SpendType.IMPRESSION;
        return spend;
    }

    public static CampaignSpendEvent from(AdClick click) {
        Objects.requireNonNull(click);
        var spend = new CampaignSpendEvent();
        spend.timestamp = click.timestamp;
        spend.eventId = click.clickId;
        spend.campaignId = click.campaignId;
        spend.advertiserId = click.advertiserId;
        spend.cost = click.cost;
        spend.type = SpendType.CLICK;
        return spend;
    }
}
