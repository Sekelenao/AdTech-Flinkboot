package io.github.sekelenao.demo.fluss;

import io.github.sekelenao.demo.exception.FlussException;
import io.github.sekelenao.demo.properties.AdtechProperties;
import org.apache.fluss.client.lookup.Lookuper;
import org.apache.fluss.row.BinaryString;
import org.apache.fluss.row.GenericRow;
import org.apache.fluss.row.InternalRow;

import java.time.Duration;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * Live implementation of FlussClient interacting with a running Apache Fluss engine.
 */
public final class LiveFlussClient implements FlussClient {

    private final Lookuper lookuper;

    private final Duration timeout;

    public LiveFlussClient(Lookuper lookuper, AdtechProperties properties) {
        this.lookuper = Objects.requireNonNull(lookuper);
        this.timeout = Objects.requireNonNull(properties).fluss().pollTimeout();
    }

    @Override
    public <T> Optional<T> lookup(String key, Function<InternalRow, T> mapper) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(mapper);
        try {
            var row = GenericRow.of(BinaryString.fromString(key));
            var result = lookuper.lookup(row).get(timeout.toMillis(), TimeUnit.MILLISECONDS);
            return Optional.ofNullable(result.getSingletonRow()).map(mapper);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new FlussException("Lookup interrupted in Fluss for key '" + key + "'", exception);
        } catch (Exception exception) {
            throw new FlussException("Lookup failed in Fluss for key '" + key + "'", exception);
        }
    }
}
