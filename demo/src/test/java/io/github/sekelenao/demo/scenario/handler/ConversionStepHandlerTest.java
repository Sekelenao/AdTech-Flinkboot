package io.github.sekelenao.demo.scenario.handler;

import io.github.sekelenao.adtech.model.event.Conversion;
import io.github.sekelenao.demo.kafka.ConversionPublisher;
import io.github.sekelenao.demo.model.Action;
import io.github.sekelenao.demo.model.ScenarioStep;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ConversionStepHandlerTest {

    @Mock
    private ConversionPublisher conversionPublisher;

    private ConversionStepHandler handler;

    @BeforeEach
    void setUp() {
        handler = new ConversionStepHandler(conversionPublisher);
    }

    @Test
    @DisplayName("Should return CONVERSION action")
    void shouldReturnConversionAction() {
        assertEquals(Action.CONVERSION, handler.action());
    }

    @Test
    @DisplayName("Should publish conversion event")
    void shouldPublishConversionEvent() {
        ScenarioStep step = new ScenarioStep(
            Action.CONVERSION,
            "User Alice completes order",
            null,
            "adv-google",
            "Google",
            "usr-alice",
            null,
            null,
            null,
            BigDecimal.valueOf(120),
            null,
            0,
            0,
            1500
        );

        handler.handle(step);

        ArgumentCaptor<Conversion> captor = ArgumentCaptor.forClass(Conversion.class);
        verify(conversionPublisher).publish(captor.capture());

        Conversion published = captor.getValue();
        assertEquals("adv-google", published.advertiserId);
        assertEquals("usr-alice", published.userId);
        assertEquals(120_000_000L, published.orderAmount);
        assertNotNull(published.conversionId);
    }
}
