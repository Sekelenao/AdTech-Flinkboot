package io.github.sekelenao.demo.scenario;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Objects;

/**
 * Controller handling user interactions for listing, viewing, and running demo scenarios.
 */
@Controller
public class ScenarioController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScenarioController.class);

    private final ScenarioService scenarioService;

    public ScenarioController(ScenarioService scenarioService) {
        this.scenarioService = Objects.requireNonNull(scenarioService);
    }

    @GetMapping("/")
    public String index(Model model) {
        Objects.requireNonNull(model);
        model.addAttribute("scenarios", scenarioService.listScenarios());
        model.addAttribute("contracts", scenarioService.listContracts());
        model.addAttribute("isRunning", scenarioService.isRunning());
        return "index";
    }

    @PostMapping("/scenarios/run")
    public String runScenarioFromForm(@RequestParam("name") String name, RedirectAttributes redirectAttributes) {
        return runScenario(name, redirectAttributes);
    }

    @PostMapping("/scenarios/{name}/run")
    public String runScenario(@PathVariable("name") String name, RedirectAttributes redirectAttributes) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(redirectAttributes);
        try {
            LOGGER.info("User requested execution of scenario '{}'", name);
            scenarioService.runScenario(name);
            redirectAttributes.addFlashAttribute("successMessage", "Scenario '" + name + "' launched successfully!");
        } catch (Exception exception) {
            LOGGER.error("Execution failed for scenario '{}'", name, exception);
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to launch scenario '" + name + "': " + exception.getMessage());
        }
        return "redirect:/";
    }
}
