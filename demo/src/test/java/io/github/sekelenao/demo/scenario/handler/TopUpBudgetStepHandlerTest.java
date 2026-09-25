package io.github.sekelenao.demo.scenario.handler;

import io.github.sekelenao.adtech.model.budget.CampaignBudget;
import io.github.sekelenao.adtech.model.budget.CampaignStatus;
import io.github.sekelenao.demo.kafka.BudgetPublisher;
import io.github.sekelenao.demo.model.Action;
import io.github.sekelenao.demo.model.ScenarioStep;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TopUpBudgetStepHandlerTest {

    @Mock
    private BudgetPublisher budgetPublisher;

    private TopUpBudgetStepHandler handler;

    @BeforeEach
    void setUp() {
        handler = new TopUpBudgetStepHandler(budgetPublisher);
    }

    @Test
    @DisplayName("Should return TOP_UP_BUDGET action")
    void shouldReturnTopUpBudgetAction() {
        assertEquals(Action.TOP_UP_BUDGET, handler.action());
    }

    @Test
    @DisplayName("Should publish top up budget with ACTIVE status")
    void shouldPublishTopUpBudgetWithActiveStatus() {
        ScenarioStep step = new ScenarioStep(
            Action.TOP_UP_BUDGET,
            "Uber top up budget",
            "cmp-uber-rides",
            "adv-uber",
            "Uber",
            null,
            null,
            BigDecimal.valueOf(25),
            null,
            null,
            null,
            0,
            0,
            1000
        );

        handler.handle(step);

        ArgumentCaptor<CampaignBudget> captor = ArgumentCaptor.forClass(CampaignBudget.class);
        verify(budgetPublisher).publish(captor.capture());

        CampaignBudget published = captor.getValue();
        assertEquals("cmp-uber-rides", published.campaignId);
        assertEquals("adv-uber", published.advertiserId);
        assertEquals(25_000_000L, published.allocatedBudget);
        assertEquals(CampaignStatus.ACTIVE, published.status);
    }
}
