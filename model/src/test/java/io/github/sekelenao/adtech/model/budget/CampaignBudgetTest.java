package io.github.sekelenao.adtech.model.budget;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.github.sekelenao.flinkboot.test.api.assertion.FlinkbootAssertions.assertThat;

class CampaignBudgetTest {

    @Test
    @DisplayName("CampaignBudget must comply with Flink POJO serialization rules")
    void shouldComplyWithPojoRules() {
        assertThat(CampaignBudget.class).isPojo();
    }
}
