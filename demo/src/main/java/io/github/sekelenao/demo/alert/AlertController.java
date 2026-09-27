package io.github.sekelenao.demo.alert;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Objects;

/**
 * Controller handling real-time budget alert views.
 */
@Controller
public class AlertController {

    private final AlertService alertService;

    public AlertController(AlertService alertService) {
        this.alertService = Objects.requireNonNull(alertService);
    }

    @GetMapping("/")
    public String root() {
        return "redirect:/alerts";
    }

    @GetMapping("/alerts")
    public String alerts(Model model) {
        Objects.requireNonNull(model);
        model.addAttribute("alerts", alertService.all());
        model.addAttribute("activeMenu", "ALERTS");
        return "index";
    }
}
