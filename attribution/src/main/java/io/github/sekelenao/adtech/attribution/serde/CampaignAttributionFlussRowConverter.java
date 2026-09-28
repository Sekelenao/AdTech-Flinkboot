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

        var row = new GenericRowData(11);
        row.setField(0, StringData.fromString(attribution.campaignId));
        row.setField(8, attribution.attributedConversionCount);
        row.setField(9, attribution.attributedRevenue);
        row.setField(10, attribution.lastUpdateTime);
        return row;
    }
}
