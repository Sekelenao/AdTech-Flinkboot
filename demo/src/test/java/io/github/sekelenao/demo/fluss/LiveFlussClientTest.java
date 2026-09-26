package io.github.sekelenao.demo.fluss;

import io.github.sekelenao.demo.exception.FlussException;
import io.github.sekelenao.demo.properties.AdtechProperties;
import io.github.sekelenao.demo.properties.part.FlussProperties;
import org.apache.fluss.client.lookup.LookupResult;
import org.apache.fluss.client.lookup.Lookuper;
import org.apache.fluss.row.InternalRow;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LiveFlussClientTest {

    private final Lookuper lookuper = mock(Lookuper.class);

    private final AdtechProperties properties = mock(AdtechProperties.class);

    private LiveFlussClient client;

    @BeforeEach
    void setUp() {
        var flussProperties = new FlussProperties(
                List.of("localhost:9123"),
                "fluss",
                "campaign_live_budgets",
                Duration.ofMillis(100)
        );
        when(properties.fluss()).thenReturn(flussProperties);
        client = new LiveFlussClient(lookuper, properties);
    }

    @Test
    @DisplayName("Should return mapped value when lookup succeeds")
    void shouldReturnMappedValueOnSuccess() {
        var row = mock(InternalRow.class);
        var result = new LookupResult(row);
        when(lookuper.lookup(any())).thenReturn(CompletableFuture.completedFuture(result));

        var value = client.lookup("camp-1", r -> "mapped-value");

        assertThat(value).contains("mapped-value");
    }

    @Test
    @DisplayName("Should return empty optional when row is not found")
    void shouldReturnEmptyOptionalWhenNotFound() {
        var result = new LookupResult((InternalRow) null);
        when(lookuper.lookup(any())).thenReturn(CompletableFuture.completedFuture(result));

        var value = client.lookup("camp-1", r -> "mapped-value");

        assertThat(value).isEmpty();
    }

    @Test
    @DisplayName("Should throw FlussException preserving cause when future fails")
    void shouldThrowFlussExceptionOnExecutionException() {
        var future = new CompletableFuture<LookupResult>();
        future.completeExceptionally(new RuntimeException("Connection reset"));
        when(lookuper.lookup(any())).thenReturn(future);

        assertThatThrownBy(() -> client.lookup("camp-1", r -> "mapped"))
                .isInstanceOf(FlussException.class)
                .hasCauseInstanceOf(ExecutionException.class);
    }

    @Test
    @DisplayName("Should throw FlussException and restore thread interrupt when interrupted")
    void shouldThrowFlussExceptionAndRestoreInterrupt() throws Exception {
        var future = mock(CompletableFuture.class);
        when(future.get(any(Long.class), any(TimeUnit.class))).thenThrow(new InterruptedException("Interrupted"));
        when(lookuper.lookup(any())).thenReturn(future);

        assertThatThrownBy(() -> client.lookup("camp-1", r -> "mapped"))
                .isInstanceOf(FlussException.class)
                .hasCauseInstanceOf(InterruptedException.class);

        assertThat(Thread.currentThread().isInterrupted()).isTrue();
        Thread.interrupted();
    }

    @Test
    @DisplayName("Should throw FlussException on timeout")
    void shouldThrowFlussExceptionOnTimeout() throws Exception {
        var future = mock(CompletableFuture.class);
        when(future.get(any(Long.class), any(TimeUnit.class))).thenThrow(new TimeoutException("Timed out"));
        when(lookuper.lookup(any())).thenReturn(future);

        assertThatThrownBy(() -> client.lookup("camp-1", r -> "mapped"))
                .isInstanceOf(FlussException.class)
                .hasCauseInstanceOf(TimeoutException.class);
    }
}
