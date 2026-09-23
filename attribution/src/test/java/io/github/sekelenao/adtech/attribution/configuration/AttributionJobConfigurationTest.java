package io.github.sekelenao.adtech.attribution.configuration;

import io.github.sekelenao.flinkboot.core.api.Flinkboot;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class AttributionJobConfigurationTest {

    @Test
    @DisplayName("Should load and validate attribution job configuration from classpath YAML")
    void shouldLoadAndValidateConfiguration() throws Exception {
        var config = Flinkboot.initialize().configuration(AttributionJobConfiguration.class);
        assertAll(
                () -> assertNotNull(config),
                () -> assertEquals("adtech-last-click-attribution-job", config.job().name()),
                () -> assertEquals(Duration.ofMinutes(30), config.attribution().window()),
                () -> assertEquals("ad-clicks-source", config.clicksSource().name()),
                () -> assertEquals("conversions-source", config.conversionsSource().name()),
                () -> assertEquals("attributed-conversions-sink", config.attributedConversionsSink().name())
        );
    }
}
