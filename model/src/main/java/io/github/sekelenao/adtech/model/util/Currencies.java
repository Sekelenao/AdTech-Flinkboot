package io.github.sekelenao.adtech.model.util;

/**
 * Utility methods and constants for handling micro-currency amounts (1 unit = 1,000,000 micros).
 */
public final class Currencies {

    public static final long MICROS_PER_UNIT = 1_000_000L;

    private Currencies() {}

    public static long toMicros(double amount) {
        return Math.round(amount * MICROS_PER_UNIT);
    }

    public static double toDecimal(long micros) {
        return (double) micros / MICROS_PER_UNIT;
    }
}
