package io.github.sekelenao.demo.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.sekelenao.adtech.model.budget.CampaignStatus;

import java.math.BigDecimal;
import java.util.OptionalInt;
import java.util.OptionalLong;

/**
 * Immutable step definition with private final fields, JsonCreator constructor,
 * and OptionalInt / OptionalLong accessors.
 */
public class ScenarioStep {

    private final Action action;

    private final String description;

    private final String campaignId;

    private final String advertiserId;

    private final String advertiserName;

    private final String userId;

    private final BigDecimal allocatedBudget;

    private final BigDecimal newAllocatedBudget;

    private final BigDecimal cost;

    private final BigDecimal orderAmount;

    private final CampaignStatus status;

    private final Integer count;

    private final Long delayBetweenMs;

    private final Long delayAfterMs;

    @JsonCreator
    public ScenarioStep(
        @JsonProperty("action") Action action,
        @JsonProperty("description") String description,
        @JsonProperty("campaignId") String campaignId,
        @JsonProperty("advertiserId") String advertiserId,
        @JsonProperty("advertiserName") String advertiserName,
        @JsonProperty("userId") String userId,
        @JsonProperty("allocatedBudget") BigDecimal allocatedBudget,
        @JsonProperty("newAllocatedBudget") BigDecimal newAllocatedBudget,
        @JsonProperty("cost") BigDecimal cost,
        @JsonProperty("orderAmount") BigDecimal orderAmount,
        @JsonProperty("status") CampaignStatus status,
        @JsonProperty("count") Integer count,
        @JsonProperty("delayBetweenMs") Long delayBetweenMs,
        @JsonProperty("delayAfterMs") Long delayAfterMs
    ) {
        this.action = action;
        this.description = description;
        this.campaignId = campaignId;
        this.advertiserId = advertiserId;
        this.advertiserName = advertiserName;
        this.userId = userId;
        this.allocatedBudget = allocatedBudget;
        this.newAllocatedBudget = newAllocatedBudget;
        this.cost = cost;
        this.orderAmount = orderAmount;
        this.status = status;
        this.count = count;
        this.delayBetweenMs = delayBetweenMs;
        this.delayAfterMs = delayAfterMs;
    }

    public Action action() {
        return action;
    }

    public String description() {
        return description;
    }

    public String campaignId() {
        return campaignId;
    }

    public String advertiserId() {
        return advertiserId;
    }

    public String advertiserName() {
        return advertiserName;
    }

    public String userId() {
        return userId;
    }

    public BigDecimal allocatedBudget() {
        return allocatedBudget;
    }

    public BigDecimal newAllocatedBudget() {
        return newAllocatedBudget;
    }

    public BigDecimal cost() {
        return cost;
    }

    public BigDecimal orderAmount() {
        return orderAmount;
    }

    public CampaignStatus status() {
        return status;
    }

    public OptionalInt count() {
        if (count == null) {
            return OptionalInt.empty();
        }
        return OptionalInt.of(count);
    }

    public OptionalLong delayBetweenMs() {
        if (delayBetweenMs == null) {
            return OptionalLong.empty();
        }
        return OptionalLong.of(delayBetweenMs);
    }

    public OptionalLong delayAfterMs() {
        if (delayAfterMs == null) {
            return OptionalLong.empty();
        }
        return OptionalLong.of(delayAfterMs);
    }
}
