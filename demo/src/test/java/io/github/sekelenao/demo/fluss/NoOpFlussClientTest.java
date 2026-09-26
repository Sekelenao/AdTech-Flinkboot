package io.github.sekelenao.demo.fluss;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class NoOpFlussClientTest {

    private final FlussClient client = new NoOpFlussClient();

    @Test
    @DisplayName("Should return empty optional when looking up any key")
    void shouldReturnEmptyOptionalOnLookup() {
        var result = client.lookup("campaign-123", row -> row.getString(0));

        assertThat(result).isEmpty();
    }
}
