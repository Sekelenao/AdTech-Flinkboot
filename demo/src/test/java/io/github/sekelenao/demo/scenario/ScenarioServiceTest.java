package io.github.sekelenao.demo.scenario;

import io.github.sekelenao.demo.model.Scenario;
import io.github.sekelenao.demo.model.ScenarioStep;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ScenarioServiceTest {

    @Mock
    private ScenarioResourceLoader scenarioResourceLoader;

    @Mock
    private ScenarioRunner scenarioRunner;

    private ScenarioService scenarioService;

    private Scenario showcaseScenario;

    @BeforeEach
    void setUp() {
        scenarioService = new ScenarioService(scenarioResourceLoader, scenarioRunner);
        showcaseScenario = new Scenario("showcase-demo", "Showcase demo", List.of());
    }

    @Test
    @DisplayName("Should list all available scenarios")
    void shouldListAllScenarios() throws IOException {
        when(scenarioResourceLoader.retrieveAll()).thenReturn(List.of(showcaseScenario));

        var scenarios = scenarioService.listScenarios();

        assertEquals(1, scenarios.size());
        assertEquals("showcase-demo", scenarios.getFirst().name());
    }

    @Test
    @DisplayName("Should find scenario by name when present")
    void shouldFindScenarioByNameWhenPresent() throws IOException {
        when(scenarioResourceLoader.retrieveAll()).thenReturn(List.of(showcaseScenario));

        Optional<Scenario> result = scenarioService.getScenario("showcase-demo");

        assertTrue(result.isPresent());
        assertEquals("showcase-demo", result.get().name());
    }

    @Test
    @DisplayName("Should return empty optional when scenario not found")
    void shouldReturnEmptyOptionalWhenNotFound() throws IOException {
        when(scenarioResourceLoader.retrieveAll()).thenReturn(List.of(showcaseScenario));

        Optional<Scenario> result = scenarioService.getScenario("unknown-scenario");

        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Should run scenario by name")
    void shouldRunScenarioByName() throws IOException {
        when(scenarioResourceLoader.retrieveAll()).thenReturn(List.of(showcaseScenario));
        when(scenarioRunner.run(showcaseScenario)).thenReturn(CompletableFuture.completedFuture(null));

        scenarioService.runScenario("showcase-demo");

        verify(scenarioRunner).run(showcaseScenario);
        assertFalse(scenarioService.isRunning());
    }

    @Test
    @DisplayName("Should prevent running multiple scenarios concurrently")
    void shouldPreventConcurrentScenarioExecutions() throws IOException {
        when(scenarioResourceLoader.retrieveAll()).thenReturn(List.of(showcaseScenario));
        var pendingFuture = new CompletableFuture<Void>();
        when(scenarioRunner.run(showcaseScenario)).thenReturn(pendingFuture);

        scenarioService.runScenario("showcase-demo");
        assertTrue(scenarioService.isRunning());

        var exception = assertThrows(IllegalStateException.class, () -> scenarioService.runScenario("showcase-demo"));
        assertEquals("A scenario is already running. Please wait for it to complete.", exception.getMessage());

        pendingFuture.complete(null);
        assertFalse(scenarioService.isRunning());
    }

    @Test
    @DisplayName("Should reset running flag when scenario execution fails asynchronously")
    void shouldResetRunningFlagWhenScenarioFailsAsync() throws IOException {
        when(scenarioResourceLoader.retrieveAll()).thenReturn(List.of(showcaseScenario));
        var failingFuture = new CompletableFuture<Void>();
        when(scenarioRunner.run(showcaseScenario)).thenReturn(failingFuture);

        scenarioService.runScenario("showcase-demo");
        assertTrue(scenarioService.isRunning());

        failingFuture.completeExceptionally(new RuntimeException("Async step failure"));
        assertFalse(scenarioService.isRunning());
    }

    @Test
    @DisplayName("Should reset running flag when scenarioRunner throws synchronously")
    void shouldResetRunningFlagWhenRunnerThrowsSynchronously() throws IOException {
        when(scenarioResourceLoader.retrieveAll()).thenReturn(List.of(showcaseScenario));
        when(scenarioRunner.run(showcaseScenario)).thenThrow(new RuntimeException("Direct failure"));

        assertThrows(RuntimeException.class, () -> scenarioService.runScenario("showcase-demo"));
        assertFalse(scenarioService.isRunning());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when running non-existent scenario")
    void shouldThrowWhenRunningNonExistentScenario() throws IOException {
        when(scenarioResourceLoader.retrieveAll()).thenReturn(List.of(showcaseScenario));

        assertThrows(IllegalArgumentException.class, () -> scenarioService.runScenario("non-existent"));
    }

    @Test
    @DisplayName("Should wrap IOException in UncheckedIOException when retrieveAll fails")
    void shouldWrapIOExceptionInUncheckedIOException() throws IOException {
        when(scenarioResourceLoader.retrieveAll()).thenThrow(new IOException("Disk error"));

        assertThrows(UncheckedIOException.class, () -> scenarioService.listScenarios());
    }

    @Test
    @DisplayName("Should list unique known campaign contracts filtering nulls")
    void shouldListKnownContracts() throws IOException {
        var step1 = new ScenarioStep(null, null, "cmp-google", null, null, null, null, null, null, null, null, null, null, null);
        var step2 = new ScenarioStep(null, null, "cmp-uber", null, null, null, null, null, null, null, null, null, null, null);
        var stepDuplicate = new ScenarioStep(null, null, "cmp-google", null, null, null, null, null, null, null, null, null, null, null);
        var stepNullCampaign = new ScenarioStep(null, null, null, null, null, null, null, null, null, null, null, null, null, null);

        var scenario = new Scenario("test-scenario", "description", List.of(step1, step2, stepDuplicate, stepNullCampaign));
        when(scenarioResourceLoader.retrieveAll()).thenReturn(List.of(scenario));

        var contracts = scenarioService.listContracts();

        assertEquals(Set.of("cmp-google", "cmp-uber"), contracts);
    }
}
