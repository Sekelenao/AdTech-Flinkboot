package io.github.sekelenao.demo.scenario;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Objects;

/**
 * Controller handling execution of demo scenarios.
 */
@Controller
public class ScenarioController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScenarioController.class);

    private final ScenarioService scenarioService;

    public ScenarioController(ScenarioService scenarioService) {
        this.scenarioService = Objects.requireNonNull(scenarioService);
    }

    @PostMapping("/scenarios/run")
    public String runScenarioFromForm(
        @RequestParam("name") String name,
        HttpServletRequest request,
        RedirectAttributes redirectAttributes
    ) {
        return runScenario(name, request, redirectAttributes);
    }

    @PostMapping("/scenarios/{name}/run")
    public String runScenario(
        @PathVariable("name") String name,
        HttpServletRequest request,
        RedirectAttributes redirectAttributes
    ) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(request);
        Objects.requireNonNull(redirectAttributes);
        try {
            LOGGER.info("User requested execution of scenario '{}'", name);
            scenarioService.run(name);
            redirectAttributes.addFlashAttribute("successMessage", "Scenario '" + name + "' launched successfully!");
        } catch (Exception exception) {
            LOGGER.error("Execution failed for scenario '{}'", name, exception);
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to launch scenario '" + name + "': " + exception.getMessage());
        }
        var referer = request.getHeader("Referer");
        return "redirect:" + (referer != null && !referer.isBlank() ? referer : "/alerts");
    }
}
