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
class CampaignStepHandlerTest {

    @Mock
    private BudgetPublisher budgetPublisher;

    private CampaignStepHandler handler;

    @BeforeEach
    void setUp() {
        handler = new CampaignStepHandler(budgetPublisher);
    }

    @Test
    @DisplayName("Should return CAMPAIGN action")
    void shouldReturnCampaignAction() {
        assertEquals(Action.CAMPAIGN, handler.action());
    }

    @Test
    @DisplayName("Should publish converted campaign budget")
    void shouldPublishConvertedCampaignBudget() {
        ScenarioStep step = new ScenarioStep(
            Action.CAMPAIGN,
            "Register Google Pixel 9",
            "cmp-google-pixel9",
            "adv-google",
            "Google",
            null,
            BigDecimal.valueOf(100),
            null,
            null,
            null,
            CampaignStatus.ACTIVE,
            0,
            0,
            500
        );

        handler.handle(step);

        ArgumentCaptor<CampaignBudget> captor = ArgumentCaptor.forClass(CampaignBudget.class);
        verify(budgetPublisher).publish(captor.capture());

        CampaignBudget published = captor.getValue();
        assertEquals("cmp-google-pixel9", published.campaignId);
        assertEquals("adv-google", published.advertiserId);
        assertEquals(100_000_000L, published.allocatedBudget);
        assertEquals(CampaignStatus.ACTIVE, published.status);
    }
}
