package io.github.sekelenao.adtech.budget.configuration;

import io.github.sekelenao.flinkboot.core.api.Flinkboot;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class BudgetJobConfigurationTest {

    @Test
    @DisplayName("Should load and validate budget job configuration from classpath YAML")
    void shouldLoadAndValidateConfiguration() throws Exception {
        var config = Flinkboot.initialize().configuration(BudgetJobConfiguration.class);
        assertAll(
                () -> assertNotNull(config),
                () -> assertEquals("adtech-budget-pacing-job", config.job().name()),
                () -> assertEquals("campaign-budget-source", config.campaignBudgetSource().name()),
                () -> assertEquals("ad-impressions-source", config.impressionsSource().name()),
                () -> assertEquals("ad-clicks-source", config.clicksSource().name()),
                () -> assertEquals("budget-alerts-sink", config.alertsSink().name()),
                () -> assertEquals("campaign-live-budgets-sink", config.flussSink().name())
        );
    }
}
