package io.github.sekelenao.adtech.budget.serde;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;

import java.io.IOException;

/**
 * Generic JSON deserialization schema using Jackson for Flink data streams.
 */
public class JsonDeserializer<T> implements DeserializationSchema<T> {

    private static final long serialVersionUID = 1L;

    private final Class<T> targetType;

    private transient ObjectMapper objectMapper;

    public JsonDeserializer(Class<T> targetType) {
        this.targetType = targetType;
    }

    @Override
    public void open(InitializationContext context) {
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public T deserialize(byte[] message) throws IOException {
        return objectMapper.readValue(message, targetType);
    }

    @Override
    public boolean isEndOfStream(T nextElement) {
        return false;
    }

    @Override
    public TypeInformation<T> getProducedType() {
        return TypeInformation.of(targetType);
    }
}
