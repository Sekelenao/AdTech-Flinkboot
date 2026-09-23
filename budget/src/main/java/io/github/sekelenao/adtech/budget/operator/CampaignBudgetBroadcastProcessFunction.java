package io.github.sekelenao.adtech.budget.operator;

import io.github.sekelenao.adtech.budget.live.LiveCampaignBudgetEnrichers;
import io.github.sekelenao.adtech.model.budget.BudgetExhaustedAlert;
import io.github.sekelenao.adtech.model.budget.CampaignBudget;
import io.github.sekelenao.adtech.model.budget.CampaignLiveBudget;
import io.github.sekelenao.adtech.model.budget.CampaignSpendEvent;
import io.github.sekelenao.adtech.model.budget.CampaignStatus;
import org.apache.flink.api.common.functions.OpenContext;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.streaming.api.functions.co.KeyedBroadcastProcessFunction;
import org.apache.flink.util.Collector;
import org.apache.flink.util.OutputTag;

/**
 * Keyed broadcast operator maintaining real-time campaign spend against dynamically broadcasted campaign budgets.
 *
 * <p>State:
 * <ul>
 *   <li>{@link #CAMPAIGN_BUDGET_STATE_DESCRIPTOR}: Replicated broadcast state holding the latest
 *       {@link CampaignBudget} per campaign ID.</li>
 *   <li>Keyed state: {@link ValueState} of {@link CampaignLiveBudget} holding cumulative spend and metrics.</li>
 * </ul>
 *
 * <p>Outputs:
 * <ul>
 *   <li>Main output: Updated {@link CampaignLiveBudget} emitted on each spend event for Fluss O(1) upsert.</li>
 *   <li>Side output: {@link BudgetExhaustedAlert} emitted via {@link #BUDGET_EXHAUSTED_TAG}
 *       when spend reaches or exceeds the allocated budget.</li>
 * </ul>
 */
public class CampaignBudgetBroadcastProcessFunction extends KeyedBroadcastProcessFunction<String, CampaignSpendEvent, CampaignBudget, CampaignLiveBudget> {

    private static final long serialVersionUID = 1L;

    public static final OutputTag<BudgetExhaustedAlert> BUDGET_EXHAUSTED_TAG =
        new OutputTag<>("budget-exhausted-alerts") {};

    public static final MapStateDescriptor<String, CampaignBudget> CAMPAIGN_BUDGET_STATE_DESCRIPTOR =
            new MapStateDescriptor<>("campaign-budget-broadcast-state", String.class, CampaignBudget.class);

    private transient ValueState<CampaignLiveBudget> budgetState;

    @Override
    public void open(OpenContext openContext) {
        var descriptor = new ValueStateDescriptor<>("budget-state", CampaignLiveBudget.class);
        this.budgetState = getRuntimeContext().getState(descriptor);
    }

    @Override
    public void processBroadcastElement(CampaignBudget campaignBudget, Context ctx, Collector<CampaignLiveBudget> out) throws Exception {
        ctx.getBroadcastState(CAMPAIGN_BUDGET_STATE_DESCRIPTOR).put(campaignBudget.campaignId, campaignBudget);
    }

    @Override
    public void processElement(CampaignSpendEvent spend, ReadOnlyContext ctx, Collector<CampaignLiveBudget> out) throws Exception {
        var liveBudget = budgetState.value();
        if (liveBudget == null) {
            liveBudget = new CampaignLiveBudget();
        }
        var campaignBudget = ctx.getBroadcastState(CAMPAIGN_BUDGET_STATE_DESCRIPTOR).get(spend.campaignId);
        liveBudget = LiveCampaignBudgetEnrichers.enrich(liveBudget, campaignBudget);

        var wasCapped = liveBudget.status == CampaignStatus.CAPPED;
        liveBudget = LiveCampaignBudgetEnrichers.enrich(liveBudget, spend);
        if (!wasCapped && liveBudget.status == CampaignStatus.CAPPED && liveBudget.allocatedBudget > 0) {
            ctx.output(BUDGET_EXHAUSTED_TAG, BudgetExhaustedAlert.from(liveBudget));
        }
        budgetState.update(liveBudget);
        out.collect(liveBudget);
    }

}
