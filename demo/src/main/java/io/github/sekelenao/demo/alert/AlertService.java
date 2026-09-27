package io.github.sekelenao.demo.alert;

import io.github.sekelenao.adtech.model.budget.BudgetExhaustedAlert;
import io.github.sekelenao.adtech.model.budget.CampaignStatus;
import io.github.sekelenao.demo.budget.LiveBudgetService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class AlertService {

    private final LiveBudgetService liveBudgetService;

    public AlertService(LiveBudgetService liveBudgetService) {
        this.liveBudgetService = Objects.requireNonNull(liveBudgetService);
    }

    public List<BudgetExhaustedAlert> all() {
        return liveBudgetService.findByStatus(CampaignStatus.CAPPED).stream()
            .map(BudgetExhaustedAlert::from)
            .toList();
    }
}
