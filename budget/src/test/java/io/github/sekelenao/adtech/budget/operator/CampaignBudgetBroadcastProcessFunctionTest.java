package io.github.sekelenao.adtech.budget.operator;

import io.github.sekelenao.adtech.model.budget.BudgetExhaustedAlert;
import io.github.sekelenao.adtech.model.budget.CampaignBudget;
import io.github.sekelenao.adtech.model.budget.CampaignLiveBudget;
import io.github.sekelenao.adtech.model.budget.CampaignSpendEvent;
import io.github.sekelenao.adtech.model.budget.CampaignStatus;
import io.github.sekelenao.adtech.model.budget.SpendType;
import io.github.sekelenao.flinkboot.test.api.sink.CollectingSink;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CampaignBudgetBroadcastProcessFunctionTest {

    @Test
    @DisplayName("Scenario 1: Nominal consumption under budget (ACTIVE, no alerts)")
    void shouldTrackSpendUnderBudget() throws Exception {
        var env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        var campaignBudget = new CampaignBudget();
        campaignBudget.campaignId = "camp-1";
        campaignBudget.advertiserId = "adv-1";
        campaignBudget.allocatedBudget = 10_000_000L; // 10.00 EUR
        campaignBudget.status = CampaignStatus.ACTIVE;
        campaignBudget.updatedAt = 1000L;

        var impression = new CampaignSpendEvent();
        impression.timestamp = 2000L;
        impression.eventId = "imp-1";
        impression.campaignId = "camp-1";
        impression.advertiserId = "adv-1";
        impression.cost = 2_000_000L; // 2.00 EUR
        impression.type = SpendType.IMPRESSION;

        var click = new CampaignSpendEvent();
        click.timestamp = 3000L;
        click.eventId = "clk-1";
        click.campaignId = "camp-1";
        click.advertiserId = "adv-1";
        click.cost = 2_000_000L; // 2.00 EUR
        click.type = SpendType.CLICK;

        try (
            var mainSink = new CollectingSink<CampaignLiveBudget>();
            var alertSink = new CollectingSink<BudgetExhaustedAlert>()
        ) {

            var budgetBroadcastStream = env.fromData(campaignBudget)
                .broadcast(CampaignBudgetBroadcastProcessFunction.CAMPAIGN_BUDGET_STATE_DESCRIPTOR);

            var spendStream = env.fromData(impression, click)
                .map(spend -> {
                    Thread.sleep(50);
                    return spend;
                })
                .returns(CampaignSpendEvent.class)
                .keyBy(spend -> spend.campaignId);

            var budgetStream = spendStream.connect(budgetBroadcastStream)
                .process(new CampaignBudgetBroadcastProcessFunction());

            budgetStream.sinkTo(mainSink);
            budgetStream.getSideOutput(CampaignBudgetBroadcastProcessFunction.BUDGET_EXHAUSTED_TAG).sinkTo(alertSink);

            env.execute();

            var budgets = mainSink.elements();
            var alerts = alertSink.elements();

            assertEquals(2, budgets.size());
            var latestBudget = budgets.get(budgets.size() - 1);

            assertAll(
                () -> assertEquals("camp-1", latestBudget.campaignId),
                () -> assertEquals(CampaignStatus.ACTIVE, latestBudget.status),
                () -> assertEquals(10_000_000L, latestBudget.allocatedBudget),
                () -> assertEquals(4_000_000L, latestBudget.spentBudget),
                () -> assertEquals(6_000_000L, latestBudget.remainingBudget),
                () -> assertEquals(1L, latestBudget.impressionCount),
                () -> assertEquals(1L, latestBudget.clickCount),
                () -> assertTrue(alerts.isEmpty(), "No alert should be emitted when spend is under budget")
            );
        }
    }

    @Test
    @DisplayName("Scenario 2: Spend exceeds budget, switches to CAPPED, and emits BudgetExhaustedAlert")
    void shouldTriggerCappedAndEmitAlertWhenBudgetExhausted() throws Exception {
        var env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        var campaignBudget = new CampaignBudget();
        campaignBudget.campaignId = "camp-2";
        campaignBudget.advertiserId = "adv-2";
        campaignBudget.allocatedBudget = 5_000_000L; // 5.00 EUR
        campaignBudget.status = CampaignStatus.ACTIVE;
        campaignBudget.updatedAt = 1000L;

        var impression = new CampaignSpendEvent();
        impression.timestamp = 2000L;
        impression.eventId = "imp-2";
        impression.campaignId = "camp-2";
        impression.advertiserId = "adv-2";
        impression.cost = 4_200_000L; // 4.20 EUR
        impression.type = SpendType.IMPRESSION;

        var click = new CampaignSpendEvent();
        click.timestamp = 3000L;
        click.eventId = "clk-2";
        click.campaignId = "camp-2";
        click.advertiserId = "adv-2";
        click.cost = 1_000_000L; // 1.00 EUR (total spend: 5.20 EUR > 5.00 EUR)
        click.type = SpendType.CLICK;

        try (
            var mainSink = new CollectingSink<CampaignLiveBudget>();
            var alertSink = new CollectingSink<BudgetExhaustedAlert>()
        ) {

            var budgetBroadcastStream = env.fromData(campaignBudget)
                .broadcast(CampaignBudgetBroadcastProcessFunction.CAMPAIGN_BUDGET_STATE_DESCRIPTOR);

            var spendStream = env.fromData(impression, click)
                .map(spend -> {
                    Thread.sleep(50);
                    return spend;
                })
                .returns(CampaignSpendEvent.class)
                .keyBy(spend -> spend.campaignId);

            var budgetStream = spendStream.connect(budgetBroadcastStream)
                .process(new CampaignBudgetBroadcastProcessFunction());

            budgetStream.sinkTo(mainSink);
            budgetStream.getSideOutput(CampaignBudgetBroadcastProcessFunction.BUDGET_EXHAUSTED_TAG).sinkTo(alertSink);

            env.execute();

            var budgets = mainSink.elements();
            var alerts = alertSink.elements();

            assertEquals(2, budgets.size());
            var latestBudget = budgets.get(budgets.size() - 1);

            assertAll(
                () -> assertEquals("camp-2", latestBudget.campaignId),
                () -> assertEquals(CampaignStatus.CAPPED, latestBudget.status),
                () -> assertEquals(5_000_000L, latestBudget.allocatedBudget),
                () -> assertEquals(5_200_000L, latestBudget.spentBudget),
                () -> assertEquals(-200_000L, latestBudget.remainingBudget),
                () -> assertEquals(1L, latestBudget.impressionCount),
                () -> assertEquals(1L, latestBudget.clickCount),
                () -> assertEquals(1, alerts.size(), "Exactly one alert must be emitted")
            );

            var alert = alerts.get(0);
            assertAll(
                () -> assertEquals("camp-2", alert.campaignId),
                () -> assertEquals("adv-2", alert.advertiserId),
                () -> assertEquals(5_000_000L, alert.allocatedBudget),
                () -> assertEquals(5_200_000L, alert.spentBudget),
                () -> assertEquals(3000L, alert.exhaustedAt)
            );
        }
    }

    @Test
    @DisplayName("Scenario 3: Spend arrives for unconfigured campaign with no CampaignBudget in Kafka")
    void shouldHandleSpendForUnconfiguredCampaign() throws Exception {
        var env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        var dummyBudget = new CampaignBudget();
        dummyBudget.campaignId = "camp-other";
        dummyBudget.advertiserId = "adv-other";
        dummyBudget.allocatedBudget = 10_000_000L;
        dummyBudget.status = CampaignStatus.ACTIVE;
        dummyBudget.updatedAt = 1000L;

        var unconfiguredSpend = new CampaignSpendEvent();
        unconfiguredSpend.timestamp = 2000L;
        unconfiguredSpend.eventId = "imp-unbudgeted";
        unconfiguredSpend.campaignId = "camp-unconfigured";
        unconfiguredSpend.advertiserId = "adv-unconfigured";
        unconfiguredSpend.cost = 2_000_000L; // 2.00 EUR
        unconfiguredSpend.type = SpendType.IMPRESSION;

        try (
            var mainSink = new CollectingSink<CampaignLiveBudget>();
            var alertSink = new CollectingSink<BudgetExhaustedAlert>()
        ) {
            var budgetBroadcastStream = env.fromData(dummyBudget)
                .broadcast(CampaignBudgetBroadcastProcessFunction.CAMPAIGN_BUDGET_STATE_DESCRIPTOR);

            var spendStream = env.fromData(unconfiguredSpend)
                .map(s -> {
                    Thread.sleep(50);
                    return s;
                })
                .returns(CampaignSpendEvent.class)
                .keyBy(spend -> spend.campaignId);

            var budgetStream = spendStream.connect(budgetBroadcastStream)
                .process(new CampaignBudgetBroadcastProcessFunction());

            budgetStream.sinkTo(mainSink);
            budgetStream.getSideOutput(CampaignBudgetBroadcastProcessFunction.BUDGET_EXHAUSTED_TAG).sinkTo(alertSink);

            env.execute();

            var budgets = mainSink.elements();
            var alerts = alertSink.elements();

            assertEquals(1, budgets.size());

            var result = budgets.get(0);
            assertAll(
                () -> assertEquals("camp-unconfigured", result.campaignId),
                () -> assertEquals(CampaignStatus.PAUSED, result.status, "Unconfigured campaign must be PAUSED"),
                () -> assertEquals(0L, result.allocatedBudget),
                () -> assertEquals(2_000_000L, result.spentBudget),
                () -> assertEquals(-2_000_000L, result.remainingBudget),
                () -> assertTrue(alerts.isEmpty(), "No alert should be emitted for unbudgeted campaign")
            );
        }
    }

    @Test
    @DisplayName("Scenario 4: Budget top-up uncaps previously CAPPED campaign back to ACTIVE")
    void shouldUncapCampaignOnBudgetTopUp() throws Exception {
        var env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        var initialBudget = new CampaignBudget();
        initialBudget.campaignId = "camp-4";
        initialBudget.advertiserId = "adv-4";
        initialBudget.allocatedBudget = 5_000_000L; // 5.00 EUR
        initialBudget.status = CampaignStatus.ACTIVE;
        initialBudget.updatedAt = 1000L;

        var cappingSpend = new CampaignSpendEvent();
        cappingSpend.timestamp = 2000L;
        cappingSpend.eventId = "clk-cap";
        cappingSpend.campaignId = "camp-4";
        cappingSpend.advertiserId = "adv-4";
        cappingSpend.cost = 5_200_000L; // 5.20 EUR -> CAPPED
        cappingSpend.type = SpendType.CLICK;

        var topUpBudget = new CampaignBudget();
        topUpBudget.campaignId = "camp-4";
        topUpBudget.advertiserId = "adv-4";
        topUpBudget.allocatedBudget = 20_000_000L; // 20.00 EUR
        topUpBudget.status = CampaignStatus.ACTIVE;
        topUpBudget.updatedAt = 3000L;

        var postTopUpSpend = new CampaignSpendEvent();
        postTopUpSpend.timestamp = 4000L;
        postTopUpSpend.eventId = "clk-after-topup";
        postTopUpSpend.campaignId = "camp-4";
        postTopUpSpend.advertiserId = "adv-4";
        postTopUpSpend.cost = 800_000L; // 0.80 EUR
        postTopUpSpend.type = SpendType.CLICK;

        try (
            var mainSink = new CollectingSink<CampaignLiveBudget>();
            var alertSink = new CollectingSink<BudgetExhaustedAlert>()
        ) {
            var budgetBroadcastStream = env.fromData(initialBudget, topUpBudget)
                .map(b -> {
                    if (b.allocatedBudget > 10_000_000L) {
                        Thread.sleep(60);
                    }
                    return b;
                })
                .returns(CampaignBudget.class)
                .broadcast(CampaignBudgetBroadcastProcessFunction.CAMPAIGN_BUDGET_STATE_DESCRIPTOR);

            var spendStream = env.fromData(cappingSpend, postTopUpSpend)
                .map(s -> {
                    if ("clk-after-topup".equals(s.eventId)) {
                        Thread.sleep(120);
                    }
                    return s;
                })
                .returns(CampaignSpendEvent.class)
                .keyBy(spend -> spend.campaignId);

            var budgetStream = spendStream.connect(budgetBroadcastStream)
                .process(new CampaignBudgetBroadcastProcessFunction());

            budgetStream.sinkTo(mainSink);
            budgetStream.getSideOutput(CampaignBudgetBroadcastProcessFunction.BUDGET_EXHAUSTED_TAG).sinkTo(alertSink);

            env.execute();

            var budgets = mainSink.elements();
            var alerts = alertSink.elements();

            assertEquals(2, budgets.size());
            assertEquals(1, alerts.size(), "Only one alert for initial capping");

            var firstResult = budgets.get(0);
            assertAll(
                () -> assertEquals("camp-4", firstResult.campaignId),
                () -> assertEquals(CampaignStatus.CAPPED, firstResult.status),
                () -> assertEquals(5_000_000L, firstResult.allocatedBudget),
                () -> assertEquals(5_200_000L, firstResult.spentBudget)
            );

            var secondResult = budgets.get(1);
            assertAll(
                () -> assertEquals("camp-4", secondResult.campaignId),
                () -> assertEquals(CampaignStatus.ACTIVE, secondResult.status, "Campaign must be uncapped back to ACTIVE"),
                () -> assertEquals(20_000_000L, secondResult.allocatedBudget),
                () -> assertEquals(6_000_000L, secondResult.spentBudget),
                () -> assertEquals(14_000_000L, secondResult.remainingBudget)
            );
        }
    }

    @Test
    @DisplayName("Scenario 5: Campaign dynamically suspended to PAUSED via CampaignBudget update")
    void shouldPauseCampaignDynamically() throws Exception {
        var env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        var pausedBudget = new CampaignBudget();
        pausedBudget.campaignId = "camp-5";
        pausedBudget.advertiserId = "adv-5";
        pausedBudget.allocatedBudget = 10_000_000L;
        pausedBudget.status = CampaignStatus.PAUSED;
        pausedBudget.updatedAt = 1000L;

        var spend = new CampaignSpendEvent();
        spend.timestamp = 2000L;
        spend.eventId = "imp-5";
        spend.campaignId = "camp-5";
        spend.advertiserId = "adv-5";
        spend.cost = 500_000L;
        spend.type = SpendType.IMPRESSION;

        try (var mainSink = new CollectingSink<CampaignLiveBudget>()) {
            var budgetBroadcastStream = env.fromData(pausedBudget)
                .broadcast(CampaignBudgetBroadcastProcessFunction.CAMPAIGN_BUDGET_STATE_DESCRIPTOR);

            var spendStream = env.fromData(spend)
                .map(s -> {
                    Thread.sleep(50);
                    return s;
                })
                .returns(CampaignSpendEvent.class)
                .keyBy(s -> s.campaignId);

            spendStream.connect(budgetBroadcastStream)
                .process(new CampaignBudgetBroadcastProcessFunction())
                .sinkTo(mainSink);

            env.execute();

            var budgets = mainSink.elements();
            assertEquals(1, budgets.size());

            var result = budgets.get(0);
            assertAll(
                () -> assertEquals("camp-5", result.campaignId),
                () -> assertEquals(CampaignStatus.PAUSED, result.status),
                () -> assertEquals(10_000_000L, result.allocatedBudget),
                () -> assertEquals(500_000L, result.spentBudget),
                () -> assertEquals(9_500_000L, result.remainingBudget)
            );
        }
    }
}

