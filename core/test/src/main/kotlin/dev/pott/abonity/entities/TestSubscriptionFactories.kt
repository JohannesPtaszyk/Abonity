package dev.pott.abonity.entities

import dev.pott.abonity.core.entity.HexColor
import dev.pott.abonity.core.entity.PaymentInfo
import dev.pott.abonity.core.entity.Subscription
import io.github.serpro69.kfaker.Faker
import kotlinx.datetime.LocalDate
import java.util.Currency
import java.util.Locale
import kotlin.random.Random

fun createTestSubscription(
    id: Long = 1337,
    name: String = "Test Subscription",
    description: String = "Test Description",
    paymentInfo: PaymentInfo = createTestPaymentInfo(),
    color: HexColor = HexColor("#FFFFFF"),
): Subscription {
    return Subscription(
        id,
        name,
        description,
        paymentInfo,
        color
    )
}

fun createTestSubscriptionList(size: Int = 5): List<Subscription> {
    val random = Random(System.currentTimeMillis())
    val faker = Faker()
    return buildList {
        repeat(size) {
            val price = createTestPrice(
                value = random.nextDouble(),
                currency = Currency.getInstance(Locale.GERMANY)
            )
            val subscription = createTestSubscription(
                name = faker.company.name(),
                description = faker.subscription.statuses(),
                paymentInfo = createTestPaymentInfo(
                    price = price,
                    firstPayment = createRandomLocalDate(random)
                )
            )
            add(subscription)
        }
    }
}

private fun createRandomLocalDate(random: Random): LocalDate {
    val year = random.nextInt(1997, 2024)
    val month = random.nextInt(1, 13)
    val day = random.nextInt(1, 27)
    return LocalDate(year, month, day)
}
