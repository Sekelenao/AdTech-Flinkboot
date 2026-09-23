package io.github.sekelenao.adtech.attribution;

import io.github.sekelenao.adtech.attribution.configuration.AttributionJobConfiguration;
import io.github.sekelenao.adtech.attribution.operator.LastClickAttributionFunction;
import io.github.sekelenao.adtech.attribution.serde.JsonDeserializer;
import io.github.sekelenao.adtech.attribution.serde.JsonSerializer;
import io.github.sekelenao.adtech.model.attribution.AttributedConversion;
import io.github.sekelenao.adtech.model.event.AdClick;
import io.github.sekelenao.adtech.model.event.Conversion;
import io.github.sekelenao.flinkboot.core.api.Flinkboot;
import io.github.sekelenao.flinkboot.kafka.api.sink.KafkaSinkFactory;
import io.github.sekelenao.flinkboot.kafka.api.source.KafkaSourceFactory;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.connector.kafka.sink.KafkaRecordSerializationSchema;
import org.apache.flink.connector.kafka.source.reader.deserializer.KafkaRecordDeserializationSchema;

/**
 * Main streaming pipeline application for real-time last-click attribution.
 *
 * <p>Pipeline topology:
 * <ol>
 *   <li>Consumes {@link AdClick} stream from Kafka, keyed by {@code userId}.</li>
 *   <li>Consumes {@link Conversion} stream from Kafka, keyed by {@code userId}.</li>
 *   <li>Connects both streams in {@link LastClickAttributionFunction}, correlating purchases with
 *       the most recent valid click per advertiser within the configured attribution window.</li>
 *   <li>Emits correlated {@link AttributedConversion} events to the Kafka attribution topic.</li>
 * </ol>
 */
public class AttributionJob {

    public static void main(String[] args) throws Exception {
        var boot = Flinkboot.initialize(args);
        var configuration = boot.configuration(AttributionJobConfiguration.class);
        var env = boot.executionEnvironment(configuration.job());

        // 1. Clicks source
        var clicksSource = KafkaSourceFactory.supplyFor(
            configuration.clicksSource(),
            KafkaRecordDeserializationSchema.valueOnly(new JsonDeserializer<>(AdClick.class))
        );
        var clicksStream = env.fromSource(
            clicksSource,
            WatermarkStrategy.noWatermarks(),
            configuration.clicksSource().name()
        );

        // 2. Conversions source
        var conversionsSource = KafkaSourceFactory.supplyFor(
            configuration.conversionsSource(),
            KafkaRecordDeserializationSchema.valueOnly(new JsonDeserializer<>(Conversion.class))
        );
        var conversionsStream = env.fromSource(
            conversionsSource,
            WatermarkStrategy.noWatermarks(),
            configuration.conversionsSource().name()
        );

        // 3. Connect and attribute
        var attributedConversionsStream = clicksStream.keyBy(click -> click.userId)
            .connect(conversionsStream.keyBy(conversion -> conversion.userId))
            .process(new LastClickAttributionFunction(configuration.attribution().window()))
            .name("last-click-attribution-processor");

        // 4. Kafka Sink
        var recordSerializationSchema = KafkaRecordSerializationSchema.<AttributedConversion>builder()
            .setTopic(configuration.attributedConversionsSink().topic())
            .setValueSerializationSchema(new JsonSerializer<>())
            .build();

        var sink = KafkaSinkFactory.supplyFor(configuration.attributedConversionsSink(), recordSerializationSchema);

        attributedConversionsStream
            .sinkTo(sink)
            .name(configuration.attributedConversionsSink().name());

        // 5. Execute job
        env.execute(configuration.job().name());
    }
}
