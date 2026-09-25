package io.github.sekelenao.adtech.model.util;

import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CurrenciesTest {

    @Test
    @DisplayName("Should convert currency amounts to micros accurately")
    void testToMicros() {
        assertEquals(2_000L, Currencies.toMicros(0.002));
        assertEquals(450_000L, Currencies.toMicros(0.45));
        assertEquals(120_000_000L, Currencies.toMicros(120.00));
        assertEquals(1_000_000_000L, Currencies.toMicros(1000.00));
    }

    @Test
    @DisplayName("Should convert micros back to decimal accurately")
    void testToDecimal() {
        assertEquals(0.002, Currencies.toDecimal(2_000L), 1e-9);
        assertEquals(0.45, Currencies.toDecimal(450_000L), 1e-9);
        assertEquals(120.00, Currencies.toDecimal(120_000_000L), 1e-9);
    }

    @Test
    @DisplayName("Should convert BigDecimal currency amounts to micros accurately")
    void testToMicrosBigDecimal() {
        assertEquals(2_000L, Currencies.toMicros(new BigDecimal("0.002")));
        assertEquals(450_000L, Currencies.toMicros(new BigDecimal("0.45")));
        assertEquals(120_000_000L, Currencies.toMicros(new BigDecimal("120.00")));
        assertEquals(1_000_000_000L, Currencies.toMicros(new BigDecimal("1000.00")));
    }

    @Test
    @DisplayName("Should reject null BigDecimal in toMicros")
    void testToMicrosBigDecimalNull() {
        assertThrows(NullPointerException.class, () -> Currencies.toMicros((BigDecimal) null));
    }
}
