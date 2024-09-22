package dev.pott.abonity.core.domain.settings

import assertk.assertThat
import assertk.assertions.isEqualTo
import dev.pott.abonity.core.entity.subscription.PaymentPeriod
import dev.pott.abonity.core.test.settings.FakeSettingsLocalDataSource
import dev.pott.abonity.core.test.settings.entities.createTestSettings
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class SettingsRepositoryImplTest {

    @Test
    fun `GIVEN some settings stored in local data source WHEN retrieving settings flow THEN a flow with the stored settings should be emitted`() {
        runTest {
            val storedSettings = createTestSettings()
            val settingsLocalDataSource = FakeSettingsLocalDataSource(storedSettings)
            val settingsRepository = SettingsRepositoryImpl(settingsLocalDataSource)

            val settingsFlow = settingsRepository.getSettingsFlow()

            assertThat(settingsFlow.first()).isEqualTo(storedSettings)
        }
    }

    @Test
    fun `GIVEN some settings to be updated WHEN updating settings THEN the updated settings should be stored in local data source`() {
        runTest {
            val updatedSettings = createTestSettings()
            val settingsLocalDataSource = FakeSettingsLocalDataSource(updatedSettings)

            val tested = SettingsRepositoryImpl(settingsLocalDataSource)
            tested.updateSettings(updatedSettings)

            assertThat(settingsLocalDataSource.getSettingsFlow().first()).isEqualTo(updatedSettings)
        }
    }

    @Test
    fun `GIVEN some settings to be updated WHEN updating settings with block THEN the updated settings should be stored in local data source`() {
        runTest {
            val settings = createTestSettings()
            val settingsLocalDataSource = FakeSettingsLocalDataSource(settings)
            val tested = SettingsRepositoryImpl(settingsLocalDataSource)
            val expected = settings.copy(period = PaymentPeriod.YEARS)
            tested.updateSettings { expected }
            assertThat(settingsLocalDataSource.getSettingsFlow().first()).isEqualTo(expected)
        }
    }
}
