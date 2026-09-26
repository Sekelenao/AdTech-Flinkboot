package io.github.sekelenao.demo.fluss;

import org.apache.fluss.row.InternalRow;

import java.util.Optional;
import java.util.function.Function;

/**
 * Generic point-lookup client interacting with Apache Fluss storage engine.
 */
public interface FlussClient {

    <T> Optional<T> lookup(String key, Function<InternalRow, T> mapper);
}
