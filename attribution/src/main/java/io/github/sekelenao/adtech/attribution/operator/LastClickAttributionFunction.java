package io.github.sekelenao.adtech.attribution.operator;

import io.github.sekelenao.adtech.model.attribution.AttributedConversion;
import io.github.sekelenao.adtech.model.event.AdClick;
import io.github.sekelenao.adtech.model.event.Conversion;
import org.apache.flink.api.common.functions.OpenContext;
import org.apache.flink.api.common.state.MapState;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.api.common.state.StateTtlConfig;
import org.apache.flink.streaming.api.functions.co.KeyedCoProcessFunction;
import org.apache.flink.util.Collector;

import java.time.Duration;
import java.util.Objects;

/**
 * Keyed co-process operator implementing advertiser-scoped last-click attribution.
 *
 * <p>Stream 1: {@link AdClick} keyed by {@code userId}.
 * <p>Stream 2: {@link Conversion} keyed by {@code userId}.
 *
 * <p>State:
 * <ul>
 *   <li>{@link MapState} keyed by {@code advertiserId} containing the most recent {@link AdClick}.</li>
 *   <li>Configured with Flink State TTL to automatically expire clicks after the configured window.</li>
 * </ul>
 */
public class LastClickAttributionFunction extends KeyedCoProcessFunction<String, AdClick, Conversion, AttributedConversion> {

    private static final long serialVersionUID = 1L;

    private final Duration attributionWindow;

    private transient MapState<String, AdClick> lastClicksState;

    public LastClickAttributionFunction(Duration attributionWindow) {
        this.attributionWindow = Objects.requireNonNull(attributionWindow, "attributionWindow must not be null");
    }

    @Override
    public void open(OpenContext openContext) {
        var ttlConfig = StateTtlConfig.newBuilder(attributionWindow)
            .updateTtlOnCreateAndWrite()
            .neverReturnExpired()
            .build();

        var descriptor = new MapStateDescriptor<>("last-clicks-by-advertiser", String.class, AdClick.class);
        descriptor.enableTimeToLive(ttlConfig);
        this.lastClicksState = getRuntimeContext().getMapState(descriptor);
    }

    @Override
    public void processElement1(AdClick click, Context ctx, Collector<AttributedConversion> out) throws Exception {
        lastClicksState.put(click.advertiserId, click);
    }

    @Override
    public void processElement2(Conversion conversion, Context ctx, Collector<AttributedConversion> out) throws Exception {
        var lastClick = lastClicksState.get(conversion.advertiserId);
        if (lastClick == null) {
            return;
        }
        var timeDiff = conversion.timestamp - lastClick.timestamp;
        if (timeDiff < 0 || timeDiff > attributionWindow.toMillis()) {
            return;
        }
        out.collect(AttributedConversion.from(lastClick, conversion));
    }
}
