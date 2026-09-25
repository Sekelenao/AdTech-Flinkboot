package io.github.sekelenao.demo.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Objects;

/**
 * Utility methods for sleeping threads safely with interrupted status preservation.
 */
public final class Sleeps {

    private static final Logger log = LoggerFactory.getLogger(Sleeps.class);

    private Sleeps() {
        throw new AssertionError("Utility class");
    }

    public static void sleep(long millis) {
        if (millis <= 0) {
            return;
        }

        try {
            Thread.sleep(millis);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            log.warn("Sleep of {} ms was interrupted", millis, exception);
        }
    }

    public static void sleep(Duration duration) {
        Objects.requireNonNull(duration);
        sleep(duration.toMillis());
    }
}
