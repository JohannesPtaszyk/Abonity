package dev.pott.abonity.core.local.settings

import assertk.assertThat
import assertk.assertions.isEqualTo
import dev.pott.abonity.core.entity.settings.Settings
import dev.pott.abonity.core.entity.settings.Theme
import dev.pott.abonity.core.entity.subscription.PaymentPeriod
import dev.pott.abonity.core.local.settings.datastore.entities.LocalTheme
import dev.pott.abonity.core.local.settings.datastore.entities.SettingsEntity
import dev.pott.abonity.core.local.subscription.db.entities.LocalPaymentPeriod
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class SettingsDataStoreDataSourceTest {

    @Test
    fun `GIVEN a settings WHEN updateSettings THEN update settings`() =
        runTest {
            val dataStore = FakeSettingsDataStore(SettingsEntity())

            val tested = SettingsDataStoreDataSource(dataStore)

            val newSettings = Settings(
                period = PaymentPeriod.DAYS,
                theme = Theme.LIGHT,
                enableAdaptiveColors = true,
            )
            tested.updateSettings(newSettings)

            assertThat(dataStore.settingsFlow.value).isEqualTo(
                SettingsEntity(
                    period = LocalPaymentPeriod.DAYS,
                    theme = LocalTheme.LIGHT,
                    enableAdaptiveColors = true,
                ),
            )
        }

    @Test
    fun `GIVEN a settings WHEN getSettingsFlow THEN return settings`() =
        runTest {
            val dataStore = FakeSettingsDataStore(
                SettingsEntity(
                    period = LocalPaymentPeriod.DAYS,
                    theme = LocalTheme.LIGHT,
                    enableAdaptiveColors = true,
                ),
            )

            val tested = SettingsDataStoreDataSource(dataStore)

            val result = tested.getSettingsFlow().first()

            assertThat(result).isEqualTo(
                Settings(
                    period = PaymentPeriod.DAYS,
                    theme = Theme.LIGHT,
                    enableAdaptiveColors = true,
                ),
            )
        }
}
