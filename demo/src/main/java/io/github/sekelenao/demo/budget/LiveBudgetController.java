package io.github.sekelenao.demo.budget;

import io.github.sekelenao.adtech.model.budget.CampaignStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Objects;

/**
 * Controller handling real-time campaign budget views and search.
 */
@Controller
public class LiveBudgetController {

    private final LiveBudgetService liveBudgetService;

    public LiveBudgetController(LiveBudgetService liveBudgetService) {
        this.liveBudgetService = Objects.requireNonNull(liveBudgetService);
    }

    @GetMapping("/campaigns/status/{status}")
    public String byStatus(@PathVariable("status") CampaignStatus status, Model model) {
        Objects.requireNonNull(status);
        Objects.requireNonNull(model);
        model.addAttribute("campaigns", liveBudgetService.findByStatus(status));
        model.addAttribute("currentStatus", status);
        model.addAttribute("activeMenu", status.name());
        return "index";
    }

    @GetMapping("/campaigns/{id}")
    public String byId(@PathVariable("id") String id, Model model) {
        Objects.requireNonNull(id);
        Objects.requireNonNull(model);
        var campaign = liveBudgetService.find(id);
        model.addAttribute("campaigns", campaign.map(List::of).orElseGet(List::of));
        model.addAttribute("campaignId", id);
        model.addAttribute("activeMenu", "SEARCH");
        return "index";
    }

    @GetMapping("/campaigns/search")
    public String search(@RequestParam("campaignId") String campaignId) {
        Objects.requireNonNull(campaignId);
        return "redirect:/campaigns/" + campaignId.trim();
    }
}
