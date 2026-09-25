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
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BurstClicksStepHandlerTest {

    @Mock
    private ClickPublisher clickPublisher;

    private BurstClicksStepHandler handler;

    @BeforeEach
    void setUp() {
        handler = new BurstClicksStepHandler(clickPublisher);
    }

    @Test
    @DisplayName("Should return BURST_CLICKS action")
    void shouldReturnBurstClicksAction() {
        assertEquals(Action.BURST_CLICKS, handler.action());
    }

    @Test
    @DisplayName("Should publish burst of clicks")
    void shouldPublishBurstOfClicks() {
        ScenarioStep step = new ScenarioStep(
            Action.BURST_CLICKS,
            "Burst of 3 clicks",
            "cmp-uber-rides",
            "adv-uber",
            "Uber",
            "usr-bob",
            null,
            null,
            new BigDecimal("0.50"),
            null,
            null,
            3,
            0,
            2000
        );

        handler.handle(step);

        ArgumentCaptor<AdClick> captor = ArgumentCaptor.forClass(AdClick.class);
        verify(clickPublisher, times(3)).publish(captor.capture());

        List<AdClick> publishedClicks = captor.getAllValues();
        assertEquals(3, publishedClicks.size());
        for (AdClick click : publishedClicks) {
            assertEquals("cmp-uber-rides", click.campaignId);
            assertEquals("adv-uber", click.advertiserId);
            assertEquals("usr-bob", click.userId);
            assertEquals(500_000L, click.cost);
        }
    }
}
