package io.github.sekelenao.demo.scenario.handler;

import io.github.sekelenao.adtech.model.event.AdImpression;
import io.github.sekelenao.demo.kafka.ImpressionPublisher;
import io.github.sekelenao.demo.model.Action;
import io.github.sekelenao.demo.model.ScenarioStep;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ImpressionStepHandlerTest {

    @Mock
    private ImpressionPublisher impressionPublisher;

    private ImpressionStepHandler handler;

    @BeforeEach
    void setUp() {
        handler = new ImpressionStepHandler(impressionPublisher);
    }

    @Test
    @DisplayName("Should return IMPRESSION action")
    void shouldReturnImpressionAction() {
        assertEquals(Action.IMPRESSION, handler.action());
    }

    @Test
    @DisplayName("Should publish ad impression")
    void shouldPublishAdImpression() {
        ScenarioStep step = new ScenarioStep(
            Action.IMPRESSION,
            "User Alice views TradingView display banner",
            "cmp-tv-charts",
            "adv-tradingview",
            "TradingView",
            "usr-alice",
            null,
            null,
            new BigDecimal("0.05"),
            null,
            null,
            0,
            0L,
            300L
        );

        handler.handle(step);

        ArgumentCaptor<AdImpression> captor = ArgumentCaptor.forClass(AdImpression.class);
        verify(impressionPublisher).publish(captor.capture());

        AdImpression published = captor.getValue();
        assertEquals("cmp-tv-charts", published.campaignId);
        assertEquals("adv-tradingview", published.advertiserId);
        assertEquals("usr-alice", published.userId);
        assertEquals(50_000L, published.cost);
        assertNotNull(published.impressionId);
    }
}
