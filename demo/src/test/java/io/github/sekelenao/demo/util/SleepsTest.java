package io.github.sekelenao.demo.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SleepsTest {

    @Test
    @DisplayName("Should return immediately for zero or negative durations")
    void shouldReturnImmediatelyForZeroOrNegative() {
        assertDoesNotThrow(() -> Sleeps.sleep(0));
        assertDoesNotThrow(() -> Sleeps.sleep(-100));
        assertDoesNotThrow(() -> Sleeps.sleep(Duration.ZERO));
        assertDoesNotThrow(() -> Sleeps.sleep(Duration.ofMillis(-50)));
    }

    @Test
    @DisplayName("Should sleep for positive duration without error")
    void shouldSleepForPositiveDuration() {
        assertDoesNotThrow(() -> Sleeps.sleep(5));
        assertDoesNotThrow(() -> Sleeps.sleep(Duration.ofMillis(5)));
    }

    @Test
    @DisplayName("Should throw NullPointerException when Duration is null")
    void shouldThrowWhenDurationIsNull() {
        assertThrows(NullPointerException.class, () -> Sleeps.sleep((Duration) null));
    }
}
