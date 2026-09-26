package io.github.sekelenao.demo.fluss;

import org.apache.fluss.row.InternalRow;

import java.util.Optional;
import java.util.function.Function;

/**
 * No-op implementation of FlussClient when running outside the demo profile.
 */
public final class NoOpFlussClient implements FlussClient {

    @Override
    public <T> Optional<T> lookup(String key, Function<InternalRow, T> mapper) {
        return Optional.empty();
    }
}
