package io.github.sekelenao.demo.kafka;

import io.github.sekelenao.adtech.model.budget.CampaignBudget;
import io.github.sekelenao.demo.properties.AdtechProperties;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class BudgetPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private final String topic;

    public BudgetPublisher(KafkaTemplate<String, Object> kafkaTemplate, AdtechProperties properties) {
        this.kafkaTemplate = Objects.requireNonNull(kafkaTemplate);
        this.topic = Objects.requireNonNull(properties).kafka().topics().campaign().budgets();
    }

    public void publish(CampaignBudget budget) {
        Objects.requireNonNull(budget);
        kafkaTemplate.send(topic, budget.campaignId, budget);
    }
}
