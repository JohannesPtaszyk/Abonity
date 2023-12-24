@file:Suppress("MagicNumber")

package dev.pott.abonity.app.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.pott.abonity.core.local.subscription.db.AppDatabase
import dev.pott.abonity.core.local.subscription.db.entities.LocalPaymentPeriod
import dev.pott.abonity.core.local.subscription.db.entities.LocalPaymentType
import dev.pott.abonity.core.local.subscription.db.entities.SubscriptionEntity
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Singleton
import kotlin.random.Random

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @OptIn(DelicateCoroutinesApi::class)
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "app-database",
        ).build().also { db ->
            GlobalScope.launch {
                generateSubscriptions(100).forEach {
                    db.subscriptionDao().upsertSubscription(it)
                }
            }
        }
    }
}

fun generateSubscriptions(n: Int): List<SubscriptionEntity> {
    val subscriptions = mutableListOf<SubscriptionEntity>()

    for (i in 0 until n) {
        val name = "Subscription #${i + 1}"
        val description = "Description for subscription ${i + 1}"
        val price = randomDouble()
        val currency = randomCurrency()
        val firstPaymentLocalDate = randomLocalDate()
        val paymentType = randomPaymentType()
        val period = randomPeriod()
        val periodCount = randomInt()

        val subscription = SubscriptionEntity(
            name = name,
            description = description,
            price = price,
            currency = currency,
            firstPaymentLocalDate = firstPaymentLocalDate,
            paymentType = paymentType,
            period = period,
            periodCount = periodCount,
            id = 0,
        )

        subscriptions.add(subscription)
    }

    return subscriptions
}

private fun randomDouble(): Double {
    return Random(0).nextDouble(0.0, 100.0)
}

private fun randomCurrency(): String {
    return listOf("USD", "EUR", "GBP", "CHF", "CAD")
        .random()
}

private fun randomLocalDate(): String {
    val minDate = "2023-01-01"
    val maxDate = "2023-12-31"
    val dateFormat = "yyyy-MM-dd"

    return randomString(dateFormat, minDate, maxDate)
}

private fun randomPaymentType(): LocalPaymentType {
    return listOf(
        LocalPaymentType.ONE_TIME,
        LocalPaymentType.PERIODICALLY,
    )
        .random()
}

private fun randomPeriod(): LocalPaymentPeriod {
    return listOf(
        LocalPaymentPeriod.DAYS,
        LocalPaymentPeriod.WEEKS,
        LocalPaymentPeriod.MONTHS,
        LocalPaymentPeriod.YEARS,
    ).random()
}

private fun randomInt(): Int {
    return Random(0).nextInt(1, 100)
}

@Suppress("SameParameterValue", "RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
private fun randomString(format: String, minDate: String, maxDate: String): String {
    val formatter = SimpleDateFormat(format, Locale.getDefault())
    val randomDate =
        Date(randomBetween(formatter.parse(minDate).time, formatter.parse(maxDate).time))

    return formatter.format(randomDate)
}

private fun randomBetween(min: Long, max: Long): Long {
    return Random.nextLong(min, max + 1)
}
