package io.github.sekelenao.adtech.model.event;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.github.sekelenao.flinkboot.test.api.assertion.FlinkbootAssertions.assertThat;

class AdClickTest {

    @Test
    @DisplayName("AdClick must comply with Flink POJO serialization rules")
    void shouldComplyWithPojoRules() {
        assertThat(AdClick.class).isPojo();
    }
}
