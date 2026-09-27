package io.github.sekelenao.adtech.attribution.operator;

import io.github.sekelenao.adtech.model.attribution.AttributedConversion;
import io.github.sekelenao.adtech.model.attribution.CampaignAttribution;
import io.github.sekelenao.flinkboot.test.api.sink.CollectingSink;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CampaignAttributionProcessFunctionTest {

    @Test
    @DisplayName("Should accumulate conversions and revenue per campaign")
    void shouldAccumulateConversionsAndRevenue() throws Exception {
        var env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        var conv1 = new AttributedConversion();
        conv1.campaignId = "cmp-1";
        conv1.orderAmount = 100_000_000L;
        conv1.conversionTimestamp = 1000L;

        var conv2 = new AttributedConversion();
        conv2.campaignId = "cmp-1";
        conv2.orderAmount = 50_000_000L;
        conv2.conversionTimestamp = 2000L;

        var conv3 = new AttributedConversion();
        conv3.campaignId = "cmp-2";
        conv3.orderAmount = 75_000_000L;
        conv3.conversionTimestamp = 3000L;

        try (var sink = new CollectingSink<CampaignAttribution>()) {
            env.fromData(conv1, conv2, conv3)
                .returns(AttributedConversion.class)
                .keyBy(c -> c.campaignId)
                .process(new CampaignAttributionProcessFunction())
                .sinkTo(sink);

            env.execute();

            var results = sink.elements();
            assertEquals(3, results.size());

            var first = results.get(0);
            assertAll(
                () -> assertEquals("cmp-1", first.campaignId),
                () -> assertEquals(1L, first.attributedConversionCount),
                () -> assertEquals(100_000_000L, first.attributedRevenue),
                () -> assertEquals(1000L, first.lastUpdateTime)
            );

            var second = results.get(1);
            assertAll(
                () -> assertEquals("cmp-1", second.campaignId),
                () -> assertEquals(2L, second.attributedConversionCount),
                () -> assertEquals(150_000_000L, second.attributedRevenue),
                () -> assertEquals(2000L, second.lastUpdateTime)
            );

            var third = results.get(2);
            assertAll(
                () -> assertEquals("cmp-2", third.campaignId),
                () -> assertEquals(1L, third.attributedConversionCount),
                () -> assertEquals(75_000_000L, third.attributedRevenue),
                () -> assertEquals(3000L, third.lastUpdateTime)
            );
        }
    }
}
