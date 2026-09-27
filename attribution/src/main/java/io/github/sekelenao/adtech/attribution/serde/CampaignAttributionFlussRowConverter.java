package io.github.sekelenao.adtech.attribution.serde;

import io.github.sekelenao.adtech.model.attribution.CampaignAttribution;
import org.apache.flink.table.data.GenericRowData;
import org.apache.flink.table.data.RowData;
import org.apache.flink.table.data.StringData;

import java.util.Objects;

/**
 * Utility to convert CampaignAttribution POJO to Flink RowData for Apache Fluss partial update sink.
 */
public final class CampaignAttributionFlussRowConverter {

    private CampaignAttributionFlussRowConverter() {
        throw new UnsupportedOperationException();
    }

    public static RowData toRowData(CampaignAttribution attribution) {
        Objects.requireNonNull(attribution);

        var row = new GenericRowData(4);
        row.setField(0, StringData.fromString(attribution.campaignId));
        row.setField(1, attribution.attributedConversionCount);
        row.setField(2, attribution.attributedRevenue);
        row.setField(3, attribution.lastUpdateTime);
        return row;
    }
}
