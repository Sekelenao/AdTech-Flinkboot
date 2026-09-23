package io.github.sekelenao.adtech.attribution.serde;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.api.common.serialization.SerializationSchema;

/**
 * Generic JSON serialization schema using Jackson for Flink data sinks.
 */
public class JsonSerializer<T> implements SerializationSchema<T> {

    private static final long serialVersionUID = 1L;

    private transient ObjectMapper objectMapper;

    @Override
    public void open(InitializationContext context) {
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public byte[] serialize(T element) {
        try {
            return objectMapper.writeValueAsBytes(element);
        } catch (JsonProcessingException exception) {
            throw new RuntimeException("Failed to serialize element to JSON: " + element, exception);
        }
    }
}
