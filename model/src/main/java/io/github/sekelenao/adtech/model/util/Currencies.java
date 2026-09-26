package io.github.sekelenao.adtech.model.util;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Utility methods and constants for handling micro-currency amounts (1 unit = 1,000,000 micros).
 */
public final class Currencies {

    public static final long MICROS_PER_UNIT = 1_000_000L;

    private Currencies() {}

    public static long toMicros(BigDecimal amount) {
        Objects.requireNonNull(amount);
        return amount.multiply(BigDecimal.valueOf(MICROS_PER_UNIT)).longValue();
    }
}
