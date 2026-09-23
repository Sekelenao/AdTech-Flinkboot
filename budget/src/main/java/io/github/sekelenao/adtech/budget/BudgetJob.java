package io.github.sekelenao.adtech.budget;

import io.github.sekelenao.adtech.budget.configuration.BudgetJobConfiguration;
import io.github.sekelenao.adtech.budget.operator.CampaignBudgetBroadcastProcessFunction;
import io.github.sekelenao.adtech.budget.serde.CampaignLiveBudgetFlussRowConverter;
import io.github.sekelenao.adtech.budget.serde.JsonDeserializer;
import io.github.sekelenao.adtech.budget.serde.JsonSerializer;
import io.github.sekelenao.adtech.model.budget.BudgetExhaustedAlert;
import io.github.sekelenao.adtech.model.budget.CampaignBudget;
import io.github.sekelenao.adtech.model.budget.CampaignSpendEvent;
import io.github.sekelenao.adtech.model.event.AdClick;
import io.github.sekelenao.adtech.model.event.AdImpression;
import io.github.sekelenao.flinkboot.core.api.Flinkboot;
import io.github.sekelenao.flinkboot.fluss.api.sink.FlussSinkFactory;
import io.github.sekelenao.flinkboot.kafka.api.sink.KafkaSinkFactory;
import io.github.sekelenao.flinkboot.kafka.api.source.KafkaSourceFactory;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.connector.kafka.sink.KafkaRecordSerializationSchema;
import org.apache.flink.connector.kafka.source.reader.deserializer.KafkaRecordDeserializationSchema;
import org.apache.fluss.flink.sink.serializer.RowDataSerializationSchema;

/**
 * Main streaming pipeline application for real-time budget engine and pacing.
 *
 * <p>Pipeline topology:
 * <ol>
 *   <li>Consumes {@link CampaignBudget} updates and broadcasts to all tasks.</li>
 *   <li>Consumes {@link AdImpression} and {@link AdClick} streams, maps to {@link CampaignSpendEvent},
 *       unions them, and keys by campaign ID.</li>
 *   <li>Connects spend events with broadcast campaign budgets in
 *       {@link CampaignBudgetBroadcastProcessFunction}.</li>
 *   <li>Emits updated {@link io.github.sekelenao.adtech.model.budget.CampaignLiveBudget} to Apache Fluss table
 *       via O(1) primary key upserts.</li>
 *   <li>Emits {@link BudgetExhaustedAlert} side-output to Kafka alert topic upon budget capping.</li>
 * </ol>
 */
public class BudgetJob {

    public static void main(String[] args) throws Exception {
        var boot = Flinkboot.initialize(args);
        var configuration = boot.configuration(BudgetJobConfiguration.class);
        var env = boot.executionEnvironment(configuration.job());

        // 1. Campaign budget broadcast source
        var campaignBudgetSource = KafkaSourceFactory.supplyFor(
            configuration.campaignBudgetSource(),
            KafkaRecordDeserializationSchema.valueOnly(new JsonDeserializer<>(CampaignBudget.class))
        );
        var campaignBudgetBroadcast = env.fromSource(
            campaignBudgetSource,
            WatermarkStrategy.noWatermarks(),
            configuration.campaignBudgetSource().name()
        ).broadcast(CampaignBudgetBroadcastProcessFunction.CAMPAIGN_BUDGET_STATE_DESCRIPTOR);

        // 2. Impressions spend stream
        var impressionsSource = KafkaSourceFactory.supplyFor(
            configuration.impressionsSource(),
            KafkaRecordDeserializationSchema.valueOnly(new JsonDeserializer<>(AdImpression.class))
        );
        var impressionsStream = env.fromSource(
            impressionsSource,
            WatermarkStrategy.noWatermarks(),
            configuration.impressionsSource().name()
        ).map(CampaignSpendEvent::from);

        // 3. Clicks spend stream
        var clicksSource = KafkaSourceFactory.supplyFor(
            configuration.clicksSource(),
            KafkaRecordDeserializationSchema.valueOnly(new JsonDeserializer<>(AdClick.class))
        );
        var clicksStream = env.fromSource(
            clicksSource,
            WatermarkStrategy.noWatermarks(),
            configuration.clicksSource().name()
        ).map(CampaignSpendEvent::from);

        // 4. Unified spend stream keyed by campaignId
        var spendStream = impressionsStream.union(clicksStream)
            .keyBy(spend -> spend.campaignId);

        // 5. Connect and process
        var liveBudgetStream = spendStream.connect(campaignBudgetBroadcast)
            .process(new CampaignBudgetBroadcastProcessFunction())
            .name("campaign-budget-processor");

        // 6. Fluss Sink (O(1) updates)
        var flussSink = FlussSinkFactory.supplyFor(
            configuration.flussSink(),
            new RowDataSerializationSchema(false, true)
        );
        liveBudgetStream
            .map(CampaignLiveBudgetFlussRowConverter::toRowData)
            .sinkTo(flussSink)
            .name(configuration.flussSink().name());

        // 7. Kafka Alert Sink (side-output)
        var alertSerializationSchema = KafkaRecordSerializationSchema.<BudgetExhaustedAlert>builder()
            .setTopic(configuration.alertsSink().topic())
            .setValueSerializationSchema(new JsonSerializer<>())
            .build();
        var alertSink = KafkaSinkFactory.supplyFor(configuration.alertsSink(), alertSerializationSchema);

        liveBudgetStream.getSideOutput(CampaignBudgetBroadcastProcessFunction.BUDGET_EXHAUSTED_TAG)
            .sinkTo(alertSink)
            .name(configuration.alertsSink().name());

        // 8. Execute job
        env.execute(configuration.job().name());
    }

}
