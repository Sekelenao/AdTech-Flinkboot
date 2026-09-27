package io.github.sekelenao.adtech.attribution.operator;

import io.github.sekelenao.adtech.model.attribution.AttributedConversion;
import io.github.sekelenao.adtech.model.attribution.CampaignAttribution;
import org.apache.flink.api.common.functions.OpenContext;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;

import java.util.Objects;

/**
 * Keyed process operator aggregating cumulative conversions and revenue per campaign.
 */
public class CampaignAttributionProcessFunction extends KeyedProcessFunction<String, AttributedConversion, CampaignAttribution> {

    private static final long serialVersionUID = 1L;

    private transient ValueState<CampaignAttribution> attributionState;

    @Override
    public void open(OpenContext openContext) {
        var descriptor = new ValueStateDescriptor<>("campaign-attribution-state", CampaignAttribution.class);
        this.attributionState = getRuntimeContext().getState(descriptor);
    }

    @Override
    public void processElement(AttributedConversion conversion, Context ctx, Collector<CampaignAttribution> out) throws Exception {
        Objects.requireNonNull(conversion);

        var attribution = attributionState.value();
        if (attribution == null) {
            attribution = new CampaignAttribution();
            attribution.campaignId = conversion.campaignId;
        }

        attribution.attributedConversionCount += 1L;
        attribution.attributedRevenue += conversion.orderAmount;
        attribution.lastUpdateTime = conversion.conversionTimestamp;

        attributionState.update(attribution);
        out.collect(attribution);
    }
}
