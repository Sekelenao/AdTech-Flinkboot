package io.github.sekelenao.demo.scenario;

import io.github.sekelenao.demo.model.Action;
import io.github.sekelenao.demo.model.Scenario;
import io.github.sekelenao.demo.model.ScenarioStep;
import io.github.sekelenao.demo.scenario.handler.StepHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ScenarioRunnerTest {

    @Mock
    private StepHandler clickHandler;

    @Mock
    private StepHandler impressionHandler;

    @Test
    @DisplayName("Should execute all steps sequentially with registered handlers")
    void shouldExecuteAllStepsSequentially() {
        when(clickHandler.action()).thenReturn(Action.CLICK);
        when(impressionHandler.action()).thenReturn(Action.IMPRESSION);

        var runner = new ScenarioRunner(List.of(clickHandler, impressionHandler));

        var step1 = new ScenarioStep(Action.CLICK, "Click step", null, null, null, null, null, null, null, null, null, null, null, null);
        var step2 = new ScenarioStep(Action.IMPRESSION, "Impression step", null, null, null, null, null, null, null, null, null, null, null, null);
        var scenario = new Scenario("test-scenario", "description", List.of(step1, step2));

        CompletableFuture<Void> future = runner.run(scenario);

        assertNotNull(future);
        assertTrue(future.isDone());
        verify(clickHandler).handle(step1);
        verify(impressionHandler).handle(step2);
    }

    @Test
    @DisplayName("Should throw IllegalStateException when no handler registered for action")
    void shouldThrowWhenNoHandlerRegistered() {
        when(clickHandler.action()).thenReturn(Action.CLICK);

        var runner = new ScenarioRunner(List.of(clickHandler));

        var step = new ScenarioStep(Action.IMPRESSION, "Impression step", null, null, null, null, null, null, null, null, null, null, null, null);
        var scenario = new Scenario("test-scenario", "description", List.of(step));

        var exception = assertThrows(IllegalStateException.class, () -> runner.run(scenario));
        assertEquals("No handler registered for action: IMPRESSION", exception.getMessage());
    }
}
