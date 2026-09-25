package io.github.sekelenao.demo.model;

import io.github.sekelenao.adtech.model.budget.CampaignStatus;

import java.math.BigDecimal;

/**
 * Immutable record representing a single actionable step inside a scenario.
 * Financial amounts use {@link BigDecimal} for exact decimal precision.
 */
public record ScenarioStep(
    Action action,
    String description,
    String campaignId,
    String advertiserId,
    String advertiserName,
    String userId,
    BigDecimal allocatedBudgetEur,
    BigDecimal newAllocatedBudgetEur,
    BigDecimal costEur,
    BigDecimal orderAmountEur,
    CampaignStatus status,
    int count,
    long delayBetweenMs,
    long delayAfterMs
) {}
