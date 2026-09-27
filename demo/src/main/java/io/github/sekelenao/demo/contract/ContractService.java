package io.github.sekelenao.demo.contract;

import io.github.sekelenao.demo.model.ScenarioStep;
import io.github.sekelenao.demo.scenario.ScenarioService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ContractService {

    private final ScenarioService scenarioService;

    public ContractService(ScenarioService scenarioService) {
        this.scenarioService = Objects.requireNonNull(scenarioService);
    }

    @Cacheable("contracts")
    public Set<String> all() {
        return scenarioService.all().stream()
            .flatMap(scenario -> scenario.steps().stream())
            .map(ScenarioStep::campaignId)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());
    }
}
