package io.github.sekelenao.demo.scenario;

import io.github.sekelenao.demo.model.Scenario;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;
import java.util.Objects;

/**
 * Provides scenario catalog and execution status to all view templates.
 */
@ControllerAdvice
public class ScenarioHeaderAdvice {

    private final ScenarioService scenarioService;

    public ScenarioHeaderAdvice(ScenarioService scenarioService) {
        this.scenarioService = Objects.requireNonNull(scenarioService);
    }

    @ModelAttribute("scenarios")
    public List<Scenario> scenarios() {
        return scenarioService.all();
    }

    @ModelAttribute("isRunning")
    public boolean isRunning() {
        return scenarioService.isRunning();
    }
}
