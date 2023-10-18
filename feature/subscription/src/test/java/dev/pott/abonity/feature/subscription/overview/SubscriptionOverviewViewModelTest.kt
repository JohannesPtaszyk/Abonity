package dev.pott.abonity.feature.subscription.overview

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import dev.pott.abonity.FakeClock
import dev.pott.abonity.FakeSubscriptionRepository
import dev.pott.abonity.common.test.CoroutinesTestExtension
import dev.pott.abonity.core.domain.PeriodicPriceCalculator
import dev.pott.abonity.core.entity.PaymentInfo
import dev.pott.abonity.core.entity.PaymentPeriod
import dev.pott.abonity.core.entity.PaymentType
import dev.pott.abonity.core.entity.Price
import dev.pott.abonity.entities.createTestSubscription
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.Currency

@ExtendWith(CoroutinesTestExtension::class)
class SubscriptionOverviewViewModelTest {

    @Test
    fun `GIVEN local subscriptions WHEN initializing THEN return list of overview items`() {
        runTest {
            val subscription = createTestSubscription(
                paymentInfo = PaymentInfo(
                    price = Price(1.0, Currency.getInstance("EUR")),
                    firstPayment = LocalDate(2021, 1, 1),
                    type = PaymentType.Periodic(2, PaymentPeriod.DAYS)
                ),
            )
            val localSubscriptionFlow = flowOf(listOf(subscription))
            val repository = FakeSubscriptionRepository(localSubscriptionFlow)
            val today = Instant.parse("2021-03-01T00:00:00Z")
            val clock = FakeClock(today)
            val calculator = PeriodicPriceCalculator(clock)

            val tested = SubscriptionOverviewViewModel(repository, calculator)

            val expectedSubscriptions = listOf(
                SubscriptionOverviewItem(
                    subscription = subscription,
                    periodPrice = Price(15.0, Currency.getInstance("EUR"))
                )
            )

            tested.state.test {
                assertThat(awaitItem()).isEqualTo(SubscriptionOverviewState())
                assertThat(awaitItem()).isEqualTo(SubscriptionOverviewState(subscriptions = expectedSubscriptions))
            }
        }
    }
}
