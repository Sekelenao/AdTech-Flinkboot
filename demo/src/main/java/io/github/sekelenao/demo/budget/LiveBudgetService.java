package io.github.sekelenao.demo.budget;

import io.github.sekelenao.adtech.model.budget.CampaignLiveBudget;
import io.github.sekelenao.adtech.model.budget.CampaignStatus;
import io.github.sekelenao.demo.contract.ContractService;
import io.github.sekelenao.demo.fluss.FlussClient;
import io.github.sekelenao.demo.fluss.InternalRows;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class LiveBudgetService {

    private final FlussClient flussClient;

    private final ContractService contractService;

    public LiveBudgetService(FlussClient flussClient, ContractService contractService) {
        this.flussClient = Objects.requireNonNull(flussClient);
        this.contractService = Objects.requireNonNull(contractService);
    }

    public Optional<CampaignLiveBudget> find(String campaignId) {
        Objects.requireNonNull(campaignId);
        return flussClient.lookup(campaignId, InternalRows::mapToCampaignLiveBudget);
    }

    public List<CampaignLiveBudget> findByStatus(CampaignStatus status) {
        Objects.requireNonNull(status);
        return contractService.all().stream()
            .map(this::find)
            .flatMap(Optional::stream)
            .filter(campaign -> campaign.status == status)
            .toList();
    }
}
