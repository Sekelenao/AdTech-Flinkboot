package io.github.sekelenao.adtech.attribution.operator;

import io.github.sekelenao.adtech.model.attribution.AttributedConversion;
import io.github.sekelenao.adtech.model.event.AdClick;
import io.github.sekelenao.adtech.model.event.Conversion;
import io.github.sekelenao.flinkboot.test.api.sink.CollectingSink;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LastClickAttributionFunctionTest {

    private static final Duration ATTRIBUTION_WINDOW = Duration.ofMinutes(30);

    @Test
    @DisplayName("Scenario 1: Nominal last-click attribution within 30-min window")
    void shouldAttributeConversionWithinWindow() throws Exception {
        var env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        var click = new AdClick();
        click.timestamp = 1000L;
        click.clickId = "clk-1";
        click.impressionId = "imp-1";
        click.campaignId = "camp-1";
        click.advertiserId = "adv-1";
        click.userId = "user-1";
        click.cost = 500_000L;

        var conversion = new Conversion();
        conversion.timestamp = 1000L + Duration.ofMinutes(10).toMillis(); // T0 + 10 mins
        conversion.conversionId = "conv-1";
        conversion.advertiserId = "adv-1";
        conversion.userId = "user-1";
        conversion.orderAmount = 120_000_000L;

        try (var sink = new CollectingSink<AttributedConversion>()) {
            var clicksStream = env.fromData(click)
                .returns(AdClick.class)
                .keyBy(c -> c.userId);

            var conversionsStream = env.fromData(conversion)
                .map(conv -> {
                    Thread.sleep(50);
                    return conv;
                })
                .returns(Conversion.class)
                .keyBy(c -> c.userId);

            clicksStream.connect(conversionsStream)
                .process(new LastClickAttributionFunction(ATTRIBUTION_WINDOW))
                .sinkTo(sink);

            env.execute();

            var results = sink.elements();
            assertEquals(1, results.size());

            var attributed = results.get(0);
            assertAll(
                () -> assertEquals("camp-1", attributed.campaignId),
                () -> assertEquals("adv-1", attributed.advertiserId),
                () -> assertEquals("user-1", attributed.userId),
                () -> assertEquals("clk-1", attributed.clickId),
                () -> assertEquals("conv-1", attributed.conversionId),
                () -> assertEquals(120_000_000L, attributed.orderAmount),
                () -> assertEquals(1000L, attributed.clickTimestamp),
                () -> assertEquals(601_000L, attributed.conversionTimestamp)
            );
        }
    }

    @Test
    @DisplayName("Scenario 2: Conversion outside attribution window (> 30 mins) is not attributed")
    void shouldNotAttributeConversionOutsideWindow() throws Exception {
        var env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        var click = new AdClick();
        click.timestamp = 1000L;
        click.clickId = "clk-2";
        click.impressionId = "imp-2";
        click.campaignId = "camp-2";
        click.advertiserId = "adv-2";
        click.userId = "user-2";
        click.cost = 500_000L;

        var conversion = new Conversion();
        conversion.timestamp = 1000L + Duration.ofMinutes(35).toMillis(); // T0 + 35 mins (> 30m)
        conversion.conversionId = "conv-2";
        conversion.advertiserId = "adv-2";
        conversion.userId = "user-2";
        conversion.orderAmount = 80_000_000L;

        try (var sink = new CollectingSink<AttributedConversion>()) {
            var clicksStream = env.fromData(click)
                .returns(AdClick.class)
                .keyBy(c -> c.userId);

            var conversionsStream = env.fromData(conversion)
                .map(conv -> {
                    Thread.sleep(50);
                    return conv;
                })
                .returns(Conversion.class)
                .keyBy(c -> c.userId);

            clicksStream.connect(conversionsStream)
                .process(new LastClickAttributionFunction(ATTRIBUTION_WINDOW))
                .sinkTo(sink);

            env.execute();

            var results = sink.elements();
            assertTrue(results.isEmpty(), "Conversion outside attribution window must not be attributed");
        }
    }

    @Test
    @DisplayName("Scenario 3: Last click overrides previous click for the same advertiser")
    void shouldAttributeToLastClickForSameAdvertiser() throws Exception {
        var env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        var firstClick = new AdClick();
        firstClick.timestamp = 1000L;
        firstClick.clickId = "clk-first";
        firstClick.impressionId = "imp-1";
        firstClick.campaignId = "camp-first";
        firstClick.advertiserId = "adv-3";
        firstClick.userId = "user-3";
        firstClick.cost = 400_000L;

        var lastClick = new AdClick();
        lastClick.timestamp = 1000L + Duration.ofMinutes(5).toMillis();
        lastClick.clickId = "clk-last";
        lastClick.impressionId = "imp-2";
        lastClick.campaignId = "camp-last";
        lastClick.advertiserId = "adv-3";
        lastClick.userId = "user-3";
        lastClick.cost = 600_000L;

        var conversion = new Conversion();
        conversion.timestamp = 1000L + Duration.ofMinutes(10).toMillis();
        conversion.conversionId = "conv-3";
        conversion.advertiserId = "adv-3";
        conversion.userId = "user-3";
        conversion.orderAmount = 200_000_000L;

        try (var sink = new CollectingSink<AttributedConversion>()) {
            var clicksStream = env.fromData(firstClick, lastClick)
                .map(clk -> {
                    Thread.sleep(25);
                    return clk;
                })
                .returns(AdClick.class)
                .keyBy(c -> c.userId);

            var conversionsStream = env.fromData(conversion)
                .map(conv -> {
                    Thread.sleep(100);
                    return conv;
                })
                .returns(Conversion.class)
                .keyBy(c -> c.userId);

            clicksStream.connect(conversionsStream)
                .process(new LastClickAttributionFunction(ATTRIBUTION_WINDOW))
                .sinkTo(sink);

            env.execute();

            var results = sink.elements();
            assertEquals(1, results.size());

            var attributed = results.get(0);
            assertAll(
                () -> assertEquals("camp-last", attributed.campaignId),
                () -> assertEquals("clk-last", attributed.clickId),
                () -> assertEquals("adv-3", attributed.advertiserId),
                () -> assertEquals("user-3", attributed.userId),
                () -> assertEquals(301_000L, attributed.clickTimestamp)
            );
        }
    }

    @Test
    @DisplayName("Scenario 4: Multi-advertiser independence for the same user")
    void shouldMaintainIndependentAttributionPerAdvertiser() throws Exception {
        var env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        var nikeClick = new AdClick();
        nikeClick.timestamp = 1000L;
        nikeClick.clickId = "clk-nike";
        nikeClick.impressionId = "imp-nike";
        nikeClick.campaignId = "camp-nike";
        nikeClick.advertiserId = "adv-nike";
        nikeClick.userId = "user-4";
        nikeClick.cost = 500_000L;

        var bookingClick = new AdClick();
        bookingClick.timestamp = 2000L;
        bookingClick.clickId = "clk-booking";
        bookingClick.impressionId = "imp-booking";
        bookingClick.campaignId = "camp-booking";
        bookingClick.advertiserId = "adv-booking";
        bookingClick.userId = "user-4";
        bookingClick.cost = 700_000L;

        var nikeConversion = new Conversion();
        nikeConversion.timestamp = 5000L;
        nikeConversion.conversionId = "conv-nike";
        nikeConversion.advertiserId = "adv-nike";
        nikeConversion.userId = "user-4";
        nikeConversion.orderAmount = 150_000_000L;

        var bookingConversion = new Conversion();
        bookingConversion.timestamp = 6000L;
        bookingConversion.conversionId = "conv-booking";
        bookingConversion.advertiserId = "adv-booking";
        bookingConversion.userId = "user-4";
        bookingConversion.orderAmount = 300_000_000L;

        try (var sink = new CollectingSink<AttributedConversion>()) {
            var clicksStream = env.fromData(nikeClick, bookingClick)
                .map(clk -> {
                    Thread.sleep(25);
                    return clk;
                })
                .returns(AdClick.class)
                .keyBy(c -> c.userId);

            var conversionsStream = env.fromData(nikeConversion, bookingConversion)
                .map(conv -> {
                    Thread.sleep(100);
                    return conv;
                })
                .returns(Conversion.class)
                .keyBy(c -> c.userId);

            clicksStream.connect(conversionsStream)
                .process(new LastClickAttributionFunction(ATTRIBUTION_WINDOW))
                .sinkTo(sink);

            env.execute();

            var results = sink.elements();
            assertEquals(2, results.size());

            var nikeResult = results.stream()
                .filter(res -> "adv-nike".equals(res.advertiserId))
                .findFirst()
                .orElseThrow();
            assertAll(
                () -> assertEquals("camp-nike", nikeResult.campaignId),
                () -> assertEquals("clk-nike", nikeResult.clickId),
                () -> assertEquals(150_000_000L, nikeResult.orderAmount)
            );

            var bookingResult = results.stream()
                .filter(res -> "adv-booking".equals(res.advertiserId))
                .findFirst()
                .orElseThrow();
            assertAll(
                () -> assertEquals("camp-booking", bookingResult.campaignId),
                () -> assertEquals("clk-booking", bookingResult.clickId),
                () -> assertEquals(300_000_000L, bookingResult.orderAmount)
            );
        }
    }

    @Test
    @DisplayName("Scenario 5: Organic purchase without prior click produces no attribution")
    void shouldIgnoreOrganicConversionWithoutClick() throws Exception {
        var env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        var otherClick = new AdClick();
        otherClick.timestamp = 1000L;
        otherClick.clickId = "clk-other";
        otherClick.impressionId = "imp-other";
        otherClick.campaignId = "camp-other";
        otherClick.advertiserId = "adv-other";
        otherClick.userId = "user-other";
        otherClick.cost = 500_000L;

        var organicConversion = new Conversion();
        organicConversion.timestamp = 5000L;
        organicConversion.conversionId = "conv-organic";
        organicConversion.advertiserId = "adv-organic";
        organicConversion.userId = "user-5";
        organicConversion.orderAmount = 90_000_000L;

        try (var sink = new CollectingSink<AttributedConversion>()) {
            var clicksStream = env.fromData(otherClick)
                .returns(AdClick.class)
                .keyBy(c -> c.userId);

            var conversionsStream = env.fromData(organicConversion)
                .map(conv -> {
                    Thread.sleep(50);
                    return conv;
                })
                .returns(Conversion.class)
                .keyBy(c -> c.userId);

            clicksStream.connect(conversionsStream)
                .process(new LastClickAttributionFunction(ATTRIBUTION_WINDOW))
                .sinkTo(sink);

            env.execute();

            var results = sink.elements();
            assertTrue(results.isEmpty(), "Organic purchases without clicks must not be attributed");
        }
    }

    @Test
    @DisplayName("Scenario 6: Out-of-order negative time difference (conversion before click) is ignored")
    void shouldIgnoreConversionOccurringBeforeClick() throws Exception {
        var env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        var click = new AdClick();
        click.timestamp = 10000L;
        click.clickId = "clk-future";
        click.impressionId = "imp-6";
        click.campaignId = "camp-6";
        click.advertiserId = "adv-6";
        click.userId = "user-6";
        click.cost = 500_000L;

        var conversion = new Conversion();
        conversion.timestamp = 5000L; // Before click timestamp
        conversion.conversionId = "conv-past";
        conversion.advertiserId = "adv-6";
        conversion.userId = "user-6";
        conversion.orderAmount = 70_000_000L;

        try (var sink = new CollectingSink<AttributedConversion>()) {
            var clicksStream = env.fromData(click)
                .returns(AdClick.class)
                .keyBy(c -> c.userId);

            var conversionsStream = env.fromData(conversion)
                .map(conv -> {
                    Thread.sleep(50);
                    return conv;
                })
                .returns(Conversion.class)
                .keyBy(c -> c.userId);

            clicksStream.connect(conversionsStream)
                .process(new LastClickAttributionFunction(ATTRIBUTION_WINDOW))
                .sinkTo(sink);

            env.execute();

            var results = sink.elements();
            assertTrue(results.isEmpty(), "Conversion with timestamp prior to click must not be attributed");
        }
    }

    @Test
    @DisplayName("Scenario 7: Multiple distinct conversions within attribution window attributed to the same click")
    void shouldAttributeMultipleConversionsToSameClick() throws Exception {
        var env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        var click = new AdClick();
        click.timestamp = 1000L;
        click.clickId = "clk-multi";
        click.impressionId = "imp-multi";
        click.campaignId = "camp-multi";
        click.advertiserId = "adv-multi";
        click.userId = "user-multi";
        click.cost = 500_000L;

        var firstConversion = new Conversion();
        firstConversion.timestamp = 1000L + Duration.ofMinutes(5).toMillis(); // T0 + 5m
        firstConversion.conversionId = "conv-1";
        firstConversion.advertiserId = "adv-multi";
        firstConversion.userId = "user-multi";
        firstConversion.orderAmount = 50_000_000L;

        var secondConversion = new Conversion();
        secondConversion.timestamp = 1000L + Duration.ofMinutes(15).toMillis(); // T0 + 15m
        secondConversion.conversionId = "conv-2";
        secondConversion.advertiserId = "adv-multi";
        secondConversion.userId = "user-multi";
        secondConversion.orderAmount = 80_000_000L;

        try (var sink = new CollectingSink<AttributedConversion>()) {
            var clicksStream = env.fromData(click)
                .returns(AdClick.class)
                .keyBy(c -> c.userId);

            var conversionsStream = env.fromData(firstConversion, secondConversion)
                .map(conv -> {
                    Thread.sleep(50);
                    return conv;
                })
                .returns(Conversion.class)
                .keyBy(c -> c.userId);

            clicksStream.connect(conversionsStream)
                .process(new LastClickAttributionFunction(ATTRIBUTION_WINDOW))
                .sinkTo(sink);

            env.execute();

            var results = sink.elements();
            assertEquals(2, results.size());

            var firstResult = results.get(0);
            assertAll(
                () -> assertEquals("camp-multi", firstResult.campaignId),
                () -> assertEquals("clk-multi", firstResult.clickId),
                () -> assertEquals("conv-1", firstResult.conversionId),
                () -> assertEquals(50_000_000L, firstResult.orderAmount)
            );

            var secondResult = results.get(1);
            assertAll(
                () -> assertEquals("camp-multi", secondResult.campaignId),
                () -> assertEquals("clk-multi", secondResult.clickId),
                () -> assertEquals("conv-2", secondResult.conversionId),
                () -> assertEquals(80_000_000L, secondResult.orderAmount)
            );
        }
    }
}
