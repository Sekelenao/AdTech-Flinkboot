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
        Objects.requireNonNull(amount, "amount must not be null");
        return amount.multiply(BigDecimal.valueOf(MICROS_PER_UNIT)).longValue();
    }

    public static long toMicros(double amount) {
        return Math.round(amount * MICROS_PER_UNIT);
    }

    public static double toDecimal(long micros) {
        return (double) micros / MICROS_PER_UNIT;
    }
}
