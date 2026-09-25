package io.github.sekelenao.demo.scenario.handler;

import io.github.sekelenao.demo.model.Action;
import io.github.sekelenao.demo.model.ScenarioStep;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SleepStepHandlerTest {

    private SleepStepHandler handler;

    @BeforeEach
    void setUp() {
        handler = new SleepStepHandler();
    }

    @Test
    @DisplayName("Should return SLEEP action")
    void shouldReturnSleepAction() {
        assertEquals(Action.SLEEP, handler.action());
    }

    @Test
    @DisplayName("Should handle sleep step without error")
    void shouldHandleSleepStep() {
        ScenarioStep step = new ScenarioStep(
            Action.SLEEP,
            "Short sleep",
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            0,
            10L,
            0L
        );

        handler.handle(step);
    }
}
