package io.github.sekelenao.demo.scenario.handler;

import io.github.sekelenao.adtech.model.event.AdClick;
import io.github.sekelenao.demo.kafka.ClickPublisher;
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
class ClickStepHandlerTest {

    @Mock
    private ClickPublisher clickPublisher;

    private ClickStepHandler handler;

    @BeforeEach
    void setUp() {
        handler = new ClickStepHandler(clickPublisher);
    }

    @Test
    @DisplayName("Should return CLICK action")
    void shouldReturnClickAction() {
        assertEquals(Action.CLICK, handler.action());
    }

    @Test
    @DisplayName("Should publish ad click")
    void shouldPublishAdClick() {
        ScenarioStep step = new ScenarioStep(
            Action.CLICK,
            "User Alice clicks Google Pixel 9 ad",
            "cmp-google-pixel9",
            "adv-google",
            "Google",
            "usr-alice",
            null,
            null,
            new BigDecimal("0.75"),
            null,
            null,
            0,
            0,
            1200
        );

        handler.handle(step);

        ArgumentCaptor<AdClick> captor = ArgumentCaptor.forClass(AdClick.class);
        verify(clickPublisher).publish(captor.capture());

        AdClick published = captor.getValue();
        assertEquals("cmp-google-pixel9", published.campaignId);
        assertEquals("adv-google", published.advertiserId);
        assertEquals("usr-alice", published.userId);
        assertEquals(750_000L, published.cost);
        assertNotNull(published.clickId);
        assertNotNull(published.impressionId);
    }
}
