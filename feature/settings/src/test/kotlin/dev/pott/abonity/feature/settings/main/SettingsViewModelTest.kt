package dev.pott.abonity.feature.settings.main

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import dev.pott.abonity.common.test.CoroutinesTestExtension
import dev.pott.abonity.core.entity.settings.Theme
import dev.pott.abonity.core.entity.subscription.PaymentPeriod
import dev.pott.abonity.core.test.settings.FakeSettingsRepository
import dev.pott.abonity.core.test.settings.entities.createTestSettings
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(CoroutinesTestExtension::class)
class SettingsViewModelTest {

    @Test
    fun `GIVEN default settings WHEN setTheme to light THEN settings value is updated`() =
        runTest {
            val settings = createTestSettings()
            val repository = FakeSettingsRepository(settings)

            val tested = SettingsViewModel(repository)

            tested.state.test {
                runCurrent()
                tested.setTheme(Theme.LIGHT)
                runCurrent()

                assertThat(awaitItem()).isEqualTo(SettingsState())
                assertThat(awaitItem()).isEqualTo(SettingsState(settings))
                assertThat(awaitItem()).isEqualTo(SettingsState(settings.copy(theme = Theme.LIGHT)))
            }
        }

    @Test
    fun `GIVEN default settings WHEN setPeriod to years THEN settings value is updated`() {
        runTest {
            val settings = createTestSettings()
            val repository = FakeSettingsRepository(settings)

            val tested = SettingsViewModel(repository)

            tested.state.test {
                runCurrent()
                tested.setPeriod(PaymentPeriod.YEARS)
                runCurrent()

                assertThat(awaitItem()).isEqualTo(SettingsState())
                assertThat(awaitItem()).isEqualTo(SettingsState(settings))
                assertThat(awaitItem()).isEqualTo(
                    SettingsState(settings.copy(period = PaymentPeriod.YEARS)),
                )
            }
        }
    }

    @Test
    fun `GIVEN default settings WHEN enableAdaptiveColors THEN settings value is updated`() =
        runTest {
            val settings = createTestSettings()
            val repository = FakeSettingsRepository(settings)

            val tested = SettingsViewModel(repository)

            tested.state.test {
                runCurrent()
                tested.enableAdaptiveColors(true)
                runCurrent()

                assertThat(awaitItem()).isEqualTo(SettingsState())
                assertThat(awaitItem()).isEqualTo(SettingsState(settings))
                assertThat(
                    awaitItem(),
                ).isEqualTo(SettingsState(settings.copy(enableAdaptiveColors = true)))
            }
        }
}
