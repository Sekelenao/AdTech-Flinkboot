package io.github.sekelenao.adtech.model.attribution;

import io.github.sekelenao.adtech.model.event.AdClick;
import io.github.sekelenao.adtech.model.event.Conversion;

import java.io.Serializable;
import java.util.Objects;

/**
 * Represents a conversion attributed to a specific ad click and campaign.
 */
public class AttributedConversion implements Serializable {

    private static final long serialVersionUID = 1L;

    public String campaignId;
    public String advertiserId;
    public String userId;
    public String clickId;
    public String conversionId;
    public long orderAmount;
    public long clickTimestamp;
    public long conversionTimestamp;

    public static AttributedConversion from(AdClick click, Conversion conversion) {
        Objects.requireNonNull(click);
        Objects.requireNonNull(conversion);
        var attributed = new AttributedConversion();
        attributed.campaignId = click.campaignId;
        attributed.advertiserId = conversion.advertiserId;
        attributed.userId = conversion.userId;
        attributed.clickId = click.clickId;
        attributed.conversionId = conversion.conversionId;
        attributed.orderAmount = conversion.orderAmount;
        attributed.clickTimestamp = click.timestamp;
        attributed.conversionTimestamp = conversion.timestamp;
        return attributed;
    }
}
