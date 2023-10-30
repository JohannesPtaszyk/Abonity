package dev.pott.abonity.app.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.pott.abonity.core.local.db.AppDatabase
import dev.pott.abonity.core.local.db.entities.LocalPaymentPeriod
import dev.pott.abonity.core.local.db.entities.LocalPaymentType
import dev.pott.abonity.core.local.db.entities.SubscriptionEntity
import kotlinx.coroutines.runBlocking
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    @Suppress("LongMethod", "MagicNumber")
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "app-database",
        ).build().also {
            // TODO remove prepopulation/Move to debug flavor with file
            val dao = it.subscriptionDao()
            runBlocking {
                dao.upsertSubscription(
                    SubscriptionEntity(
                        0,
                        "Vodafone",
                        "Handy Vertrag",
                        49.99,
                        "EUR",
                        "2022-04-01",
                        LocalPaymentType.PERIODICALLY,
                        1,
                        LocalPaymentPeriod.MONTHS,
                    ),
                )
                dao.upsertSubscription(
                    SubscriptionEntity(
                        1,
                        "Vodafone",
                        "Internet & Telefon Vertrag",
                        9.99,
                        "EUR",
                        "2020-01-01",
                        LocalPaymentType.PERIODICALLY,
                        1,
                        LocalPaymentPeriod.MONTHS,
                    ),
                )
                dao.upsertSubscription(
                    SubscriptionEntity(
                        2,
                        "Miete",
                        "Volksbank Miete für Wohnung",
                        1299.0,
                        "EUR",
                        "1997-04-01",
                        LocalPaymentType.PERIODICALLY,
                        1,
                        LocalPaymentPeriod.MONTHS,
                    ),
                )
                dao.upsertSubscription(
                    SubscriptionEntity(
                        3,
                        "Brötchen",
                        "Ffürhstück füür die Arbeit im Büro",
                        4.78,
                        "EUR",
                        "2022-12-12",
                        LocalPaymentType.PERIODICALLY,
                        3,
                        LocalPaymentPeriod.DAYS,
                    ),
                )
                dao.upsertSubscription(
                    SubscriptionEntity(
                        4,
                        "Essen gehen",
                        "Date night mit Partner",
                        80.0,
                        "EUR",
                        "2020-02-02",
                        LocalPaymentType.PERIODICALLY,
                        2,
                        LocalPaymentPeriod.WEEKS,
                    ),
                )
                dao.upsertSubscription(
                    SubscriptionEntity(
                        5,
                        "Neues Macbook Pro",
                        "Ich brauche ein neues Macbook",
                        1999.99,
                        "USD",
                        "2021-12-12",
                        LocalPaymentType.ONE_TIME,
                        null,
                        null,
                    ),
                )
                dao.upsertSubscription(
                    SubscriptionEntity(
                        6,
                        "Dollar $",
                        "US Dollar",
                        80.0,
                        "USD",
                        "2020-02-02",
                        LocalPaymentType.PERIODICALLY,
                        1,
                        LocalPaymentPeriod.MONTHS,
                    ),
                )
                dao.upsertSubscription(
                    SubscriptionEntity(
                        7,
                        "Englando Pound",
                        "Komische Währung aus England",
                        99.99,
                        "GBP",
                        "2020-02-02",
                        LocalPaymentType.PERIODICALLY,
                        1,
                        LocalPaymentPeriod.MONTHS,
                    ),
                )
            }
        }
    }
}
